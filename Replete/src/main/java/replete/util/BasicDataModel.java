package replete.util;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;


public class BasicDataModel {


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    public class BatchChangeLock {
        private int count = 0;
        public synchronized void lock() {
            count++;
        }
        public synchronized void unlock() {
            count--;
        }
        public synchronized boolean isLocked() {
            return count != 0;
        }
    }


    ///////////
    // FIELD //
    ///////////

    protected transient BatchChangeLock batchChangeLock = new BatchChangeLock();


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected transient ChangeNotifier dataModelChangedNotifier = new ChangeNotifier(this);

    public void addDataModelChangedListener(ChangeListener listener) {
        dataModelChangedNotifier.addListener(listener);
    }

    protected void fireDataModelChangedNotifier() {
        if(batchChangeLock.isLocked()) {
            return;
        }
        dataModelChangedNotifier.fireStateChanged();
    }


    /////////////////
    // BEGIN / END //
    /////////////////

    public void beginBatchChanges() {
        batchChangeLock.lock();
    }

    public void endBatchChanges() {
        batchChangeLock.unlock();
        fireDataModelChangedNotifier();
    }
}
