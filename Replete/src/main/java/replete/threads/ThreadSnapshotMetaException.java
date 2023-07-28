package replete.threads;

import java.util.LinkedHashMap;
import java.util.Map;

public class ThreadSnapshotMetaException extends RuntimeException {


    ////////////
    // FIELDS //
    ////////////

    private static final int ELEMENT_MAX = 10;
    public Map<String, ThreadInfo> infos = new LinkedHashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ThreadSnapshotMetaException() {
        init();
    }
    public ThreadSnapshotMetaException(String message) {
        super(message);
        init();
    }
    public ThreadSnapshotMetaException(Throwable cause) {
        super(cause);
        init();
    }
    public ThreadSnapshotMetaException(String message, Throwable cause) {
        super(message, cause);
        init();
    }
    public ThreadSnapshotMetaException(String message, Throwable cause, boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        init();
    }

    private void init() {
        Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for(Thread t : traces.keySet()) {
            StackTraceElement[] trace = traces.get(t);
            ThreadInfo info = new ThreadInfo(t, trace, ELEMENT_MAX);
            infos.put(info.getKey(), info);
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getMessage() {
        String msg = super.getMessage();

        if(!infos.isEmpty()) {
            msg += "\n";
            for(String k : infos.keySet()) {
                ThreadInfo info = infos.get(k);
                msg += ">THREAD #" + info.id + ": " + info.name + " [" + info.type + "]\n";
                msg += "  {state=" + info.state + ", alive=" + info.alive + ", daemon=" + info.daemon + ", intp=" + info.intp + "}\n";
                for(String ele : info.topStackTrace) {
                    msg += "    " + ele + "\n";
                }
            }
        }

        return msg.trim();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        someMethod();
    }
    private static void someMethod() {
        try {
            throw new RuntimeException("error!");
        } catch(Exception e) {
            ThreadSnapshotMetaException e2 = new ThreadSnapshotMetaException(e);
            e2.printStackTrace();
        }
    }
}
