package finio.core;

import static finio.core.impl.FMap.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import finio.core.errors.KeyPathException;
import finio.core.impl.FMap;
import replete.collections.AList;
import replete.text.StringUtil;

// INI File (2-level hierarchy):
//
// [Spell-Checker]
// Current Language=3
// Relative Path=..\Aspell\bin

// Remember any AList can be either a stack or a queue depending on how you use it.

// TODO: Make sure this class has
//   public boolean isPrefix(KeyPath Pprefix);
//   public boolean isSuffix(KeyPath Psuffix);

// An AList is just an array of objects - any objects.
// A KeyPath is an AList because keys are just arbitrary objects as well.

public class KeyPath extends AList {


    ////////////
    // FIELDS //
    ////////////

    // The labels in the following special/pseudo key segments are
    // chosen arbitrarily (though they are used by operating systems)
    // and are an example of possible special segments.
    public static final SpecialKeyPathSegment CURRENT =
        new SpecialKeyPathSegment(".");
    public static final SpecialKeyPathSegment PARENT =
        new SpecialKeyPathSegment("..");

    // The separating characters below are chosen arbitrarily for this
    // class (though they are used by operating systems).
    public static final String ROOT_PREFIX = "/";
    public static final String SEGMENT_SEPARATOR = "/";


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Ignoring the initialCapacity super constructor.
    public KeyPath() {}
    public KeyPath(Collection<? extends Object> coll) {
        super(coll);
    }
    public KeyPath(Collection<? extends Object> coll, boolean reverse) {
        // Not most efficient way to do it.  Would have been nice to start
        // with the list a certain size, and then just iterate over the
        // collection adding to the back forward.  But feeling lazy with
        // the private fields of ArrayList.  Uggh.
        super(coll);
        if(reverse) {
            reverse();
        }
    }
    public KeyPath(Object[] segs) {
        this(segs, false);
    }
    public KeyPath(Object[] segs, boolean reverse) {
        if(reverse) {
            for(int s = segs.length - 1; s >= 0; s--) {  // No need to use reverse()
                add(segs[s]);
            }
        } else {
            for(int s = 0; s < segs.length; s++) {
                add(segs[s]);
            }
        }
    }
    public KeyPath(String collapsed, String delimRegex) {
        this(collapsed.split(delimRegex), false);
    }
    public KeyPath(String collapsed, String delimRegex, boolean reverse) {
        this(collapsed.split(delimRegex), reverse);
    }
    public KeyPath(Object segment) {
        this(new Object[] {segment}, false);
    }


    //////////////////////
    // STATIC SHORTHAND //
    //////////////////////

    public static KeyPath KP() {
        return new KeyPath();
    }
    public static KeyPath KP(Collection<? extends Object> coll) {
        return new KeyPath(coll);
    }
    public static KeyPath KP(Collection<? extends Object> coll, boolean reverse) {
        return new KeyPath(coll, reverse);
    }
    public static KeyPath KP(Object[] segs) {
        return new KeyPath(segs);
    }
    public static KeyPath KP(Object[] segs, boolean reverse) {
        return new KeyPath(segs, reverse);
    }
    public static KeyPath KP(String collapsed, String delimRegex) {
        return new KeyPath(collapsed, delimRegex);
    }
    public static KeyPath KP(String collapsed, String delimRegex, boolean reverse) {
        return new KeyPath(collapsed, delimRegex, reverse);
    }
    public static KeyPath KP(Object seg) {
        return new KeyPath(seg);
    }
    public static boolean isEmpty(KeyPath P) {
        return P == null || P.size() == 0;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Modifying, this reference returning

    // These must be overridden to provide the correct return type.
    @Override
    public KeyPath prepended(Object S) {
        prepend(S);
        return this;
    }
    @Override
    public KeyPath appended(Object S) {
        append(S);
        return this;
    }
    @Override
    public KeyPath removedFirst() {
        removeFirst();
        return this;
    }
    @Override
    public KeyPath removedLast() {
        removeLast();
        return this;
    }

    // Methods with better names for this class.
    public void prependPath(Collection<?> L) {
        super.prependList(L);
    }
    public void prependPath(Object... L) {
        super.prependList(L);
    }
    public void appendPath(Collection<?> L) {
        super.appendList(L);
    }
    public void appendPath(Object... L) {
        super.appendList(L);
    }
    public KeyPath appendedPath(KeyPath P) {
        super.appendList(P);
        return this;
    }

    // Non-modifying, reference returning

    @Override
    public KeyPath withFirst(Object E) {
        KeyPath L = new KeyPath(this);
        L.prepend(E);
        return L;
    }
    @Override
    public KeyPath withLast(Object E) {
        KeyPath L = new KeyPath(this);
        L.append(E);
        return L;
    }
    public KeyPath withLast(KeyPath P) {
        KeyPath L = new KeyPath(this);
        L.appendPath(P);
        return L;
    }
    // TODO: With first (keypath)
    @Override
    public KeyPath withoutFirst() {
        KeyPath L = new KeyPath(this);
        L.removeFirst();
        return L;
    }
    @Override
    public KeyPath withoutLast() {
        KeyPath L = new KeyPath(this);
        L.removeLast();
        return L;
    }
    // TODO: without last/first (keypath)


    //////////
    // MISC //
    //////////

    public String toUnixString() {
        return ROOT_PREFIX + StringUtil.join(this, SEGMENT_SEPARATOR);  // Should this be here?
    }
    @Override
    public KeyPath reversed() {
        return (KeyPath) super.reversed();
    }

    public KeyPath toReversed() {
        KeyPath copy = new KeyPath(this);  // Shallow copy
        copy.reverse();
        return copy;
    }

    public FMap toHierarchicalMulti() {
        return toHierarchicalMulti(null, false);
    }
    public FMap toHierarchicalMulti(boolean useValue) {
        return toHierarchicalMulti(null, useValue);
    }
    public FMap toHierarchicalMulti(Object V) {
        return toHierarchicalMulti(V, false);
    }
    public FMap toHierarchicalMulti(Object V, boolean useValue) {
        FMap tail = A();
        FMap head = tail;
        FMap preTail = null;
        Object tailObj = null;

        for(Object seg : this) {
            FMap next = A();
            tail.put(seg, next);
            preTail = tail;
            tail = next;
        }

        if(useValue || V != null) {
            if(preTail != null) {
                preTail.putEach(V);
                tailObj = V;
            } else {
                head = null;
                tailObj = null;
            }
        } else {
            tailObj = tail;
        }

        FMap multi = A(
            "head", head,
            "preTail", preTail,
            "tail", tailObj
        );

        return multi;
    }
    public FMap toHierarchical() {
        return toHierarchical(null, false);
    }
    public FMap toHierarchical(boolean useValue) {
        return toHierarchical(null, useValue);
    }
    public FMap toHierarchical(Object V) {
        return toHierarchical(V, false);
    }
    public FMap toHierarchical(Object V, boolean useValue) {
        FMap multi = toHierarchicalMulti(V, useValue);
        return multi.getM("head");
    }

    public KeyPath subKeyPath(int fromIndex, int toIndexNonIncl) {
        KeyPath P = new KeyPath();
        for(int i = fromIndex; i < toIndexNonIncl; i++) {
            P.add(get(i));
        }
        return P;
    }
    public KeyPath subKeyPath(int fromIndex) {
        return subKeyPath(fromIndex, size());
    }
    public boolean isAncestor(KeyPath P) {  // super walk?
        if(size() > P.size()) {
            return false;
        }
        for(int i = 0; i < size(); i++) {
            if(!FUtil.equals(P.get(i), get(i))) {
                return false;
            }
        }
        return true;
    }
    public boolean isDescendent(KeyPath P) {
        return P.isAncestor(this);
    }
    public static KeyPath[] findRootPaths(KeyPath[] paths) {
        List<KeyPath> accepted = new ArrayList<KeyPath>();
        for(KeyPath Ppossible : paths) {
            boolean addPossible = true;
            for(int s = accepted.size() - 1; s >= 0; s--) {
                KeyPath Paccepted = accepted.get(s);
                if(Paccepted.isAncestor(Ppossible)) {
                    accepted.remove(s);
                } else if(Ppossible.isAncestor(Paccepted)) {
                    addPossible = false;
                    break;
                }
            }
            if(addPossible) {
                accepted.add(Ppossible);
            }
        }
        return accepted.toArray(new KeyPath[0]);
    }


    ////////////////
    // RESOLUTION //
    ////////////////

    // Convert certain string segments into the appropriate actual object.
    public void convertSpecialSegments() {
        for(int i = 0; i < size(); i++) {
            if(get(i).equals(CURRENT.getLabel())) {
                set(i, CURRENT);
            } else if(get(i).equals(PARENT.getLabel())) {
                set(i, PARENT);
            }
        }
    }
    public KeyPath resolve() {
        return resolve(null);
    }
    public KeyPath resolve(KeyPath scope) {
        KeyPath P = new KeyPath();
        if(scope != null) {
            P.appendPath(scope);
        }
        KeyPath converted = KP(this);
        converted.convertSpecialSegments();
        for(Object S : converted) {
            if(S.equals(CURRENT)) {
                continue;
            } else if(S.equals(PARENT)) {
                if(P.size() != 0) {
                    P.removedLast();
                } else {
                    throw new KeyPathException("Key path resolution failed due to invalid parent reference.");
                }
                continue;
            }
            P.add(S);
        }
        return P;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("US");
        list.add("Colorado");
        list.add("Denver");

        String[] segs = {"US", "Colorado", "Denver"};

        KeyPath P = KP(segs, true);
        System.out.println("P: " + P);

        KeyPath P2 = KP(list, false);
        System.out.println("P2: " + P2);

        System.out.println("P2-Hier: " + P2.toHierarchicalMulti(true));

        P.prependPath(P2);
        System.out.println("P2/P: " + P);

        KeyPath P3 = KP("hi/../there/../dude.txt", "/");

        P3.convertSpecialSegments();
        System.out.println("P3: " + P3);
        System.out.println("P3-RESOLVE: " + P3.resolve());

        KeyPath P4 = KP("../../a", "/");
        P4.convertSpecialSegments();
        System.out.println("P4: " + P4);
        System.out.println("P4-RESOLVE-CTXT: " + P4.resolve(KP("usr/local", "/")));
        System.out.println(P4.resolve());
    }
}
