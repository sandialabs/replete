package replete.collections.simq;

public interface SimDataMutator<I, T> {
    public void mutate(SimulatedQueue<I, T> queue);
}
