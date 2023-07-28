package replete.threads;

public interface TargetWorkProducer<T> {
    public boolean isEmpty();
    public T produceWork();
}
