package replete.collections;

import java.io.Serializable;

/*
 * An unordered version of the Pair class.
 */
public class UPair<V1, V2> implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private V1 value1;
    private V2 value2;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public UPair(V1 value1, V2 value2) {
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
        int v2 = (value2 == null) ? 0 : value2.hashCode();
        int v1 = (value1 == null) ? 0 : value1.hashCode();
        if (v1 < v2) {
            int tmp = v1;
            v1 = v2;
            v2 = tmp;
        }
        int result = 1;
        result = prime * result + v1;
        result = prime * result + v2;
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
        UPair other = (UPair) obj;
        if (value1 == null && other.value1 == null) {
            return (other.value1 == null) && (other.value2 == null);
        } else if (value1 != null && value2 == null) {
            return (value1.equals(other.value1) && (other.value2 == null)) ||
                    (value1.equals(other.value2) && (other.value1 == null));
        } else if (value1 == null && value2 != null) {
            return (value2.equals(other.value1) && (other.value2 == null)) ||
                    (value2.equals(other.value2) && (other.value1 == null));
        } else {
            return (value1.equals(other.value1) && value2.equals(other.value2)) ||
                   (value1.equals(other.value2) && value2.equals(other.value1));
        }
    }
}
