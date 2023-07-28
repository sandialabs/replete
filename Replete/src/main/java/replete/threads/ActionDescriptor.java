package replete.threads;

public class ActionDescriptor {


    ////////////
    // FIELDS //
    ////////////

    String id;
    Performable immediateAction;
    int delay;
    Performable delayedAction;
    boolean repeatImmediateAction;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getId() {
        return id;
    }
    public Performable getImmediateAction() {
        return immediateAction;
    }
    public int getDelay() {
        return delay;
    }
    public Performable getDelayedAction() {
        return delayedAction;
    }
    public boolean isRepeatImmediateAction() {
        return repeatImmediateAction;
    }

    // Mutators

    public ActionDescriptor setId(String id) {
        this.id = id;
        return this;
    }
    public ActionDescriptor setImmediateAction(Performable immediateAction) {
        this.immediateAction = immediateAction;
        return this;
    }
    public ActionDescriptor setDelay(int delay) {
        this.delay = delay;
        return this;
    }
    public ActionDescriptor setDelayedAction(Performable delayedAction) {
        this.delayedAction = delayedAction;
        return this;
    }
    public ActionDescriptor setRepeatImmediateAction(boolean repeatImmediateAction) {
        this.repeatImmediateAction = repeatImmediateAction;
        return this;
    }
}
