package replete.ui.table;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class DefaultReadOnlyTableModel extends DefaultTableModel {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DefaultReadOnlyTableModel() {}
    public DefaultReadOnlyTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }
    public DefaultReadOnlyTableModel(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);
    }
    public DefaultReadOnlyTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }
    public DefaultReadOnlyTableModel(Vector data, Vector columnNames) {
        super(data, columnNames);
    }
    public DefaultReadOnlyTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
