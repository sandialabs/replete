package replete.ui.list.acceptor;

import javax.swing.DefaultListSelectionModel;

/**
 * @author Derek Trumbo
 */

public class AcceptorListSelectionModel extends DefaultListSelectionModel {
    public static final int ADD = 0;
    public static final int REMOVE = 0;
    public static final int SET = 0;

    private ListSelectionAcceptor acceptor;

    public AcceptorListSelectionModel(ListSelectionAcceptor a) {
        if(a == null) {
            throw new IllegalArgumentException();
        }
        acceptor = a;
    }

    @Override
    public void addSelectionInterval(int index0, int index1) {
        if(acceptor.accept(ADD, index0, index1)) {
            super.addSelectionInterval(index0, index1);
        }
    }

    @Override
    public void removeSelectionInterval(int index0, int index1) {
        if(acceptor.accept(REMOVE, index0, index1)) {
            super.removeSelectionInterval(index0, index1);
        }
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if(acceptor.accept(SET, index0, index1)) {
            super.setSelectionInterval(index0, index1);
        }
    }
}
