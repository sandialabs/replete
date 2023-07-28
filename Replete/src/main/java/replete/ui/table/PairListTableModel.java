package replete.ui.table;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import replete.collections.Pair;
import replete.ui.windows.Dialogs;

/**
 * A table model for displaying Pairs of Strings.
 * @author Elliott Ridgway
 *
 */
public class PairListTableModel extends DefaultTableModel {


    ////////////
    // FIELDS //
    ////////////

    private Component parent;
    private String[] headers;
    List<Pair<String, String>> entries = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PairListTableModel(Component parent, String[] headers) {
        this.parent = parent;
        this.headers = headers;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessor

    public List<Pair<String, String>> getEntries() {
        return entries;
    }

    // Mutators

    public void setEntries(List<Pair<String, String>> entries) {
        this.entries = entries;
        fireTableDataChanged();
    }
    public void addEntry(String key, String value) {
        entries.add(new Pair(key, value));
        fireTableDataChanged();
    }
    public void removeEntry(int i) {
        entries.remove(i);
        fireTableDataChanged();
    }
    public void clearEntries() {
        entries.clear();
        fireTableDataChanged();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void removeRow(int row) {
        removeEntry(row);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return String.class;
    }
    @Override
    public int getColumnCount() {
        return headers.length;
    }
    @Override
    public String getColumnName(int column) {
        return headers[column];
    }
    @Override
    public int getRowCount() {
        if(entries == null) {
            return 0;
        }
        return entries.size();
    }
    @Override
    public Object getValueAt(int row, int column) {
        Pair<String, String> entry = entries.get(row);
        if(column == 0) {
            return entry.getValue1();
        }
        return entry.getValue2();
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
    @Override
    public void setValueAt(Object value, int row, int column) {
        Pair<String, String> entry = entries.get(row);
        if(column == 0) {
            String val = ((String) value).trim();
            if(val.equals("")) {
                Dialogs.showWarning(parent,
                    headers[0] + " must not be blank.", "Warning");
            } else {
                entry.setValue1(val);
            }
        } else {
            String val = ((String) value).trim();
            if(val.equals("")) {
                Dialogs.showWarning(parent,
                    headers[1] + " must not be blank.", "Warning");
            } else {
                entry.setValue2(val);
            }
        }
    }
}