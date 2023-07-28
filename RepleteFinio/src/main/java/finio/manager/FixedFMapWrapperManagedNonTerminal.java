package finio.manager;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.ReadOnlyNonTerminal;
import finio.core.errors.FMapException;
import finio.core.impl.FMap;
import finio.plugins.extpoints.NonTerminalManager;

public class FixedFMapWrapperManagedNonTerminal
                 extends SimpleWrapperManagedNonTerminal
                 implements ReadOnlyNonTerminal {

    // Not unloadable/reloadable, always loaded.  Not refreshable.  No parameters.


    ///////////
    // FIELD //
    ///////////

    private Map m;
    private NonTerminal M;
    private boolean readOnlyEngaged = false;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FixedFMapWrapperManagedNonTerminal(NonTerminalManager manager) {
        super(manager);
        readOnlyEngaged = true;
    }

    @Override
    protected void initSimple() {
        M = FMap.A();
    }


    //////////////
    // ACCESSOR //
    //////////////

    @Override
    protected NonTerminal getM() {
        return M;
    }


    /////////
    // MAP //
    /////////

    @Override
    public void clear() {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("clear not supported");
        }
        super.clear();
    }
    // [NT Core: 1/10 methods]


    /////////
    // GET //
    /////////

    @Override
    public Object getAndSet(Object K, Object Vdefault) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("getAndSet not supported");
        }
        return super.getAndSet(K, Vdefault);
    }
    @Override
    public Object getAndSetValid(Object K, Object Vdefault, Class type) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("getAndSetValid not supported");
        }
        return super.getAndSetValid(K, Vdefault, type);
    }
    // [NT Get: 2/9 methods]


    /////////
    // PUT //
    /////////

    @Override
    public Object put(Object K, Object V) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("put not supported");
        }
        return super.put(K, V);
    }
    @Override
    public Object putByKey(Object K, Object V) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("put not supported");
        }
        return super.putByKey(K, V);
    }
    @Override
    public Object putByIndex(int I, Object V) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("put not supported");
        }
        return super.putByIndex(I, V);
    }
    @Override
    public Object putByPath(KeyPath P, Object V) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("put not supported");
        }
        return super.putByPath(P, V);
    }
    @Override
    public Object putByPathNoCopy(KeyPath P, Object V) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("put not supported");
        }
        return super.putByPathNoCopy(P, V);
    }
    @Override
    public void putAll(Map<? extends Object, ? extends Object> m) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("put not supported");
        }
        super.putAll(m);
    }
    @Override
    public void putAll(NonTerminal Mnew) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("put not supported");
        }
        super.putAll(Mnew);
    }
    // [NT Put: 7 methods] (TODO: allow putByPath)


    ////////////
    // REMOVE //
    ////////////

    @Override
    public Object removeByKey(Object K) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("remove not supported");
        }
        return super.removeByKey(K);
    }
    @Override
    public boolean removeValue(Object V) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("remove not supported");
        }
        return super.removeValue(V);
    }
    @Override
    public Object removeByPath(KeyPath P) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("remove not supported");
        }
        return super.removeByPath(P);
    }
    @Override
    public Object removeByPathNoCopy(KeyPath P) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("remove not supported");
        }
        return super.removeByPathNoCopy(P);
    }
    // [NT Remove: 4 methods]


    /////////////////////////////
    // CHANGE ALLOWED/PROBABLE //
    /////////////////////////////

    @Override
    public boolean canAdd(Object K, Object V) {
        return false;
    }
    @Override
    public boolean canChangeValue(Object K, Object V) {
        return false;
    }
    @Override
    public boolean canSetValue(Object K, Object V) {
        return false;
    }
    @Override
    public boolean canChangeKey(Object K1, Object K2) {
        return false;
    }
    @Override
    public boolean canRemove(Object K) {
        return false;
    }
    @Override
    public boolean canClearMap() {
        return false;
    }
    @Override
    public boolean wouldAdd(Object K, Object V) {
        return false;
    }
    @Override
    public boolean wouldChangeValue(Object K, Object V) {
        return false;
    }
    @Override
    public boolean wouldSetValue(Object K, Object V) {
        return false;
    }
    @Override
    public boolean wouldChangeKey(Object K1, Object K2) {
        return false;
    }
    @Override
    public boolean wouldRemove(Object K) {
        return false;
    }
    @Override
    public boolean wouldClearMap() {
        return false;
    }
    // 12 methods


    //////////
    // MISC //
    //////////

    @Override
    public Object putSysMeta(Object K, Object V) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("putSysMeta not supported");
        }
        return super.putSysMeta(K, V);
    }
    @Override
    public void compress() {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("compress not supported");
        }
        super.compress();
    }
    @Override
    public void changeKey(Object Kcur, Object Knew) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("changeKey not supported");
        }
        super.changeKey(Kcur, Knew);
    }
    @Override
    public void createAlternatesMap() {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("createAlternatesMap not supported");
        }
        super.createAlternatesMap();
    }
    @Override
    public void createAlternatesMap(Object K) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("createAlternatesMap not supported");
        }
        super.createAlternatesMap(K);
    }
    @Override
    public void describe(Object K) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("describe not supported");
        }
        super.describe(K);
    }
    @Override
    public void promote(Object K) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("promote not supported");
        }
        super.promote(K);
    }
    // 7/14 methods


    ///////////////////////////
    // MOVE / COPY / OVERLAY //
    ///////////////////////////

    @Override
    public void move(KeyPath[] Psources, KeyPath Pdest, int position, boolean preventOverwrite)
                                                                                                   throws FMapException {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("move not supported");
        }
        super.move(Psources, Pdest, position, preventOverwrite);
    }
    @Override
    public void copy(KeyPath[] Psources, KeyPath Pdest, int position) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("copy not supported");
        }
        super.copy(Psources, Pdest, position);
    }
    @Override
    public NonTerminal overlayByKeyPath(KeyPath P) {
        if(readOnlyEngaged) {
            throw new UnsupportedOperationException("overlayByKeyPath not supported");
        }
        return super.overlayByKeyPath(P);
    }
    // 3 methods

}
