package replete.collections;

import java.util.ArrayList;
import java.util.Collection;

public class RArrayList<T> extends ArrayList<T> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RArrayList() {}
//    public RArrayList(int initialCapacity) {
//        super(initialCapacity);
//    }
    public RArrayList(Collection<? extends T> c) {
        super(c);
    }
    public RArrayList(T... elements) {
        for(T elem : elements) {
            add(elem);
        }
    }


    //////////
    // MISC //
    //////////

    // NULL padding

    public void grow(int size) {
        while(size() < size) {
            add((T) null);
        }
    }
    public void shrink() {
        while(size() > 0 && get(size() - 1) == null) {
            remove(size() - 1);
        }
    }

    // Robust gets, sets, and adds

    public T getOrNull(int index) {
        if(index < -1 || index >= size()) {
            return null;
        }
        return get(index);
    }

    public T getWithGrow(int index) {
        grow(index + 1);
        return get(index);
    }

    public T setWithGrow(int index, T element) {
        grow(index + 1);
        return set(index, element);
    }
    public void addWithGrow(int index, T element) {
        grow(index);
        add(index, element);
    }
    public void addIfNonNullWithGrow(int index, T element) {
        if(index >= size() && element != null || index < size()) {
            addWithGrow(index, element);
        }
    }

    public RArrayList add(T... elems) {
        for(T elem : elems) {
            add(elem);
        }
        return this;
    }

    // Other

    public T setIfExists(int index, T element) {
        if(index >= 0 && index < size()) {
            return set(index, element);
        }
        return null;
    }
}
