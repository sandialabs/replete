
package replete.pstate.example;


import java.io.File;

import replete.pstate.PersistentState;
import replete.pstate.StateReader;
import replete.pstate.StateWriter;



/**
 * A subclass of PersistentState holds all the actual
 * application state that is to be persisted when the
 * application shuts down. It must initialize all of
 * that state by the end of its constructor, and add
 * all the state readers and state writers that are
 * responsible for writing that state to disk (in what
 * ever form) when the application shuts down and for
 * reading that state back into memory when the
 * application starts up again.
 *
 * @author Derek Trumbo
 */

public class AppState extends PersistentState {
    public static final File XML_FILE = new File("/home/user/app.state");
    public static final File INI_FILE = new File("/home/user/app.ini");
    public static final File CFG_FILE = new File("/home/user/app.cfg");

    // X is a state variable that is read from an XML
    // file, modifiable in the application, and written
    // back to the XML file (in serialized form). Since
    // it's in the XML it's not supposed to be user-
    // modifiable outside the application (or that's the
    // theory - it could also have been easily serialized
    // to binary form).
    public int x = 20;

    // Y is a state variable that is read from an INI file,
    // not modifiable in the application, and thus not
    // written back to any file. This is the typical
    // usage pattern of an INI file. It is for this reason
    // that we only register a reader and not a writer
    // for this application's state. The file format is a
    // list of key=value pairs. This format is used so that it
    // is easily modifiable by the user. The field is listed as
    // transient because this application's state is also
    // using a serializable reader/writer pair for certain
    // fields in this state and we don't have any reason
    // to write this field to that file since it is handled
    // by an INI reader/writer.
    public int y = 40;

    // Z is a state variable that is read from a configuration
    // file, modifiable in the application and written
    // back to the configuration file. The file format is a
    // list of key=value pairs. This format is used so that it
    // is easily modifiable by the user. The field is listed as
    // transient because this application's state is also
    // using a serializable reader/writer pair for certain
    // fields in this state and we don't have any reason
    // to write this field to that file since it is handled
    // by an INI reader/writer.
    public int z = 60;

    // One could also imagine state being stored in the Windows
    // registry, a database, or on some website that could
    // be accessed via a URL.
    // public transient int q;

    @Override
    protected void addStateReaders() {
        addStateReader(new XMLStateReader(XML_FILE));
        addStateReader(new IniStateReader(INI_FILE, this));
        addStateReader(new CfgStateReader(CFG_FILE, this));
        // addStateReader(new WindowsRegistryStateReader());
    }

    @Override
    protected void addStateWriters() {
        addStateWriter(new XMLStateWriter(XML_FILE));
        addStateWriter(new CfgStateWriter(CFG_FILE, this));
        // addStateWriter(new WindowsRegistryStateWriter());
    }

    // State readers and state writers as private inner
    // classes (they implicitly have access to the
    // application state).

    protected class XMLStateReader implements StateReader {
        protected File xmlFile;

        public XMLStateReader(File file) {
            xmlFile = file;
        }

        public boolean read() {
            // AppState state = (AppState) {deserialize from xmlFile};
            // x = state.x;
            return true;
        }
    }

    protected class XMLStateWriter implements StateWriter {
        protected File xmlFile;

        public XMLStateWriter(File file) {
            xmlFile = file;
        }

        public boolean write() {
            // {serialize AppState.this to xmlFile};
            return true;
        }
    }

    // The application's state can be made available via a
    // singleton pattern, can be made apart of the application's
    // data model, or can be made available via some other
    // method.  Most likely it will be some sort of global
    // concept similar to the Java system properties.
    private AppState() {}

    public static AppState getState() {
        return state;
    }

    protected static AppState state = new AppState();
}
