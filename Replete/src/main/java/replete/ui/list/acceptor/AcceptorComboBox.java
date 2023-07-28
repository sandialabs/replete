package replete.ui.list.acceptor;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author Derek Trumbo
 */

public class AcceptorComboBox extends JComboBox {

    private ComboSelectionAcceptor acceptor;

    public AcceptorComboBox(ComboBoxModel aModel) {
        super(aModel);
    }
    public AcceptorComboBox(final Object items[]) {
        super(items);
    }
    public AcceptorComboBox(Vector<?> items) {
        super(items);
    }
    public AcceptorComboBox() {
        super();
    }

    public ComboSelectionAcceptor getAcceptor() {
        return acceptor;
    }
    public void setSelectionAcceptor(ComboSelectionAcceptor acc) {
        acceptor = acc;
    }

    // To set the selected item of the combo box.  This
    // is the method used by the Swing UI when the user
    // changes the selection.
    @Override
    public void setSelectedItem(Object anObject) {
        if(acceptor == null || acceptor.accept(anObject)) {
            super.setSelectedItem(anObject);
        }
    }

    // To set the selected item programmatically without
    // invoking the acceptor.
    public void setSelectedItemWithoutAccept(Object anObject) {
        super.setSelectedItem(anObject);
    }
}