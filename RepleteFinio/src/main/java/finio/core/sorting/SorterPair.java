package finio.core.sorting;

import java.util.Set;

public class SorterPair {
    private String name;
    private Set<?> sorter;

    public SorterPair(String nm, Set<?> str) {
        name = nm;
        sorter = str;
    }
    public String getName() {
        return name;
    }
    public Set<?> getSorter() {
        return sorter;
    }
}
