package replete.threads.deadlock;

public class StandardConsoleDeadlockHandler extends StandardMessageCreatorDeadlockHandler {


    ////////////
    // FIELDS //
    ////////////

    private boolean stdErr;
    private boolean newOnly;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StandardConsoleDeadlockHandler() {
        this(true, false);
    }
    public StandardConsoleDeadlockHandler(boolean stdErr, boolean newOnly) {
        this.stdErr = stdErr;
        this.newOnly = newOnly;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected boolean includeDescriptor(DeadlockedThreadDescriptor desc) {
        return newOnly && desc.isNewlyEcountered() || !newOnly;
    }

    @Override
    protected void processMessage(String msg) {
        if(stdErr) {
            System.err.println(msg);
        } else {
            System.out.println(msg);
        }
    }
}
