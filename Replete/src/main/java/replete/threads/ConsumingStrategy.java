package replete.threads;

public interface ConsumingStrategy<T> {
    void setProducer(TargetWorkProducer<T> producer);
    T produceWork();
}