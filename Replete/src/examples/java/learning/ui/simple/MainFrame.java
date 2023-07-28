package learning.ui.simple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class MainFrame extends EscapeFrame {

    private TableModel mdlDogs;

    public MainFrame() {
        super("Test Frame");

        initSwingUi();
//        initLayUi();

        setSize(700, 700);
        setLocationRelativeTo(null);
    }

    private void initLayUi() {

        JButton btnClose;

        DefaultListModel<String> mdlFruits;

        mdlDogs = new DogsTableModel();

        Lay.BLtg(this,
            "N", Lay.lb(
                "<html>This is the top label <font color='red'>laksd flska fjlskafj</font> slkaf jlksfjlks jflksjf lksaj flksa jflksja fls ajlfslkfj salkf klsad dfjl</html>",
                "bg=yellow"
            ),
            "W", Lay.BL(
                Lay.sp(Lay.lst(mdlFruits = createListData()), "prefw=200"),
                "bg=blue,eb=5tb20l"
            ),
            "C", Lay.sp(Lay.tbl(mdlDogs)),
            "S", Lay.FL("R",
                Lay.btn("&Save"),
                btnClose = Lay.btn("&Close"),
                "bg=green"
            )
        );

        btnClose.addActionListener(e -> dispose());   // Using lambda expression
    }

    private void initSwingUi() {
        setLayout(new BorderLayout());     // default hgap & vgap = 0

//        pnlButtons.setLayout(new FlowLayout());
        JButton btnSave = new JButton("Save");
        btnSave.setMnemonic('S');
        JButton btnClose = new JButton("Close");
        btnClose.setMnemonic('C');
        btnClose.addActionListener(e -> dispose());   // Using lambda expression

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));  // default hgap & vgap = 5
        pnlButtons.setBackground(Color.green);
        pnlButtons.add(btnSave);
        pnlButtons.add(btnClose);

        // JLabels can show HTML: good for italics, bold, underline, foreground colors, and wrapping.
        JLabel lblTop = new JLabel("<html>This is the top label <font color='red'>laksd flska fjlskafj</font> slkaf jlksfjlks jflksjf lksaj flksa jflksja fls ajlfslkfj salkf klsad dfjl</html>");
        lblTop.setBackground(Color.yellow);
        lblTop.setOpaque(true);

        DefaultListModel<String> mdlFruits = createListData();

        JList lstFruits = new JList(mdlFruits);
        JScrollPane scrFruits = new JScrollPane(lstFruits);
        scrFruits.setPreferredSize(new Dimension(200, 10));
        JPanel pnlFruitsWrapper = new JPanel(new BorderLayout());
        pnlFruitsWrapper.add(scrFruits, BorderLayout.CENTER);
        Border emptyBorder = BorderFactory.createEmptyBorder(5, 20, 5, 0);
        pnlFruitsWrapper.setBorder(emptyBorder);
        pnlFruitsWrapper.setBackground(Color.blue);

//        Object[][] data = new Object[][] {
//            {"Fido", "Golden Retriever", 10},
//            {"Lily", "Daschund",          2}
//        };
//        Object[] headers = new Object[] {"Name", "Breed", "Age"};

        TableModel mdlDogs = new DogsTableModel();
        JTable tblDogs = new JTable(mdlDogs);
        JScrollPane scrDogs = new JScrollPane(tblDogs);

        // list boxes, combo boxes, trees, and tables require models
        // list boxes, trees, and tables require scroll panes

        add(lblTop, BorderLayout.NORTH);
        add(pnlFruitsWrapper, BorderLayout.WEST);
        add(scrDogs, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private DefaultListModel<String> createListData() {
        DefaultListModel<String> mdlFruits = new DefaultListModel<>();
        mdlFruits.addElement("Apple");
        mdlFruits.addElement("Banana");
        mdlFruits.addElement("Orange");
        mdlFruits.addElement("Apple");
        mdlFruits.addElement("Banana");
        mdlFruits.addElement("Orange");
        mdlFruits.addElement("Apple");
        mdlFruits.addElement("Banana");
        mdlFruits.addElement("Orange");
        return mdlFruits;
    }

    private class DogsTableModel extends DefaultTableModel {
        private Object[][] data = new Object[][] {
            {"Fido", "Golden Retriever", 10},
            {"Lily", "Daschund",          2}
        };
        private String[] headers = new String[] {"Name", "Breed", "Age"};

        DogsTableModel() {
            super();  /// <----- gets called before your object initializes
        }

//        public Object[] getDataForData(int row) {
//
//        }

        public void setMyData(Object[][] newData) {   // (int index, Object newData) {
            // update our model (usually the local intstance variables)
            fireTableDataChanged();  // <-- extending DefaultTableModel gives us this
        }

        @Override
        public int getRowCount() {
            if(data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public int getColumnCount() {
            if(headers == null) {
                return 0;
            }
            return headers.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if(headers == null) {
                return null;
            }
            return headers[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch(columnIndex) {
                case 0: return String.class;
                case 1: return String.class;
                case 2: return Integer.class;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if(columnIndex == 1 && rowIndex == 0) {
                return true;
            }
            return false;  // can use a switch here too
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
//            return data[rowIndex][columnIndex];    // <-- Yes, in this particular toy problem
            switch(columnIndex) {
                case 0: return data[rowIndex][0];
                case 1: return data[rowIndex][1];
                case 2: return data[rowIndex][2];
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

            // called only if the given cell was editable and the user attempted to edit the value
            System.out.println("user tried to change the value!  haha sucker no such luck");
        }
    }
}
