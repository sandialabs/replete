package finio.core;

import java.util.Comparator;

public abstract class AbstractOrderingManager implements OrderingManager {


    ////////////
    // FIELDS //
    ////////////

    protected NonTerminal Mcontainer;
    protected Comparator<KeyValue> C;


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void initialize(NonTerminal Mcontainer) {
        this.Mcontainer = Mcontainer;
        clearOrdering();
        initOrdering();
        resortOrdering();
    }

    @Override
    public void setComparator(Comparator<KeyValue> C) {
        this.C = C;
        resortOrdering();
    }


    //////////////
    // ABSTRACT //
    //////////////

    protected abstract void addEntry(KeyValue KV);
    protected abstract void initOrdering();
    protected abstract void clearOrdering();
    protected abstract void resortOrdering();
}
