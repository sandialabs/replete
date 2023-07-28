package replete.pipeline.graph;

import java.util.ArrayList;
import java.util.List;

import replete.pipeline.desc.OutputDescriptor;

public class OutputDescriptorNode {


    ////////////
    // FIELDS //
    ////////////

    private StageNode parent;
    private List<InputDescriptorNode> edges = new ArrayList<>();
    private OutputDescriptor descriptor;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public OutputDescriptorNode(StageNode parent, OutputDescriptor descriptor) {
        this.parent = parent;
        this.descriptor = descriptor;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public StageNode getParent() {
        return parent;
    }
    public List<InputDescriptorNode> getEdges() {
        return edges;
    }
    public OutputDescriptor getDescriptor() {
        return descriptor;
    }

    // Accessors (Computed)

    public boolean hasEdge(InputDescriptorNode node) {
        return getEdgePosition(node) != -1;
    }
    public int getEdgePosition(InputDescriptorNode node) {
        return edges.indexOf(node);
    }

    // Mutators

    public void addEdge(InputDescriptorNode node) {
        edges.add(node);
    }
    public void removeEdge(InputDescriptorNode node) {
        edges.remove(node);
    }


    //////////////
    // toString //
    //////////////

    @Override
    public String toString() {
        return "OutputDescriptorNode [edges=" + edges + ", descriptor=" + descriptor + "]";
    }
}
