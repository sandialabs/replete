package replete.pstate2;

import java.io.File;

import replete.xstream.XStreamWrapper;


public class XmlFileManager extends PersistentStateManager {
    private File file;

    public XmlFileManager(File f) {
        file = f;
    }

    @Override
    public Object load() throws PersistentStateLoadException {
        try {
            return XStreamWrapper.loadTarget(file);
        } catch(Exception e) {
            throw new PersistentStateLoadException(e);
        }
    }
    @Override
    public void save(Object obj) throws PersistentStateSaveException {
        try {
            XStreamWrapper.writeToFile(obj, file);
        } catch(Exception e) {
            throw new PersistentStateSaveException(e);
        }
    }
}