
package replete.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import com.sun.management.GarbageCollectionNotificationInfo;

import replete.threads.ThreadUtil;

// References:
//   https://stackoverflow.com/questions/2057792/garbage-collection-notification
//   http://www.fasterj.com/articles/gcnotifs.shtml
//    - GcUtil/StandardConsoleGcNotificationListener based off this code
//   https://stackoverflow.com/questions/1262328/how-is-the-java-memory-pool-divided
//    - Good information on Java memory pool concepts
//   https://docs.oracle.com/javase/7/docs/api/java/lang/management/MemoryUsage.html
//    - Memory pool documentation

// Other useful GC debugging tools:
//   -Xloggc:FILEPATH, -XX:+PrintGCDetails, -XX:+PrintGCTimeStamps, jstat

public class GcUtil {


    ////////////
    // FIELDS //
    ////////////

    private static Map<GarbageCollectorMXBean, NotificationListener> beanListeners = new HashMap<>();


    ////////////////////
    // ENABLE/DISABLE //
    ////////////////////

    public static synchronized void setGcMonitoringEnabled(boolean enabled) {
        if(isGcMonitoringEnabled() == enabled) {
            return;
        }

        // Get all the GarbageCollectorMXBeans - there's one for each heap generation
        // so probably two - the old generation and young generation
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

        // Install a notification handler for each bean
        for(GarbageCollectorMXBean gcBean : gcBeans) {
            NotificationEmitter gcBeanEmitter = (NotificationEmitter) gcBean;

            if(enabled) {
                NotificationListener listener = new InternalNotificationListener(gcBean);
                gcBeanEmitter.addNotificationListener(listener, null, null);
                beanListeners.put(gcBean, listener); // Save for later for disable
            } else {
                try {
                    NotificationListener listener = beanListeners.get(gcBean);
                    gcBeanEmitter.removeNotificationListener(listener, null, null);
                    beanListeners.remove(gcBean);
                } catch(ListenerNotFoundException e) {
                    // Intentionally ignore.  Most notifiers don't care if
                    // you try to remove a listener that is not registered
                    // but this one apparently has a checked exception for it!
                }
            }
        }
    }

    public static synchronized boolean isGcMonitoringEnabled() {
        return !beanListeners.isEmpty();
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    // A custom notifier implementation here as ChangeNotifier & ExtChangeNotifier
    // are not exactly what we need here.
    private static List<GcNotificationListener> gcListeners = new ArrayList<>();
    public static void addGcListener(GcNotificationListener listener) {
        gcListeners.add(listener);
    }
    public static void removeGcListener(GcNotificationListener listener) {
        gcListeners.remove(listener);
    }
    private static void fireGcNotifier(GarbageCollectorMXBean gcBean, GarbageCollectionNotificationInfo info) {
        for(GcNotificationListener listener : gcListeners) {
            listener.handle(gcBean, info);
        }
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private static class InternalNotificationListener implements NotificationListener {
        private GarbageCollectorMXBean gcBean;
        public InternalNotificationListener(GarbageCollectorMXBean gcBean) {
            this.gcBean = gcBean;
        }
        @Override
        public void handleNotification(Notification notification, Object handback) {
            if(notification.getType().equals(
                GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                GarbageCollectionNotificationInfo info =
                    GarbageCollectionNotificationInfo.from(
                        (CompositeData) notification.getUserData());
                fireGcNotifier(gcBean, info);
            }
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws InterruptedException {
        addGcListener(new StandardConsoleGcNotificationListener());
        setGcMonitoringEnabled(true);

        Thread t = new Thread() {
            @Override
            public void run() {
                ThreadUtil.sleep(15000);
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                System.gc();
                ThreadUtil.sleep(5000);
                System.gc();
            }
        };
        t.start();
        t2.start();
        t.join();
        t2.join();
    }
}
