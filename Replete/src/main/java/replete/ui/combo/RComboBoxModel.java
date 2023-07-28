package replete.ui.combo;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

// TODO: Have this class implement Collection<T> for additional
// ease of usage.

public class RComboBoxModel<T> extends DefaultComboBoxModel<T> implements Iterable<T> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RComboBoxModel() {
        super();
    }
    public RComboBoxModel(T[] items) {
        super(items);
    }
    public RComboBoxModel(Vector<T> v) {
        super(v);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Iterator<T> iterator() {
        return new RComboBoxModelIterator();
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class RComboBoxModelIterator implements Iterator<T> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < getSize();
        }
        @Override
        public T next() {
            return getElementAt(index++);
        }
    }
}
