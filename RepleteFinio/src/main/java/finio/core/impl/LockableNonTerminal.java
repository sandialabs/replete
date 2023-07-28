package finio.core.impl;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.errors.FMapException;

public class LockableNonTerminal extends WrapperNonTerminal {


    ////////////
    // FIELDS //
    ////////////

    private boolean locked = false;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public LockableNonTerminal(NonTerminal M) {
        super(M);
        init();
        locked = true;
    }

    protected void init() {
        // Can be subclassed to override this method.  This
        // method is called before lock first engaged.  Can
        // also just pass into the constructor an already-
        // populated non-terminal.
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessor

    public boolean isLocked() {
        return locked;
    }

    // Mutator

    public void setLocked(boolean locked) {
        this.locked = locked;
    }


    /////////
    // MAP //
    /////////

    @Override
    public void clear() {
        if(locked) {
            throw new LockedException("cannot clear while locked");
        }
        super.clear();
    }
    // [NT Core: 1/10 methods]


    /////////
    // GET //
    /////////

    @Override
    public Object getAndSet(Object K, Object Vdefault) {
        if(locked) {
            throw new LockedException("cannot getAndSet while locked");
        }
        return super.getAndSet(K, Vdefault);
    }
    @Override
    public Object getAndSetValid(Object K, Object Vdefault, Class type) {
        if(locked) {
            throw new LockedException("cannot getAndSetValid while locked");
        }
        return super.getAndSetValid(K, Vdefault, type);
    }
    // [NT Get: 2/9 methods]


    /////////
    // PUT //
    /////////

    @Override
    public Object put(Object K, Object V) {
        if(locked) {
            throw new LockedException("cannot put while locked");
        }
        return super.put(K, V);
    }
    @Override
    public Object putByKey(Object K, Object V) {
        if(locked) {
            throw new LockedException("cannot putByKey while locked");
        }
        return super.putByKey(K, V);
    }
    @Override
    public Object putByIndex(int I, Object V) {
        if(locked) {
            throw new LockedException("cannot putByIndex while locked");
        }
        return super.putByIndex(I, V);
    }
    @Override
    public Object putByPath(KeyPath P, Object V) {
        if(locked) {
            throw new LockedException("cannot putByPath while locked");
        }
        return super.putByPath(P, V);
    }
    @Override
    public Object putByPathNoCopy(KeyPath P, Object V) {
        if(locked) {
            throw new LockedException("cannot putByPathNoCopy while locked");
        }
        return super.putByPathNoCopy(P, V);
    }
    @Override
    public void putAll(Map<? extends Object, ? extends Object> m) {
        if(locked) {
            throw new LockedException("cannot putAll while locked");
        }
        super.putAll(m);
    }
    @Override
    public void putAll(NonTerminal Mnew) {
        if(locked) {
            throw new LockedException("cannot putAll while locked");
        }
        super.putAll(Mnew);
    }
    // [NT Put: 7 methods] (TODO: allow putByPath*)


    ////////////
    // REMOVE //
    ////////////

    @Override
    public Object removeByKey(Object K) {
        if(locked) {
            throw new LockedException("cannot removeByKey while locked");
        }
        return super.removeByKey(K);
    }
    @Override
    public boolean removeValue(Object V) {
        if(locked) {
            throw new LockedException("cannot removeValue while locked");
        }
        return super.removeValue(V);
    }
    @Override
    public Object removeByPath(KeyPath P) {
        if(locked) {
            throw new LockedException("cannot removeByPath while locked");
        }
        return super.removeByPath(P);
    }
    @Override
    public Object removeByPathNoCopy(KeyPath P) {
        if(locked) {
            throw new LockedException("cannot removeByPathNoCopy while locked");
        }
        return super.removeByPathNoCopy(P);
    }
    // [NT Put: 4 methods] (TODO: allow removeByPath*)


    /////////////////////////////
    // CHANGE ALLOWED/PROBABLE //
    /////////////////////////////

    @Override
    public boolean canAdd(Object K, Object V) {
        return !locked && super.canAdd(K, V);
    }
    @Override
    public boolean canChangeValue(Object K, Object V) {
        return !locked && super.canChangeValue(K, V);
    }
    @Override
    public boolean canChangeKey(Object K1, Object K2) {
        return !locked && super.canChangeKey(K1, K2);
    }
    @Override
    public boolean canRemove(Object K) {
        return !locked && super.canRemove(K);
    }
    @Override
    public boolean canClearMap() {
        return !locked && super.canClearMap();
    }
    // 5/12 methods


    //////////
    // MISC //
    //////////

    @Override
    public Object putSysMeta(Object K, Object V) {
        if(locked) {
            throw new LockedException("cannot putSysMeta while locked");
        }
        return super.putSysMeta(K, V);
    }
    @Override
    public void compress() {
        if(locked) {
            throw new LockedException("cannot compress while locked");
        }
        super.compress();
    }
    @Override
    public void changeKey(Object Kcur, Object Knew) {
        if(locked) {
            throw new LockedException("cannot changeKey while locked");
        }
        super.changeKey(Kcur, Knew);
    }
    @Override
    public void createAlternatesMap() {
        if(locked) {
            throw new LockedException("cannot createAlternatesMap while locked");
        }
        super.createAlternatesMap();
    }
    @Override
    public void createAlternatesMap(Object K) {
        if(locked) {
            throw new LockedException("cannot createAlternatesMap while locked");
        }
        super.createAlternatesMap(K);
    }
    @Override
    public void describe(Object K) {
        if(locked) {
            throw new LockedException("cannot describe while locked");
        }
        super.describe(K);
    }
    @Override
    public void promote(Object K) {
        if(locked) {
            throw new LockedException("cannot promote while locked");
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
        if(locked) {
            throw new LockedException("cannot move while locked");
        }
        super.move(Psources, Pdest, position, preventOverwrite);
    }
    @Override
    public void copy(KeyPath[] Psources, KeyPath Pdest, int position) {
        if(locked) {
            throw new LockedException("cannot copy while locked");
        }
        super.copy(Psources, Pdest, position);
    }
    @Override
    public NonTerminal overlayByKeyPath(KeyPath P) {
        if(locked) {
            throw new LockedException("cannot overlayByKeyPath while locked");
        }
        return super.overlayByKeyPath(P);
    }
    // 3 methods
}
