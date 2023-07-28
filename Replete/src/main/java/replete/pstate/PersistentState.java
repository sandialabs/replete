
package replete.pstate;

import java.util.ArrayList;
import java.util.List;

import replete.logging.LogEntryType;
import replete.logging.LogManager;


/**
 * The Persistent State framework is a small framework
 * used to simplify how an application saves its state
 * between sessions.
 *
 * @author Derek Trumbo
 */

public abstract class PersistentState {

    protected transient List<StateReader> readers = new ArrayList<StateReader>();
    protected transient List<StateWriter> writers = new ArrayList<StateWriter>();

    // By the end of a subclass's constructor,
    // all state variables must be initialized.

    public PersistentState() {
        addStateReaders();
        addStateWriters();
    }

    protected abstract void addStateReaders();
    protected abstract void addStateWriters();

    // These methods are preferred to be used by subclasses
    // rather than accessing the list fields directly.

    protected void addStateReader(StateReader reader) {
        readers.add(reader);
    }

    protected void addStateWriter(StateWriter writer) {
        writers.add(writer);
    }

    public boolean read() {
        try {
            boolean success = true;
            for(StateReader reader : readers) {
                success = success && reader.read();
            }
            return success;
        } catch(Exception e) {
            LogManager.log(null, LogEntryType.ERROR, e, false);
            return false;
        }
    }

    public boolean write() {
        try {
            boolean success = true;
            for(StateWriter writer : writers) {
                success = success && writer.write();
            }
            return success;
        } catch(Exception e) {
            LogManager.log(null, LogEntryType.ERROR, e, false);
            return false;
        }
    }
}
