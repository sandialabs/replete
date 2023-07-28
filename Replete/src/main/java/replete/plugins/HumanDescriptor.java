package replete.plugins;

import java.io.Serializable;

import javax.swing.Icon;

public class HumanDescriptor implements HumanDescribable, Serializable {


    ////////////
    // FIELDS //
    ////////////

    private String name;              // Can be null or blank (optional arbitrary user text)
    private String description;       // Can be null or blank (optional arbitrary user text)
    //private <IconSpecifier> icon;   // Requires a dependable referencing scheme first


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HumanDescriptor() {
        // Leave fields null
    }
    public HumanDescriptor(HumanDescriptor descriptor) {
        this(descriptor.getName(), descriptor.getDescription());
    }
    public HumanDescriptor(String name, String description) {
        this.name = name;
        this.description = description;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public Icon getIcon() {
        return null;                 // Descriptor
    }

    // Mutators

    public HumanDescriptor setName(String name) {
        this.name = name;
        return this;
    }
    public HumanDescriptor setDescription(String description) {
        this.description = description;
        return this;
    }
    // No setIcon(...)


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        HumanDescriptor other = (HumanDescriptor) obj;
        if(description == null) {
            if(other.description != null) {
                return false;
            }
        } else if(!description.equals(other.description)) {
            return false;
        }
        if(name == null) {
            if(other.name != null) {
                return false;
            }
        } else if(!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HumanDescriptor [name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append("]");
        return builder.toString();
    }
}
