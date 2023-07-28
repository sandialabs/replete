package finio.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import finio.core.FConst;
import finio.core.KeyValue;
import finio.core.NonTerminal;
import replete.text.StringUtil;

public class FListMap extends FList {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FListMap() {}
    // public AListMap(Collection<? extends Object> c) {    // This one really doesn't make much sense
    //     super(c);
    // }
    public FListMap(int initialCapacity) {
        super(initialCapacity);
    }

    // Custom

    public FListMap(Object... args) {
        for(int a = 0; a < args.length; a += 2) {
           if(a + 1 == args.length) {
               put(args[a], null);
           } else {
               put(args[a], args[a + 1]);
           }
        }
    }
    public FListMap(Map<?, ?> M) {
        super(M);
    }
    public FListMap(NonTerminal M) {
        super(M);
    }


    /////////
    // MAP //
    /////////

    // public boolean isEmpty();
    // public int size();
    // public int Z();
    @Override
    public Set<Object> getKeys() {            // O(n) runtime
        Set<Object> Ks = new LinkedHashSet<Object>();
        for(int I = 0; I < Z(); I++) {
            Ks.add(getLMKey(I));
        }
        return Ks;
    }
    @Override
    public Collection<Object> getValues() {          // O(n) runtime
        List<Object> Vs = new ArrayList<Object>();
        for(int I = 0; I < Z(); I++) {
            Vs.add(getLMValue(I));
        }
        return Vs;
    }
    // public Collection<Object> K();
    // public Collection<Object> V();
    // public Set<Object> keySet();
    // public Collection<Object> values();
    // public void clear();


    /////////
    // GET //
    /////////

    // public Object get(int I);                     // This method untouched for lists
    @Override
    public Object get(Object K) {                    // O(n) runtime
        for(int I = 0; I < Z(); I++) {
            KeyValue T = (KeyValue) super.get(I);
            if(T.getK().equals(K)) {
                return T.getV();
            }
        }
        return null;   // [OPTION] throw exception
    }
    // public Object getByKey(Object K);
    @Override
    public Object getByIndex(int I) {
        return getLMValue(I);
    }
    @Override
    public Object getKeyByIndex(int I) {
        return getLMKey(I);
    }
    // public Object getByPath(KeyPath P);
    // public Object getByPathNoCopy(KeyPath P);
    // public Object getValid(Object K, Class type);
    // public Object getAndSet(Object K, Object Vdefault);
    // public Object getAndSetValid(Object K, Object Vdefault, Class type);


    /////////
    // PUT //
    /////////

    @Override
    public Object put(Object K, Object V) {          // O(n) runtime
        for(int I = 0; I < Z(); I++) {
            KeyValue T = (KeyValue) super.get(I);
            if(T.getK().equals(K)) {
                Object Vprev = T.getV();
                T.setV(V);
                return Vprev;
            }
        }
        super.add(new KeyValue(K, V));
        return null;
    }
    // public Object putByKey(Object K, Object V);
    @Override
    public Object putByIndex(int I, Object V) {
        KeyValue T = (KeyValue) super.get(I);
        Object Vprev = T.getV();
        T.setV(V);
        return Vprev;
    }
    // public Object putByPath(KeyPath P, Object V);
    // public Object putByPathNoCopy(KeyPath P, Object V);
    // public void putAll(Map<? extends Object, ? extends Object> M);


    ////////////
    // REMOVE //
    ////////////

    // public Object remove(int I);              // This method untouched for lists
    @Override
    public Object removeByKey(Object K) {        // O(n) runtime
        for(int I = 0; I < Z(); I++) {
            if(getLMKey(I).equals(K)) {
                return super.remove(I);
            }
        }
        return null;
    }
    @Override
    public boolean removeValue(Object V) {       // O(n) runtime
        for(int I = 0; I < Z(); I++) {
            if(getLMValue(I).equals(V)) {
                super.remove(I);
                return true;
            }
        }
        return false;
    }
    // public Object removeByPath(KeyPath P);
    // public Object removeByPathNoCopy(KeyPath P);


    //////////////
    // CONTAINS //
    //////////////

    @Override
    public boolean has(Object K) {               // O(n) runtime
        for(int I = 0; I < Z(); I++) {
            if(getLMKey(I).equals(K)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean hasValue(Object V) {          // O(n) runtime
        for(int I = 0; I < Z(); I++) {
            if(getLMValue(I).equals(V)) {
                return true;
            }
        }
        return false;
    }
    // public boolean hasPath(KeyPath P);
    // public boolean hasPathNoCopy(KeyPath P);
    // public boolean containsKey(Object K);
    // public boolean containsValue(Object V);
    @Override
    public boolean contains(Object V) {          // Must override this one
        return hasValue(V);
    }
    @Override
    public boolean containsAll(Collection<?> Vs) {     // O(n^2) runtime
        for(Object V : Vs) {
            if(!hasValue(V)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public int indexOf(Object V) {
        for(int I = 0; I < Z(); I++) {
            if(getLMValue(I).equals(V)) {
                return I;
            }
        }
        return -1;
    }
    @Override
    public int lastIndexOf(Object V) {
        for(int I = Z() - 1; I >= 0; I--) {
            if(getLMValue(I).equals(V)) {
                return I;
            }
        }
        return -1;
    }


    //////////
    // MISC //
    //////////

    @Override
    public Object getSysMeta(Object K) {
        NonTerminal sysMeta = (NonTerminal) getAndSetValid(FConst.SYS_META_KEY, FMap.A(true), NonTerminal.class);
        return sysMeta.get(K);
    }
    @Override
    public Object putSysMeta(Object K, Object V) {
        NonTerminal sysMeta = (NonTerminal) getAndSetValid(FConst.SYS_META_KEY, FMap.A(true), NonTerminal.class);
        return sysMeta.put(K, V);
    }
    // public void compress();
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return null;  // TODO!!!!!!!!
    }
    @Override
    public Object getNextAvailableKey() {
        return getNextAvailableKeyName();
    }
    @Override
    public void changeKey(Object Kcur, Object Knew) { // TODO
        throw new UnsupportedOperationException("AListMap does not support changeKey currently -- but it should!");
    }


    ///////////////
    // TRANSLATE //
    ///////////////

    // Would have been nice to have just implemented
    // Map via ANonTerminal...
    // public Map<Object, Object> toJavaMap();


    ///////////////////////
    // AListMap SPECIFIC //
    ///////////////////////

    private Object getLMKey(int I) {
        KeyValue T = (KeyValue) super.get(I);
        return T.getK();
    }
    private Object getLMValue(int I) {
        KeyValue T = (KeyValue) super.get(I);
        return T.getV();
    }
    @Override
    public void add(int index, Object element) {
        throw new UnsupportedOperationException("AListMap does not support add methods, use put instead.");
    }
    @Override
    public boolean add(Object e) {
        throw new UnsupportedOperationException("AListMap does not support add methods, use put instead.");
    }
    @Override
    public boolean addAll(Collection<? extends Object> c) {
        throw new UnsupportedOperationException("AListMap does not support add methods, use put instead.");
    }
    @Override
    public boolean addAll(int index, Collection<? extends Object> c) {
        throw new UnsupportedOperationException("AListMap does not support add methods, use put instead.");
    }
    @Override
    public Object set(int index, Object element) {
        // [OPTION] Set calls put
        throw new UnsupportedOperationException("AListMap does not support set method, use put instead.");
    }
    @Override
    public boolean remove(Object o) {
        // [OPTION] call removeValue instead
        throw new UnsupportedOperationException("AListMap does not support remove(Object) method.");
    }
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("AListMap does not support removeAll method.");
    }
    public String getNextAvailableKeyName() {
        return getNextAvailableKeyName(null);
    }
    public String getNextAvailableKeyName(String prefix) {
        if(prefix == null) {
            prefix = "key";
        }
        return StringUtil.getNextNumberedString(keySet(), prefix, false);
    }


    ///////////////////////////
    // ANonTerminalGenerator //
    ///////////////////////////

    @Override
    public NonTerminal extract() {
        FMap map = new FMap();
        for(int I = 0; I < Z(); I++) {
            KeyValue T = (KeyValue) super.get(I);
            map.put(T.getK(), T.getV());
        }
        return map;
    }


    ///////////////
    // TO STRING //
    ///////////////

    @Override
    public String toString() {
        return render();
    }
    public String render() {
        StringBuilder buffer = new StringBuilder();
        for(int I = 0; I < size(); I++) {
            KeyValue T = (KeyValue) get(I);
            String K = T.getK() == null ? "null" : T.getK().toString();
            String V = T.getV() == null ? "null" : T.getV().toString();
            buffer.append('[');
            buffer.append(I);
            buffer.append("] ");
            buffer.append(K);
            buffer.append(" => ");
            buffer.append(V);
            buffer.append('\n');
        }
        return buffer.toString();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        FList L3 = new FList(0, "X", 1, "Y", 2, "Z");
        System.out.println(L3);
        System.out.println(L3.asJavaMap());

//        AList L = new AList();
//        L.add("Venus");
//        L.add("Mars");
//
//        AMap M2 = new AMap("A", "1", "B", "2");
//        M2.put("planets", L);
//
//        AListMap M = new AListMap();
//        M.put("Denver", "Colorado");
//        M.put("Sacramento", "California");
//
//        Map javaMap = M.toJavaMap();
//        System.out.println(javaMap.get("Denver"));

//        M.put("next", M2);
//
//        L.trimToSize();
//        M.putSysMeta("class", "xyz");
//
////        System.out.println(M);
////        System.out.println(M.getByPath(KeyPath.KP("next.planets.2", "\\.")));
////        System.out.println(M.getByPath(KeyPath.KP(new Object[] {"next", "planets", 1})));
//
//        System.out.println(M);
//        System.out.println(M.getSysMeta("class"));
    }
}
