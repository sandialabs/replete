package replete.pstate2;


public abstract class PersistentStateManager {
    public abstract Object load() throws PersistentStateLoadException;
    public abstract void save(Object obj) throws PersistentStateSaveException;
}
