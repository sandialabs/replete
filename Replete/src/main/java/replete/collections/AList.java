package replete.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class AList<T> extends ArrayList<T> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AList() {
        super();
    }
    public AList(Collection<? extends T> c) {
        super(c);
    }
    public AList(int initialCapacity) {
        super(initialCapacity);
    }


    ///////////////////
    // HANDY METHODS //
    ///////////////////

    // [OPTION] For each method, you technically have two choices for implementation:
    // Errors throw exceptions, or errors cause null/-1 return values.
    // Blocking versions have a third choice potentially: block until data
    // becomes available.

    // --> Inspecting the ends of the list.

    // Alternate names: head, bottom, front, peekFirst, peekFront, getFirst, etc.
    public T first() {
        return super.get(0);        // Throw specific exceptions for bad indices?
    }
    // Alternate names: tail, peek, top, back, end, peekLast, peekBack, getLast, etc.
    public T last() {
        return super.get(size() - 1);
    }

    // --> Adding to the ends of the list.

    // Alternate names: pushFirst, pushFront, insert(0, *), unshift, etc.
    public void prepend(T E) {
        super.add(0, E);
    }
    // Alternate names: join, combine
    public void prependList(Collection<T> L) {
        super.addAll(0, L);
    }
    public void prependList(T... L) {
        super.addAll(0, Arrays.asList(L));
    }
    // Alternate names: add, push, pushLast, pushBack, add, enqueue, etc.
    public void append(T E) {
        super.add(E);
    }
    // Alternate names: join, combine
    public void appendList(Collection<T> L) {
        super.addAll(L);
    }
    public void appendList(T... L) {
        super.addAll(Arrays.asList(L));
    }
    // Could also have, adding to a specific position in the list.

    // --> Removing from the ends of the list.

    // Alternate names: popFirst, popFront, dequeue, shift, etc.
    public T removeFirst() {
        return super.remove(0);
    }
    // Alternate names: pop, popLast, popBack, etc.
    public T removeLast() {
        return super.remove(size() - 1);
    }
    // Could also have, removing from a specific position in the list.
    // or removing a range.

    // Convenience Forms

    // Could have forms of append,prepend,removeFirst,removeLast
    // that return this instead of void|object removed.  Some
    // programming languages prefer this construct by default.

    // Modifying, 'this' reference returning

    public AList<T> prepended(T E) {
        prepend(E);
        return this;
    }
    public AList<T> appended(T E) {
        append(E);
        return this;
    }
    public AList<T> removedFirst() {
        removeFirst();
        return this;
    }
    public AList<T> removedLast() {
        removeLast();
        return this;
    }

    // Non-modifying, returns a modified copy of the list.

    public AList<T> withFirst(T E) {
        AList<T> L = new AList<>(this);
        L.prepend(E);
        return L;
    }
    public AList<T> withLast(T E) {
        AList<T> L = new AList<>(this);
        L.append(E);
        return L;
    }
    public AList<T> withoutFirst() {
        AList<T> L = new AList<>(this);
        L.removeFirst();
        return L;
    }
    public AList<T> withoutLast() {
        AList<T> L = new AList<>(this);
        L.removeLast();
        return L;
    }

    // Seriously tired of private fields!!!  Could have bypassed the get methods...
    // Alternate names: flip
    public void reverse() {
        int sz = size();
        int mid = sz / 2;
        for(int i = 0; i < mid; i++) {
            int j = sz - i - 1;
            T early = super.get(i);
            super.set(i, super.get(j));
            super.set(j, early);
        }
    }

    public AList reversed() {
        reverse();
        return this;
    }
}
