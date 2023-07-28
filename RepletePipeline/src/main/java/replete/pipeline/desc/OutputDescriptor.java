package replete.pipeline.desc;

import replete.pipeline.Pipeline;
import replete.pipeline.Stage;

public class OutputDescriptor extends PortDescriptor {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public OutputDescriptor(Stage parent, String name, String friendlyName,
            String description, Class<?> type) {
        super(parent, name, friendlyName, description, type);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    public String toStringComplete() {
        return "OutputDescriptor [parentId=" + parent.getId() + ", name=" + name + ", friendlyName=" +
               friendlyName + ", description=" + description + ", type=" + type + "]";
    }
    @Override
    public String toString() {
        return parent.getName() + Pipeline.PIPELINE_NAME_SEPARATOR + name;
    }
}
