package replete.pipeline.graph;

import java.util.ArrayList;
import java.util.List;

import replete.pipeline.desc.InputDescriptor;

public class InputDescriptorNode {


    ////////////
    // FIELDS //
    ////////////

    private StageNode parent;
    private List<OutputDescriptorNode> edges = new ArrayList<>();
    private InputDescriptor descriptor;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public InputDescriptorNode(StageNode parent, InputDescriptor descriptor) {
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
    public List<OutputDescriptorNode> getEdges() {
        return edges;
    }
    public InputDescriptor getDescriptor() {
        return descriptor;
    }

    // Accessors (Computed)

    public boolean hasEdge(OutputDescriptorNode node) {
        return getEdgePosition(node) != -1;
    }
    public int getEdgePosition(OutputDescriptorNode node) {
        return edges.indexOf(node);
    }

    // MIN = MAX
    //   3 states   UNSATIS SATIS/AVAILABLE/MAX OVER
    // MIN < MAX
    //   5 states   UNSATIS SATIS AVAILABLE MAX OVER

    public InputDescriptorNodeStatus getStatus() {
        InputDescriptor id = getDescriptor();
        int eg = getEdges().size();
        int min = id.getCardinalityMinimum();
        int max = id.getCardinalityMaximum();

        if(min > max) {
            return InputDescriptorNodeStatus.INVALID;

        } else if(min == max) {
            if(eg < min) {
                return InputDescriptorNodeStatus.UNSATISFIED;
            } else if(eg == min) {
                return InputDescriptorNodeStatus.SATISFIED_FULL;
            }
            return InputDescriptorNodeStatus.OVERCONNECTED;

        } else {             // min < max
            if(eg < min) {
                return InputDescriptorNodeStatus.UNSATISFIED;
            } else if(eg == min) {
                return InputDescriptorNodeStatus.SATISFIED_AVAILABLE;
            } else if(eg < max) {
                return InputDescriptorNodeStatus.SATISFIED_AVAILABLE;  // eg > min
            } else if(eg == max) {
                return InputDescriptorNodeStatus.SATISFIED_FULL;
            }
            return InputDescriptorNodeStatus.OVERCONNECTED;
        }
    }

    // Mutators

    public void addEdge(OutputDescriptorNode node) {
        edges.add(node);
    }
    public void removeEdge(OutputDescriptorNode node) {
        edges.remove(node);
    }


    //////////////
    // toString //
    //////////////

    @Override
    public String toString() {
        return "InputDescriptorNode [edges=" + edges + ", descriptor=" + descriptor + "]";
    }
}
