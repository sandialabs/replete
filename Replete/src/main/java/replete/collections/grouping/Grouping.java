package replete.collections.grouping;

import java.util.List;

// Experimental grouping code

public abstract class Grouping<G, T> {
    public abstract G getRootGroup();
    public abstract List<G> getSubgroups(G group);
    public abstract List<G> getGroups(T object);
}
