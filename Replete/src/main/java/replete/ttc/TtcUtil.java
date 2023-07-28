package replete.ttc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TtcUtil {
    private static Map<Thread, TransparentTaskContext> ttcs = new ConcurrentHashMap<>();
    public static TransparentTaskContext getTtc() {
        return ttcs.get(Thread.currentThread());
    }
    public static void addTtc(TransparentTaskContext context) {
        ttcs.put(Thread.currentThread(), context);
    }
    public static void removeTtc() {
        ttcs.remove(Thread.currentThread());
    }
}
