package replete.ui.table;

// Allows someone to more-or-less use the quick JTable constructor
//     JTable(Object[][] rowData, Object[] colNames)
// but NOT have the model created from it be editable.
// Example:
//     new JTable(new ReadOnlyQuickTableModel(rowData, colNames))
// DefaultUiHintedTableModel is not editable by default.

public class ReadOnlyQuickTableModel extends DefaultUiHintedTableModel {

    private Object[][] rowData;
    private Object[] colNames;

    public ReadOnlyQuickTableModel(Object[][] rowData, Object[] colNames) {
        this.rowData = rowData;
        this.colNames = colNames;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }
    @Override
    public String getColumnName(int col) {
        return colNames[col] == null ? "" : colNames[col].toString();
    }
    @Override
    public Class<?> getColumnClass(int col) {
        return String.class;
    }

    @Override
    public int getRowCount() {
        if(rowData == null) {
            return 0;
        }
        return rowData.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rowData[rowIndex][columnIndex];
    }

}
