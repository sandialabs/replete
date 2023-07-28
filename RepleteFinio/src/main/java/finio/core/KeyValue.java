package finio.core;

public class KeyValue {


    ////////////
    // FIELDS //
    ////////////

    protected Object K;
    protected Object V;
    private boolean equalsKOnly;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public KeyValue(Object K, Object V) {
        this(K, V, false);
    }
    public KeyValue(Object K, Object V, boolean ek) {
        this.K = K;
        this.V = V;
        equalsKOnly = ek;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Object getK() {
        return K;
    }
    public Object getV() {
        return V;
    }

    // Mutators

    public void setK(Object K) {
        this.K = K;
    }
    public void setV(Object V) {
        this.V = V;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        return equalsKOnly ? hashCodeK() : hashCodeKV();
    }

    private int hashCodeK() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((K == null) ? 0 : K.hashCode());
        return result;
    }
    private int hashCodeKV() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((K == null) ? 0 : K.hashCode());
        result = prime * result + ((V == null) ? 0 : V.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return equalsKOnly ? equalsK(obj) : equalsKV(obj);
    }

    private boolean equalsK(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        KeyValue other = (KeyValue) obj;
        if(K == null) {
            if(other.K != null) {
                return false;
            }
        } else if(!K.equals(other.K)) {
            return false;
        }
        return true;
    }
    private boolean equalsKV(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        KeyValue other = (KeyValue) obj;
        if(K == null) {
            if(other.K != null) {
                return false;
            }
        } else if(!K.equals(other.K)) {
            return false;
        }
        if(V == null) {
            if(other.V != null) {
                return false;
            }
        } else if(!V.equals(other.V)) {
            return false;
        }
        return true;
    }

    // Rendering!
    @Override
    public String toString() {
        return K + " = " + V;
    }
}
