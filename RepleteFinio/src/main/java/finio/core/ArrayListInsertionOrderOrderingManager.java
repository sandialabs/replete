package finio.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArrayListInsertionOrderOrderingManager extends AbstractOrderingManager {


    ////////////
    // FIELDS //
    ////////////

    private List<KeyValue> ordering = new ArrayList<>();
    private Map<Object, Integer> keyIndices = new HashMap<>();


    /////////////////////
    // OVERRIDDEN - OM //
    /////////////////////

    @Override
    public Iterator iterator() {
        return null;
    }
    @Override
    public Set<Object> getKeys() {
        return null;
    }
    @Override
    public Collection<Object> getValues() {
        return null;
    }
    @Override
    public void ntBatchChanged() {
        clearOrdering();
        initOrdering();
        resortOrdering();
    }
    @Override
    public void ntCleared() {
        clearOrdering();
    }
    @Override
    public void ntKeyAdded(Object K, Object V) {
        addEntry(new KeyValue(K, V));
    }
    @Override
    public void ntKeyRemoved(Object K) {
        int I = keyIndices.get(K);
        ordering.remove(I);
    }
    @Override
    public void ntKeyChanged(Object Kold, Object Knew) {
    }
    @Override
    public void ntValueChanged(Object K, Object V) {
        int I = keyIndices.get(K);
        KeyValue KV = ordering.get(I);
        KV.setV(V);
        // Ordering doesn't change but value is updated
    }

    @Override
    protected void initOrdering() {
        for(Object K : Mcontainer.K()) {       // Use E() eventually
            Object V = Mcontainer.get(K);
            KeyValue KV = new KeyValue(K, V);
            addEntry(KV);
        }
    }



    @Override
    public Object getKey(int I) {
        return ordering.get(I).getK();
    }
    @Override
    public boolean has(int I) {
        return I >= 0 && I < ordering.size();
    }


    //////////////////////
    // OVERRIDDEN - AOM //
    //////////////////////

    @Override
    protected void addEntry(KeyValue KV) {
        keyIndices.put(KV.getK(), Mcontainer.size());
        ordering.add(KV);
        // New KV is at desired index.
    }
    @Override
    protected void clearOrdering() {
        keyIndices.clear();
        ordering.clear();
    }
    @Override
    protected void resortOrdering() {
        if(C != null) {
            Collections.sort(ordering, C);
        }
    }
}
