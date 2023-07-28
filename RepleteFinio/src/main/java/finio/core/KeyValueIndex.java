package finio.core;

public class KeyValueIndex {


    ////////////
    // FIELDS //
    ////////////

    protected Object K;
    protected Object V;
    protected int I;
    private boolean equalsKOnly;      // TODO: Expand this for I


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public KeyValueIndex(Object K, Object V, int I) {
        this(K, V, I, false);
    }
    public KeyValueIndex(Object K, Object V, int I, boolean equalsKOnly) {
        this.K = K;
        this.V = V;
        this.I = I;
        this.equalsKOnly = equalsKOnly;
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
    public int getI() {
        return I;
    }

    // Mutators

    public KeyValueIndex setK(Object K) {
        this.K = K;
        return this;
    }
    public KeyValueIndex setV(Object V) {
        this.V = V;
        return this;
    }
    public KeyValueIndex setI(int i) {
        I = i;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // TODO: Update these for I
    
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
        KeyValueIndex other = (KeyValueIndex) obj;
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
        KeyValueIndex other = (KeyValueIndex) obj;
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

    @Override
    public String toString() {
        return "[" + I + "] " + K + " = " + V;  // TODO: Needs rendering here?
    }
}
