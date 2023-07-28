package replete.threads;


public class OpaqueConsumingStrategy<T> implements ConsumingStrategy<T> {


    ///////////
    // FIELD //
    ///////////

    private TargetWorkProducer<T> producer;


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setProducer(TargetWorkProducer<T> producer) {
        this.producer = producer;
    }

    @Override
    public T produceWork() {
        return producer.produceWork();
    }
}
