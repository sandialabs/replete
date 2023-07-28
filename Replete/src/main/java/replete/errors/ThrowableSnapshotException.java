package replete.errors;

import replete.text.StringUtil;

// This exception holds a snapshot of an exception that
// occurred on another machine, so machines don't have
// to exchange actual Throwable objects, which can have
// negative effects.

public class ThrowableSnapshotException extends Exception {


    ////////////
    // FIELDS //
    ////////////

    private ThrowableSnapshot snapshot;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ThrowableSnapshotException(ThrowableSnapshot snapshot) {
        this.snapshot = snapshot;
    }
    public ThrowableSnapshotException(String message, ThrowableSnapshot snapshot) {
        super(message);
        this.snapshot = snapshot;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public ThrowableSnapshot getSnapshot() {
        return snapshot;
    }


    //////////
    // MISC //
    //////////

    public String getSnapshotMessageLabel() {
        return "Error Snapshot Text";
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getMessage() {
        String top = "=== " + getSnapshotMessageLabel() + " ===";
        String bot = StringUtil.replicateChar('=', top.length());
        String baseMsg = super.getMessage();   // Sleeper space will still exist on null baseMsg ;)
        return StringUtil.cleanNull(baseMsg) + "\n" + top + "\n" + snapshot.getCompleteText() + bot;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        try {
            x();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void x() throws Exception {
        try {
            a();
        } catch(Exception e) {
            ThrowableSnapshot snapshot = new ThrowableSnapshot(e);
            ThrowableSnapshotException e2 = new ThrowableSnapshotException("An error occurred...", snapshot);
            throw e2;
        }
    }
    private static void a() {
        b();
    }
    private static void b() {
        try {
            c();
        } catch(Exception e) {
            throw new RuntimeException("Second message", e);
        }
    }
    private static void c() {
        throw new RuntimeException("Orig Message");
    }
}
