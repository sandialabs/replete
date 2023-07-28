package replete.ui.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public abstract class RTableModel extends DefaultTableModel {


    ///////////
    // FIELD //
    ///////////

    private List<Object[]> colInfo = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTableModel() {
        super();
        init();
    }
    public RTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
        init();
    }
    public RTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
        init();
    }
    public RTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        init();
    }
    public RTableModel(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);
        init();
    }
    public RTableModel(Vector data, Vector columnNames) {
        super(data, columnNames);
        init();
    }


    /////////////
    // COLUMNS //
    /////////////

    protected void init() {}
    public int addColumn(String colName) {
        return addColumn(colName, String.class, null);
    }
    public int addColumn(String colName, Class<?> colType) {
        return addColumn(colName, colType, null);
    }
    public int addColumn(String colName, int[] widths) {
        return addColumn(colName, String.class, widths);
    }
    public int addColumn(String colName, int allW) {
        return addColumn(colName, String.class, new int[] {allW, allW, allW});
    }
    public int addColumn(String colName, int minW, int prefW, int maxW) {
        return addColumn(colName, String.class, new int[] {minW, prefW, maxW});
    }
    public int addColumn(String colName, Class<?> colType, int allW) {
        return addColumn(colName, colType, new int[] {allW, allW, allW});
    }
    public int addColumn(String colName, Class<?> colType, int minW, int prefW, int maxW) {
        return addColumn(colName, colType, new int[] {minW, prefW, maxW});
    }
    public int addColumn(String colName, Class<?> colType, int[] widths) {
        colInfo.add(new Object[] {colName, colType, widths});
        return colInfo.size() - 1;
    }


    ////////////////
    // OVERRIDDEN // (Some trivial implementations, some based off of RTableModel fields)
    ////////////////

    @Override
    public Class<?> getColumnClass(int col) {
        if(colInfo == null || colInfo.isEmpty()) {
            return String.class;
        }
        return (Class<?>) colInfo.get(col)[1];
    }
    @Override
    public int getColumnCount() {
        if(colInfo == null || colInfo.isEmpty()) {
            return 0;
        }
        return colInfo.size();
    }
    @Override
    public String getColumnName(int col) {
        if(colInfo == null || colInfo.isEmpty()) {
            return "";
        }
        return (String) colInfo.get(col)[0];
    }
    public int[] getColumnWidth(int col) {      // New in RTableModel
        if(colInfo == null || colInfo.isEmpty()) {
            return null;
        }
        return (int[]) colInfo.get(col)[2];
    }
    @Override
    public int getRowCount() {
        return 0;
    }
    @Override
    public Object getValueAt(int row, int col) {
        return null;
    }
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    @Override
    public void setValueAt(Object value, int row, int col) {

    }
}
