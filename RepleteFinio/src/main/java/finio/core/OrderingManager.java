package finio.core;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

public interface OrderingManager extends Iterable /* will iterate over KVI from getEntries() */ {

    // Initialization - Read the non terminal's key-value pairs for
    // the first time.  Discard any previous ordering state.
    public void initialize(NonTerminal M);

    // Host Mutation - When the host map changes, update the ordering
    // that this object is managing.  These correspond to 6 out of 7
    // of NonTerminal's change events (overall change not needed).
    public void ntBatchChanged();
    public void ntCleared();
    public void ntKeyAdded(Object K, Object V);
    public void ntKeyRemoved(Object K);
    public void ntKeyChanged(Object Kold, Object Knew);
    public void ntValueChanged(Object K, Object V);

    // Index Mapping - Allows for individual look ups of indices
    // related to keys.
    public Object getKey(int I);
    public boolean has(int I);
    public Set<Object> getKeys();
    public Collection<Object> getValues();
    //public Set<KeyValueIndex> getEntries();

    // Comparisons - Asks the ordering manager to use a different
    // comparator to create the ordering.
    public void setComparator(Comparator<KeyValue> C);
}
