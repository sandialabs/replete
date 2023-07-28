package finio.core;

public class KeyPathValue extends KeyValue {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public KeyPathValue(KeyPath K, Object V) {
        super(K, V);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    @Override
    public KeyPath getK() {
        return (KeyPath) super.getK();
    }
    public KeyPath getPath() {
        return getK();
    }

    // Mutators

    @Override
    public void setK(Object K) {
        if(!(K instanceof KeyPath)) {
            throw new IllegalArgumentException("Key must be a key path.");
        }
        super.setK(K);
    }
    public void setK(KeyPath K) {
        super.setK(K);
    }
    public void setPath(KeyPath K) {
        setK(K);
    }
}
