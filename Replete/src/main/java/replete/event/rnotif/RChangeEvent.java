package replete.event.rnotif;


public class RChangeEvent {


    ///////////
    // FIELD //
    ///////////

    private RChangeNotifier notifier;
    private Object source;
    private long creationStart;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RChangeEvent(RChangeNotifier notifier, Object source) {
        this.notifier = notifier;
        this.source = source;
        creationStart = System.currentTimeMillis();
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public RChangeNotifier getNotifier() {
        return notifier;
    }
    public Object getSource() {
        return source;
    }
    public long getCreationStart() {
        return creationStart;
    }
}
