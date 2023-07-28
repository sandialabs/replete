package replete.threads;

public class ConsumerThreadInfo extends ContinuousThreadInfo {


    ////////////
    // FIELDS //
    ////////////

    protected String targetWorkClass;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConsumerThreadInfo(Thread thread, StackTraceElement[] trace, int maxStackTraceElements) {
        super(thread, trace, maxStackTraceElements);
    }

    @Override
    public void updateFrom(Thread thread, StackTraceElement[] trace, int maxStackTraceElements) {
        super.updateFrom(thread, trace, maxStackTraceElements);
        Object targetWork = ((ConsumerThread<?>) thread).getTargetWork();
        targetWorkClass = (targetWork != null) ? targetWork.getClass().getName() : null;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getTargetWorkClass() {
        return targetWorkClass;
    }
}
