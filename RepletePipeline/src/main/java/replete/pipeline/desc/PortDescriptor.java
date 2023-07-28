package replete.pipeline.desc;

import java.io.Serializable;

import replete.pipeline.Pipeline;
import replete.pipeline.Stage;

public abstract class PortDescriptor implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    protected Stage parent;
    protected String name;
    protected String friendlyName;
    protected String description;
    protected Class<?> type;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PortDescriptor(Stage parent, String name, String friendlyName, String description, Class<?> type) {
        if(parent == null) {
            throw new IllegalArgumentException("Port descriptor must have a parent stage.");
        }
        // [OPTION] Consider name character restrictions as well.
        if(name == null || name.trim().equals("")) {
            throw new IllegalArgumentException("Port descriptor must have a name.");
        }
        if(type == null) {
            throw new IllegalArgumentException("Port descriptor must have a type.  Specify Object.class to have no type restriction on the port.");
        }
        this.parent = parent;
        this.name = name;
        this.friendlyName = friendlyName;
        this.description = description;
        this.type = type;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Stage getParent() {
        return parent;
    }
    public String getName() {
        return name;
    }
    public String getFriendlyName() {
        return friendlyName;
    }
    public String getDescription() {
        return description;
    }
    public Class<?> getType() {
        return type;
    }

    // Accessors (computed)

    public String getQualifiedName() {
        return parent.getName() + Pipeline.PIPELINE_NAME_SEPARATOR + name;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "PortDescriptor [parentId=" + parent.getId() + ", name=" + name + ", friendlyName="
                + friendlyName + ", description=" + description + ", type=" + type + "]";
    }
}
