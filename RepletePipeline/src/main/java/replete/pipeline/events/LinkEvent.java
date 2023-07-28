package replete.pipeline.events;

import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;

public class LinkEvent {


    ////////////
    // FIELDS //
    ////////////

    private InputDescriptor inputDescriptor;
    private OutputDescriptor outputDescriptor;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public LinkEvent(OutputDescriptor outputDescriptor, InputDescriptor inputDescriptor) {
        this.outputDescriptor = outputDescriptor;
        this.inputDescriptor = inputDescriptor;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public InputDescriptor getInputDescriptor() {
        return inputDescriptor;
    }
    public OutputDescriptor getOutputDescriptor() {
        return outputDescriptor;
    }
}
