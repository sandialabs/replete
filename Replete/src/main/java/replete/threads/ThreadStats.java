package replete.threads;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import replete.errors.ThrowableSnapshot;

public class ThreadStats implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    public static final int ELEMENT_MAX = 10;

    private Map<String, ThreadInfo> infos = Collections.synchronizedMap(new HashMap<String, ThreadInfo>());


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ThreadStats() {
        updateAll(true);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Map<String, ThreadInfo> getInfos() {
        return infos;
    }


    ///////////
    // CLEAR //
    ///////////

    public void clear() {
        synchronized(infos) {
            infos.clear();
        }
    }


    ////////////
    // UPDATE //
    ////////////

    public void updateAll(boolean recordRemovedThreads) {
        synchronized(infos) {
            Map<Thread, StackTraceElement[]> liveTraces = Thread.getAllStackTraces();
            Set<String> liveKeys = new HashSet<>();
            for(Thread liveThread : liveTraces.keySet()) {
                String key = updateThread(liveThread, liveTraces, null);
                liveKeys.add(key);
            }

            Set<String> removeKeys = new HashSet<>();
            for(String prevKey : infos.keySet()) {
                if(!liveKeys.contains(prevKey)) {
                    if(recordRemovedThreads) {
                        infos.get(prevKey).setRemoved(true);
                    } else {
                        removeKeys.add(prevKey);
                    }
                }
            }
            for(String removeKey : removeKeys) {
                infos.remove(removeKey);
            }
        }
    }

    public String updateThread(Thread liveThread, Throwable e) {
        synchronized(infos) {
            Map<Thread, StackTraceElement[]> liveTraces = Thread.getAllStackTraces();
            return updateThread(liveThread, liveTraces, e);
        }
    }

    private String updateThread(Thread thread, Map<Thread, StackTraceElement[]> traces, Throwable e) {
        StackTraceElement[] trace = traces.get(thread);
        String key = thread.getClass().getName() + "/" + thread.getId() + "/" + thread.hashCode();
        ThreadInfo info = infos.get(key);
        if(e != null) {
            info.setUncaughtExceptionSnapshot(new ThrowableSnapshot(e));  // We use a snapshot because ThreadInfo's are serialized outside of the software
        }
        if(info == null) {
            if(thread instanceof RThread) {
                info = ((RThread) thread).getInfo(trace);
            } else {
                info = new ThreadInfo(thread, trace, ELEMENT_MAX);
            }
            infos.put(key, info);
        } else {
            info.updateFrom(thread, trace, ELEMENT_MAX);
        }
        return key;
    }
}
