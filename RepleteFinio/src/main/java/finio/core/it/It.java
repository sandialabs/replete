package finio.core.it;

import java.util.Iterator;

public class It<T> implements Iterable<T> {


    ///////////
    // FIELD //
    ///////////

    private Iterator<T> it;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public It(Iterator<T> it) {
        this.it = it;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    public Iterator<T> iterator() {
        return it;
    }
}
