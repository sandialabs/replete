package replete.gc;

import java.lang.management.GarbageCollectorMXBean;

import com.sun.management.GarbageCollectionNotificationInfo;


public interface GcNotificationListener {

    // Called once per collector per collection.  The collector is represented
    // primarily by the first argument and the collection is represented
    // primarily by the second argument.
    public void handle(GarbageCollectorMXBean gcBean, GarbageCollectionNotificationInfo info);
}
