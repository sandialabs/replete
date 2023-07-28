package replete.pipeline.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import replete.pipeline.Stage;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.PipelineValidationException;

// Stage graph vertex object
public class StageNode {


    ////////////
    // FIELDS //
    ////////////

    // Payload
    private Stage stage;

    // Input/output port graph vertex objects
    private List<InputDescriptorNode> inputNodes = new ArrayList<>();
    private List<OutputDescriptorNode> outputNodes = new ArrayList<>();

    // Payload object => graph vertex object mappings
    private Map<InputDescriptor, InputDescriptorNode> inputNodeMap = new LinkedHashMap<>();
    private Map<OutputDescriptor, OutputDescriptorNode> outputNodeMap = new LinkedHashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StageNode(Stage stage) {
        this.stage = stage;

        // Construct the appropriate input port graph vertex objects
        // given the stage payload.
        for(InputDescriptor descriptor : stage.getInputDescriptors()) {
            if(inputNodeMap.containsKey(descriptor)) {
                throw new PipelineValidationException("Duplicate input descriptor detected.");
            }
            InputDescriptorNode node = new InputDescriptorNode(this, descriptor);
            inputNodes.add(node);
            inputNodeMap.put(descriptor, node);
        }

        // Construct the appropriate output port graph vertex objects
        // given the stage payload.
        for(OutputDescriptor descriptor : stage.getOutputDescriptors()) {
            if(outputNodeMap.containsKey(descriptor)) {
                throw new PipelineValidationException("Duplicate output descriptor detected.");
            }
            OutputDescriptorNode node = new OutputDescriptorNode(this, descriptor);
            outputNodes.add(node);
            outputNodeMap.put(descriptor, node);
        }
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Stage getStage() {
        return stage;
    }

    public List<InputDescriptorNode> getInputNodeMap() {
        return Collections.unmodifiableList(inputNodes);
    }
    public List<OutputDescriptorNode> getOutputNodeMap() {
        return Collections.unmodifiableList(outputNodes);
    }

    // [OPTION] Throw an exception if the descriptor is not found
    public InputDescriptorNode getInputNode(InputDescriptor descriptor) {
        return inputNodeMap.get(descriptor);
    }
    public OutputDescriptorNode getOutputNode(OutputDescriptor descriptor) {
        return outputNodeMap.get(descriptor);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "StageNode [stage=" + stage + "]";
    }
}
