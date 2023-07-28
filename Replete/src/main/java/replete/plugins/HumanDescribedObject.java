package replete.plugins;

import java.io.Serializable;

public class HumanDescribedObject implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private HumanDescriptor descriptor;          // Will not be null, but the instance's fields can be
    private Object object;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public HumanDescriptor getDescriptor() {
        return descriptor;
    }
    public Object getObject() {
        return object;
    }

    // Mutators

    public HumanDescribedObject setDescriptor(HumanDescriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }
    public HumanDescribedObject setObject(Object object) {
        this.object = object;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((descriptor == null) ? 0 : descriptor.hashCode());
        result = prime * result + ((object == null) ? 0 : object.hashCode());
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
        HumanDescribedObject other = (HumanDescribedObject) obj;
        if(descriptor == null) {
            if(other.descriptor != null) {
                return false;
            }
        } else if(!descriptor.equals(other.descriptor)) {
            return false;
        }
        if(object == null) {
            if(other.object != null) {
                return false;
            }
        } else if(!object.equals(other.object)) {
            return false;
        }
        return true;
    }
}
