package replete.collections;
import java.io.Serializable;

public class IdentityPair<V1, V2> implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private V1 value1;
    private V2 value2;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public IdentityPair(V1 value1, V2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public V1 getValue1() {
        return value1;
    }
    public V2 getValue2() {
        return value2;
    }

    // Mutators

    public void setValue1(V1 value1) {
        this.value1 = value1;
    }
    public void setValue2(V2 value2) {
        this.value2 = value2;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "Pair [value1=" + value1 + ", value2=" + value2 + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value1 == null) ? 0 : System.identityHashCode(value1));
        result = prime * result + ((value2 == null) ? 0 : System.identityHashCode(value2));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IdentityPair other = (IdentityPair) obj;
        if (value1 == null) {
            if (other.value1 != null) {
                return false;
            }
        } else if (value1 != other.value1) {
            return false;
        }
        if (value2 == null) {
            if (other.value2 != null) {
                return false;
            }
        } else if (value2 != other.value2) {
            return false;
        }
        return true;
    }
}

