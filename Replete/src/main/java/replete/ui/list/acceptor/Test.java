package replete.ui.list.acceptor;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import replete.ui.debug.DebugActionListener;
import replete.ui.debug.DebugItemListener;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;


/**
 * @author Derek Trumbo
 */

public class Test {

    public static void main(String[] args) {
        final JFrame frame = new EscapeFrame();

        JList xl = new JList(new String[] {"Mars", "Jupiter", "What", "ABC"});
        xl.setPreferredSize(new Dimension(200, 1));
        xl.setSelectionModel(new AcceptorListSelectionModel(new ListSelectionAcceptor() {
            public boolean accept(int type, int index0, int index1) {
                return Dialogs.showConfirm(frame, "allow?");
            }
        }));
        xl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //
        AcceptorComboBox xc = new AcceptorComboBox(new String[] {"Mars", "Jupiter", "What", "ABC"});
        xc.setPreferredSize(new Dimension(200, 1));
        xc.addActionListener(new DebugActionListener());
        xc.addItemListener(new DebugItemListener());
        xc.setSelectionAcceptor(new ComboSelectionAcceptor() {
            public boolean accept(Object newSel) {
                return Dialogs.showConfirm(frame, "allow?");
            }
        });

        //
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(xl), BorderLayout.WEST);
        frame.add(xc, BorderLayout.EAST);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
