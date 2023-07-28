package replete.ui.list;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

// TODO: Have this class implement Collection<T> for additional
// ease of usage.

public class RListModel<T> extends DefaultListModel<T> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RListModel() {
        super();
    }
    public RListModel(Iterable<T> elements) {
        setElements(elements);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for(int p = 0; p < size(); p++) {
            list.add(getElementAt(p));
        }
        return list;
    }

    // Mutators

    public void setElements(Iterable<T> elements) {
        clear();
        for(T elem : elements) {
            addElement(elem);
        }
    }
}
