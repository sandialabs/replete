package replete.collections.simq;

import replete.collections.Pair;

public interface SimDataCreator<I, T> {
    public Pair<I, T>[] createElements(int total, int currentSize);
}
