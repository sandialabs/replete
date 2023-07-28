package replete.pipeline.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import replete.event.ExtChangeNotifier;
import replete.pipeline.Stage;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.graph.DuplicateLinkException;
import replete.pipeline.errors.graph.GraphException;
import replete.pipeline.errors.graph.InvalidLinkException;
import replete.pipeline.errors.graph.LinkCycleException;
import replete.pipeline.events.LinkEvent;
import replete.pipeline.events.LinkListener;
import replete.text.StringUtil;

// This class combines a traditional graph layer with the specific
// user object layer pertinent to a graph pipeline.  This is clear
// due to the explicit mention of the payload object type through-
// out (i.e. Stage)
public class StageGraph {


    ////////////
    // FIELDS //
    ////////////

    // TODO: synchronization?
    private Map<Stage, StageNode> stageNodes = new LinkedHashMap<>();
    private Map<String, StageNode> stageNodeIds = new LinkedHashMap<>();
    private Map<StageNode, Set<StageNode>> dependencies = new LinkedHashMap<>();


    ////////////
    // STAGES //
    ////////////

    public int getStageCount() {
        return stageNodes.size();
    }
    public List<Stage> getStages() {
        return new ArrayList<>(stageNodes.keySet());
    }
    public Stage getStageById(String stageId) {
        StageNode node = stageNodeIds.get(stageId);
        if(node != null) {
            return node.getStage();
        }
        return null;
    }
    public boolean hasStage(Stage stage) {
        return stageNodes.containsKey(stage);
    }
    public void addStage(Stage stage) throws GraphException {

        // Check if the stage already exists.
        if(hasStage(stage)) {
            throw new GraphException("The graph already contains the stage '" + stage + "'.");
        }

        // Register the new stage.
        StageNode node = new StageNode(stage);
        stageNodes.put(stage, node);
        stageNodeIds.put(stage.getId(), node);
    }
    public boolean removeStage(Stage removeStage) {
        StageNode removeNode = stageNodes.get(removeStage);

        // If the stage was in this graph, then remove it
        if(removeNode != null) {

            // Remove all parent nodes' links to this node and update
            // their open status.
            for(InputDescriptorNode inNode : removeNode.getInputNodeMap()) {
                for(OutputDescriptorNode outNode : inNode.getEdges()) {
                    outNode.removeEdge(inNode);
                }
            }

            // Remove all child node links to this node and update their
            // open status.
            for(OutputDescriptorNode outNode : removeNode.getOutputNodeMap()) {
                for(InputDescriptorNode inNode : outNode.getEdges()) {
                    inNode.removeEdge(outNode);
                }
            }

            // Remove the stage and its corresponding node from all
            // internal data structures.
            stageNodes.remove(removeStage);
            stageNodeIds.remove(removeStage.getId());
            dependencies.remove(removeNode);           // Test this dep removal
            for(Set<StageNode> dependents : dependencies.values()) {
                dependents.remove(removeNode);
            }

            // The stage was successfully removed from the graph.
            return true;
        }

        // The stage was not in the graph to begin with.
        return false;
    }


    ///////////
    // LINKS //
    ///////////

    public boolean hasLink(InputDescriptor in) throws InvalidLinkException {
        StageNode inStageNode = stageNodes.get(in.getParent());
        checkInputDescriptor(in, inStageNode);
        InputDescriptorNode inNode = inStageNode.getInputNode(in);
        return !inNode.getEdges().isEmpty();
    }
    public boolean hasLink(OutputDescriptor out) throws InvalidLinkException {
        StageNode outStageNode = stageNodes.get(out.getParent());
        checkOutputDescriptor(out, outStageNode);
        OutputDescriptorNode outNode = outStageNode.getOutputNode(out);
        return !outNode.getEdges().isEmpty();
    }
    public boolean hasLink(OutputDescriptor out, InputDescriptor in) throws InvalidLinkException {
        StageNode outStageNode = stageNodes.get(out.getParent());
        StageNode inStageNode = stageNodes.get(in.getParent());

        checkOutputDescriptor(out, outStageNode);
        checkInputDescriptor(in, inStageNode);

        OutputDescriptorNode outNode = outStageNode.getOutputNode(out);
        InputDescriptorNode inNode = inStageNode.getInputNode(in);

        return outNode.hasEdge(inNode) || inNode.hasEdge(outNode);
    }

    public void addLink(OutputDescriptor out, InputDescriptor in) throws InvalidLinkException {
        StageNode outStageNode = stageNodes.get(out.getParent());
        StageNode inStageNode = stageNodes.get(in.getParent());

        checkOutputDescriptor(out, outStageNode);
        checkInputDescriptor(in, inStageNode);
        checkTypeAndCycle(out, in, outStageNode, inStageNode);

        OutputDescriptorNode outNode = outStageNode.getOutputNode(out);
        InputDescriptorNode inNode = inStageNode.getInputNode(in);

        checkExistingLink(out, in, outNode, inNode);
        checkInputCardinality(in, inNode);

        outNode.addEdge(inNode);
        inNode.addEdge(outNode);

        // TEST
        // TODO JTM - DMT FIX THIS
//        Object outputValue = outStageNode.getStage().getOutput(out.getName());
//        in.getParent().addInputMulti(in.getName(), outputValue);

        // TODO: someone (graph or pipeline) needs to push the output value
        // on the output descriptor (IF there is one) to the input values
        // of the input descriptor to end end of the input multi list.

        LinkEvent event = new LinkEvent(out, in);
        fireLinkAddedNotifier(event);
    }

    public boolean canLink(OutputDescriptor out, InputDescriptor in) {
        StageNode outStageNode = stageNodes.get(out.getParent());
        StageNode inStageNode = stageNodes.get(in.getParent());

        try {
            checkOutputDescriptor(out, outStageNode);
            checkInputDescriptor(in, inStageNode);
            checkTypeAndCycle(out, in, outStageNode, inStageNode);

            OutputDescriptorNode outNode = outStageNode.getOutputNode(out);
            InputDescriptorNode inNode = inStageNode.getInputNode(in);

            checkExistingLink(out, in, outNode, inNode);
            checkInputCardinality(in, inNode);

        } catch(InvalidLinkException e) {
            return false;
        }
        return true;
    }

    public void removeLink(OutputDescriptor out, InputDescriptor in) throws InvalidLinkException {
        StageNode outNode = stageNodes.get(out.getParent());
        StageNode inNode = stageNodes.get(in.getParent());

        checkOutputDescriptor(out, outNode);
        checkInputDescriptor(in, inNode);

        OutputDescriptorNode outDesc = outNode.getOutputNode(out);
        if(outDesc == null) {
            return;
        }
        InputDescriptorNode inDesc = inNode.getInputNode(in);
        if(inDesc == null) {
            return;
        }

        outDesc.removeEdge(inDesc);
        int pos = inDesc.getEdgePosition(outDesc);
        inDesc.removeEdge(outDesc);     // TODO [DMT] need to remove appropriate input value

        // TODO need to test this!!!  was in the middle of this...
        // and testing removeLink
        if(pos < in.getParent().getInputMulti(in.getName()).size()) {
            in.getParent().removeInputMulti(in.getName(), pos);
        }

        LinkEvent event = new LinkEvent(out, in);
        fireLinkRemovedNotifier(event);
    }

    private Set<StageNode> findStageNodes(InputDescriptorNodeStatus status) {
        Set<StageNode> result = new HashSet<>();
        for(StageNode node : stageNodes.values()) {
            for(InputDescriptorNode inNode : node.getInputNodeMap()) {
                if(inNode.getStatus() == status) {
                    result.add(node);
                    break;
                }
            }
        }
        return result;
    }

    // public boolean canDependency?
    public void addDependency(Stage from, Stage to) {
        StageNode fromNode = stageNodes.get(from);
        StageNode toNode = stageNodes.get(to);
        if(fromNode == null) {
            throw new InvalidLinkException("Stage type '" + from.getClass() +
                "' with ID '" + from.getId() + "' was not found in the graph.");
        }
        if(toNode == null) {
            throw new InvalidLinkException("Stage type '" + to.getClass() +
                "' with ID '" + to.getId() + "' was not found in the graph.");
        }
        checkCycleOnly(fromNode, toNode);
        Set<StageNode> dependents = dependencies.get(fromNode);
        if(dependents == null) {
            dependents = new HashSet<>();
            dependencies.put(fromNode, dependents);
        }
        dependents.add(toNode);
    }


    ////////////
    // CYCLES //
    ////////////

    public boolean checkCycle(Stage out, Stage in) {
        return checkCycle(stageNodes.get(out), stageNodes.get(in));
    }

    private boolean checkCycle(StageNode out, StageNode in) {
        Set<StageNode> visited = new HashSet<>();
        visited.add(out);
        return checkCycle(in, visited);
    }

    private boolean checkCycle(StageNode current, Set<StageNode> visited) {
        if(visited.contains(current)) {
            return true;
        }
        visited.add(current);
        boolean result = false;
        for(OutputDescriptorNode outNode : current.getOutputNodeMap()) {
            for(InputDescriptorNode inNode : outNode.getEdges()) {
                result = result || checkCycle(inNode.getParent(), visited);
            }
        }
        if(dependencies.containsKey(current)) {
            for(StageNode dependent : dependencies.get(current)) {
                result = result || checkCycle(dependent, visited);
            }
        }
        visited.remove(current);
        return result;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public List<Stage> getSourceStages() {
        List<Stage> stages = new ArrayList<>();
        for(StageNode node : findStageNodes(InputDescriptorNodeStatus.UNSATISFIED)) {
            stages.add(node.getStage());
        }
        return stages;
    }
    public List<InputDescriptor> getInputLinks(OutputDescriptor output) {
        StageNode node = stageNodes.get(output.getParent());
        checkOutputDescriptor(output, node);
        OutputDescriptorNode outNode = node.getOutputNode(output);
        List<InputDescriptor> results = new ArrayList<>();
        for(InputDescriptorNode inputNode : outNode.getEdges()) {
            results.add(inputNode.getDescriptor());
        }
        return results;
    }
    public List<OutputDescriptor> getOutputLinks(InputDescriptor input) {
        StageNode node = stageNodes.get(input.getParent());
        checkInputDescriptor(input, node);
        InputDescriptorNode inNode = node.getInputNode(input);
        List<OutputDescriptor> results = new ArrayList<>();
        for(OutputDescriptorNode outputNode : inNode.getEdges()) {
            results.add(outputNode.getDescriptor());
        }
        return results;
    }
    public List<Stage> getChildren(Stage stage) {
        List<Stage> children = new ArrayList<>();
        StageNode node = stageNodes.get(stage);
        if(node == null) {
            return children;   // TODO [DMT] is this case possible?
        }
        for(OutputDescriptorNode outNode : node.getOutputNodeMap()) {
            for(InputDescriptorNode inNode : outNode.getEdges()) {
                if(!children.contains(inNode.getParent().getStage())) {
                    children.add(inNode.getParent().getStage());
                }
            }
        }
        return children;
    }
    public List<Stage> getParents(Stage stage) {
        List<Stage> parents = new ArrayList<>();
        StageNode node = stageNodes.get(stage);
        for(InputDescriptorNode inNode : node.getInputNodeMap()) {
            for(OutputDescriptorNode outNode : inNode.getEdges()) {
                if(!parents.contains(outNode.getParent().getStage())) {
                    parents.add(outNode.getParent().getStage());
                }
            }
        }
        return Collections.unmodifiableList(parents);
    }
    public InputDescriptorNodeStatus getStatus(InputDescriptor input) {
        StageNode node = stageNodes.get(input.getParent());
        checkInputDescriptor(input, node);
        InputDescriptorNode inNode = node.getInputNode(input);
        return inNode.getStatus();
    }


    ////////////
    // CHECKS //
    ////////////

    // TODO: The descriptor's parents shouldn't have to be DIRECTLY in this
    // graph, but rather just have this pipeline as its ancestor.... think
    // about this.
    private void checkOutputDescriptor(OutputDescriptor out, StageNode outNode) throws InvalidLinkException {
        if(outNode == null) {
            throw new InvalidLinkException("Stage type '" + out.getParent().getClass() +
                "' with ID '" + out.getParent().getId() + "' was not found in the graph.");
        }
        OutputDescriptorNode od = outNode.getOutputNode(out);
        if(od == null) {
            throw new InvalidLinkException("Output descriptor '" + out + "' not found.");
        }
    }
    private void checkInputDescriptor(InputDescriptor in, StageNode inNode) throws InvalidLinkException {
        if(inNode == null) {
            throw new InvalidLinkException("Stage type '" + in.getParent().getClass() +
                "' with ID '" + in.getParent().getId() + "' was not found in the graph.");
        }
        InputDescriptorNode id = inNode.getInputNode(in);
        if(id == null) {
            throw new InvalidLinkException("Input descriptor '" + in + "' not found.");
        }
    }
    private void checkTypeAndCycle(OutputDescriptor out, InputDescriptor in, StageNode outNode, StageNode inNode) throws InvalidLinkException {
        if(!in.getType().isAssignableFrom(out.getType())) {
            if(!out.getType().isAssignableFrom(in.getType())) {
                throw new InvalidLinkException("Output type '" + out.getType() +
                    "' does not match input type '" + in.getType() + "'");
            }
        }
        if(checkCycle(outNode, inNode)) {
            throw new LinkCycleException("Cycle detected for link between " + out + " and " + in);
        }
    }
    private void checkCycleOnly(StageNode outNode, StageNode inNode) throws InvalidLinkException {
        if(checkCycle(outNode, inNode)) {
            throw new LinkCycleException("Cycle detected for dependency between " +
                outNode.getStage() + " and " + inNode.getStage());
        }
    }
    private void checkExistingLink(OutputDescriptor out, InputDescriptor in,
                                   OutputDescriptorNode outNode, InputDescriptorNode inNode) {
        if(inNode.getEdges().contains(outNode) || outNode.getEdges().contains(inNode)) {
            throw new DuplicateLinkException("A link already exists between '" +
                out + "' and '" + in + "'.");
        }
    }
    private void checkInputCardinality(InputDescriptor in, InputDescriptorNode inNode) {
        if(inNode.getEdges().size() == in.getCardinalityMaximum()) {
            throw new InvalidLinkException("Cannot add link to '" + in +
                "' as it already has the maximum number of inputs.");
        }
    }


    ////////////
    // DEPTHS //
    ////////////

    private Map<Stage, Integer> getMaxDepths(Set<StageNode> nodes) {
        Map<StageNode, Integer> depths = new LinkedHashMap<>();
        for(StageNode open : nodes) {
            getMaxDepths(open, 0, depths);
        }
        Map<Stage, Integer> result = new LinkedHashMap<>();
        for(Entry<StageNode, Integer> entry : depths.entrySet()) {
            result.put(entry.getKey().getStage(), entry.getValue());
        }
        return result;
    }
    public Map<Stage, Integer> getMaxDepthsSources() {
        return getMaxDepths(findStageNodes(InputDescriptorNodeStatus.UNSATISFIED));
    }

    public Map<Stage, Integer> getMaxDepths(String stageId) {
        StageNode start = stageNodeIds.get(stageId);
        Map<StageNode, Integer> depths = new LinkedHashMap<>();
        getMaxDepths(start, 0, depths);
        Map<Stage, Integer> result = new LinkedHashMap<>();
        for(Entry<StageNode, Integer> entry : depths.entrySet()) {
            result.put(entry.getKey().getStage(), entry.getValue());
        }
        return result;
    }

    private void getMaxDepths(StageNode node, int depth, Map<StageNode, Integer> depths) {
        Integer lastDepth = depths.get(node);
        if(lastDepth == null || lastDepth < depth) {
            depths.put(node, depth);
        }
        for(OutputDescriptorNode output : node.getOutputNodeMap()) {
            for(InputDescriptorNode input : output.getEdges()) {
                getMaxDepths(input.getParent(), depth + 1, depths);
            }
        }
        if(dependencies.containsKey(node)) {
            for(StageNode dependent : dependencies.get(node)) {
                getMaxDepths(dependent, depth + 1, depths);
            }
        }
    }


    ///////////
    // DEBUG //
    ///////////

    public void walkGraph() {
        for(StageNode node : findStageNodes(InputDescriptorNodeStatus.UNSATISFIED)) {
            walkGraph(node, "", 0);
        }
    }
    private void walkGraph(StageNode node, String via, int level) {
        String indent = StringUtil.spaces(level * 4);
        if(findStageNodes(InputDescriptorNodeStatus.UNSATISFIED).contains(node)) {
            System.out.println(indent + "Open: " + via + node);
        }
//        else if(sinks.contains(node)) {
//            System.out.println(indent + "Sink: " + node);
//        }
        else {
            System.out.println(indent + "Middle: " + via + node);
        }
        if(node.getOutputNodeMap().isEmpty()) {
            return;
        }
        for(OutputDescriptorNode outNode : node.getOutputNodeMap()) {
            for(InputDescriptorNode inNode : outNode.getEdges()) {
                String via2 = "[" + outNode.getDescriptor().getName() +
                    "->" + inNode.getDescriptor().getName() + "] ";
                walkGraph(inNode.getParent(), via2, level + 1);
            }
        }
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    // Link added

    private ExtChangeNotifier<LinkListener> linkAddedNotifier = new ExtChangeNotifier<>();
    public void addLinkAddedListener(LinkListener listener) {
        linkAddedNotifier.addListener(listener);
    }
    public void removeLinkAddedListener(LinkListener listener) {
        linkAddedNotifier.removeListener(listener);
    }
    protected void fireLinkAddedNotifier(LinkEvent e) {
        linkAddedNotifier.fireStateChanged(e);
    }

    // Link removed

    private ExtChangeNotifier<LinkListener> linkRemovedNotifier = new ExtChangeNotifier<>();
    public void addLinkRemovedListener(LinkListener listener) {
        linkRemovedNotifier.addListener(listener);
    }
    public void removeLinkRemovedListener(LinkListener listener) {
        linkRemovedNotifier.removeListener(listener);
    }
    protected void fireLinkRemovedNotifier(LinkEvent e) {
        linkRemovedNotifier.fireStateChanged(e);
    }
}
