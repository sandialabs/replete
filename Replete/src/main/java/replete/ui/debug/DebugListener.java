package replete.ui.debug;

/**
 * @author Derek Trumbo
 */

public class DebugListener {
    public static boolean debugEnabled = true;
    private String[] evNameArr;
    private String context;

    public DebugListener() {
        this(null);
    }
    public DebugListener(String evNames) {
        if(evNames == null) {
            evNameArr = null;
        } else {
            evNameArr = evNames.split(" *, *");
        }
    }

    public DebugListener setContext(String context) {
        this.context = context;
        return this;
    }

    protected boolean acceptEvent(String evName) {
        if(!debugEnabled) {
            return false;
        }

        if(evNameArr == null) {
            return true;
        }

        for(String ev : evNameArr) {
            if(ev.equals(evName)) {
                return true;
            }
        }

        return false;
    }

    protected void output(String s) {
        if(context != null) {
            System.out.print(context + ": ");
        }
        System.out.println(s);
    }
}
