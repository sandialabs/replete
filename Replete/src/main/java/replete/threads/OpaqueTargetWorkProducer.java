package replete.threads;

public interface OpaqueTargetWorkProducer<T> extends TargetWorkProducer<T> {
    default public boolean isEmpty() {
        return false;                  // Allows Consuming threads to not have to deal with this method
    }
}
