package replete.progress;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;



/**
 * This progress manager is useful in the case when multiple
 * GUI components are jockeying for use of a single status-
 * displaying component.  This class keeps track of all the
 * "progress sources" that wish to display a status message,
 * or use the status progress bar, and their corresponding
 * priority.  Only the progress source with the highest
 * priority is allowed to have its progress messages displayed.
 * Progress sources with equal priority are treated in a
 * first-come, first-serve manner.  Note that this class is
 * not useful when there exists a GUI component that can
 * display progress messages from multiple sources at once
 * (i.e. Eclipse) - a different design completely would
 * be required.  This design is useful when there is a single
 * status bar and/or progress bar on a given window.
 *
 * This class was designed with efficiency in mind.  The
 * majority of operations below that correctly keep track of
 * all the progress sources and their corresponding priorities
 * and messages execute in O(1) time.  This was why two separate
 * data structures are used independent of but in harmony with
 * each other.
 *
 * This class is thread safe.  The only two methods that
 * should be called by the outside world, sendProgress and
 * endProgress, are synchronized.  Depending on how many
 * progress sources are sending updates at once, this
 * could actually hurt performance, but this will need to
 * be addressed at a later date if it becomes a problem.
 *
 * Note also that the behavior is undefined if a progress
 * source first purports to be of one priority (via
 * sendProgress method), and before executing the
 * endProgress method for that priority it executes
 * sendProgress with a different priority.  In general
 * a progress source's priority should be determined
 * once before hand, and then always send message updates
 * using that one priority.
 *
 * @author Derek Trumbo
 */

public class ProgressManager {

    // Some default priorities.  Progress sources can use whatever
    // integer priority they want.  The smaller the value, the
    // higher the priority.  Take into consideration the values
    // for the constants below if using priorities other than
    // these three constants.
    public static final int HIGH_PR = 0;
    public static final int MEDIUM_PR = 50;
    public static final int LOW_PR = 100;

    // The key and priority of the progress source whose progress
    // messages are currently being delivered to the UI.  This
    // source is known as the "top source".  When the key is null,
    // then there is no top source and no progress messages are being
    // shown.  Also, this key is null iff the source-message map and
    // source priority queue are empty.
    protected static Object topSourceKey = null;
    protected static int topSourcePriority = -1;

    // The two data structures that maintain 1) a mapping between
    // a progress source and its most recent progress message
    // and 2) a priority queue of progress sources.
    protected static Map<Object, ProgressMessage> sourceMessageMap =
       new HashMap<Object, ProgressMessage>();
    protected static Map<Integer, ArrayList<Object>> sourcePriorityQueue =
       new HashMap<Integer, ArrayList<Object>>();

    // Notifier for when a progress message should be delivered
    // to the UI components that are listening.
    protected static ChangeNotifier progressNotifier = new ChangeNotifier(new String(""));
    public static void addProgressListener(ChangeListener listener) {
        progressNotifier.addListener(listener);
    }

    // Notifier for when progress displays should be cleared,
    // as there is no more progress to report.
    protected static ChangeNotifier clearNotifier = new ChangeNotifier(new String(""));
    public static void addClearListener(ChangeListener listener) {
        clearNotifier.addListener(listener);
    }

    // Sends a progress message for a given progress source at
    // the specified priority.  sourceKey can simply be whatever
    // object is using the progress code ('this' works well).
    // If multiple classes want to send progress messages as
    // the same source, they will need to have some common object
    // shared between them that they can all pass in as first
    // argument to this method.  If no such object exists
    // (a common situation if one class calls another, which calls
    // another, which calls another, and the last method doesn't
    // know anything about the original class), then use this
    // class's utility method, generateKey.
    public static synchronized void sendProgress(Object sourceKey, int priority, ProgressMessage message) {

        if(sourceKey == null) {
            throw new IllegalArgumentException("Source key cannot be null.");
        }

        // Only put the new source into the queue if this
        // is the first time it is being seen.  Sources
        // are only allowed to have a single priority
        // during their existence.
       if(!sourceMessageMap.containsKey(sourceKey)) {
           addPriorityQueue(sourceKey, priority);
       }

       // Update the progress message for this source.
       sourceMessageMap.put(sourceKey, message);

       // If there is no top source, then set it to the
       // immediate source.
       if(topSourceKey == null) {
          topSourceKey = sourceKey;
          topSourcePriority = priority;
          sendTopProgress();

       // Else if the immediate source is the top source,
       // just send the new progress message.
       } else if(topSourceKey == sourceKey) {
          sendTopProgress();

       // Else if the immediate source's priority is
       // greater than the current top source's priority,
       // replace the top source with the immediate source
       // and send the new top source's progress message.
       } else if(priority < topSourcePriority) {
          topSourceKey = sourceKey;
          topSourcePriority = priority;
          sendTopProgress();
       }

       // The case that doesn't do anything is the case in
       // which:
       // * There is already a top source.
       // * The immediate source is not the top source.
       // * The immediate source has a lower priority than
       //   the top source.
       // In this case you don't want to send any message
       // because the top source's message should just stay
       // visible and the lower priority source's message
       // should just be ignored.
    }

    // Allow a progress source to signal that it is will
    // no longer be sending progress messages.  This is
    // important so that higher-priority sources can
    // indicate to the progress manager that the next
    // source can take over sending progress messages.
    public static synchronized void endProgress(Object sourceKey, int priority) {

       // Remove the progress source's key from both data
       // structures.
       sourceMessageMap.remove(sourceKey);
       removePriorityQueue(sourceKey, priority);

       // If the immediate source is the top source, get the
       // progress source which has the next-highest priority
       // and send its progress message.
       if(topSourceKey == sourceKey) {
          findNextTopSource();

          // If there are no more sources who want to report
          // progress messages, then let listeners know that
          // they can clear themselves.  The top source's
          // key being null is equivalent to both the
          // source-message map and the source priority queue
          // being empty.
          if(topSourceKey == null) {
              clearNotifier.fireStateChanged();
          } else {
              sendTopProgress();
          }
       }

       // Else if the immediate source is not the top source,
       // then don't change the top source or send any
       // new messages, as the top source still has the
       // priority, and its message hasn't changed with
       // the calling of this method.
    }

    // Clear all progress sources and tell all listeners that
    // progress should be cleared.
    public static void clear() {
        topSourceKey = null;
        topSourcePriority = -1;
        sourceMessageMap.clear();
        sourcePriorityQueue.clear();
        clearNotifier.fireStateChanged();
    }

    // Send the progress message of the top source.
    protected static void sendTopProgress() {
       progressNotifier.setSource(sourceMessageMap.get(topSourceKey));
       progressNotifier.fireStateChanged();
    }

    ////////////////////////////
    // Priority Queue Methods //
    ////////////////////////////

    // Adds a source key object to the end of the list
    // corresponding to the given priority.
    protected static void addPriorityQueue(Object sourceKey, int priority) {
        ArrayList<Object> priorityList = sourcePriorityQueue.get(priority);

        if(priorityList == null) {
            priorityList = new ArrayList<Object>();
            sourcePriorityQueue.put(priority, priorityList);
        }

        priorityList.add(sourceKey);
    }

    // Removes a source key object from the list corresponding
    // to the specified priority.
    protected static void removePriorityQueue(Object sourceKey, int priority) {
        ArrayList<Object> priorityList = sourcePriorityQueue.get(priority);

        if(priorityList != null) {
            priorityList.remove(sourceKey);
            if(priorityList.isEmpty()) {
                sourcePriorityQueue.remove(priority);
            }
        }
    }

    // Find the source with the next most highest priority.
    // This will set the top source fields directly, or
    // leave them uninitialized to represent there are no
    // more progress sources sending progress messages.
    protected static void findNextTopSource() {

        // Clear the top source if the priority queue
        // is now empty.
        if(sourcePriorityQueue.isEmpty()) {
            topSourceKey = null;
            topSourcePriority = -1;
            return;
        }

        ArrayList<Object> priorityList = sourcePriorityQueue.get(topSourcePriority);

        // If there are more sources at the same priority
        // as the previous top source, just grab the
        // next in that priority's list.
        if(priorityList != null) {
            topSourceKey = priorityList.get(0);
            // topSourcePriority stays the same
            return;
        }

        // Else we need to sort the priorities to find
        // the next-lowest priority.
        Set<Integer> priorities = sourcePriorityQueue.keySet();
        Object[] pArray = priorities.toArray();
        Arrays.sort(pArray);
        topSourcePriority = (Integer) pArray[0];
        topSourceKey = sourcePriorityQueue.get(topSourcePriority).get(0);
    }

    // Good when two classes who don't know anything about each other
    // want to send as the same source.
    protected static Map<String, Object> generatedKeys = new HashMap<String, Object>();
    public static Object generateKey(String s) {
        Object o = generatedKeys.get(s);
        if(o == null) {
            o = new Object();
            generatedKeys.put(s, o);
        }
        return o;
    }
}
