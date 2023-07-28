package replete.threads;

public class TransparentWaitPauseStopConsumingStrategy<T> implements ConsumingStrategy<T> {


    ////////////
    // FIELDS //
    ////////////

    private ConsumerThread<T> thread;
    private TargetWorkProducer<T> producer;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TransparentWaitPauseStopConsumingStrategy(ConsumerThread<T> thread) {
        this.thread = thread;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setProducer(TargetWorkProducer<T> producer) {
        this.producer = producer;
    }

    @Override
    public T produceWork() {
        T targetWork = null;
        synchronized(producer) {
            while(producer.isEmpty() && !thread.producerPauseRequested && !thread.producerStopRequested) {
                ThreadUtil.wait(producer);
            }
            if(!thread.producerPauseRequested && !thread.producerStopRequested) {
                targetWork = producer.produceWork();
            }
            synchronized(this) {
                thread.producerPauseRequested = false;
                thread.producerStopRequested = false;
            }
        }
        return targetWork;
    }
}
