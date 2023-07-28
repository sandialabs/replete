package replete.ui.table;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class PerRowTableCellEditor extends AbstractCellEditor implements TableCellEditor {


    ////////////
    // FIELDS //
    ////////////

    private PerRowTableCellEditorGenerator generator;
    private TableCellEditor chosen;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PerRowTableCellEditor(PerRowTableCellEditorGenerator gen) {
        generator = gen;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Object getCellEditorValue() {
        return chosen.getCellEditorValue();
    }
    @Override
    public boolean stopCellEditing() {
        return chosen.stopCellEditing();
    }
    @Override
    public void cancelCellEditing() {
        chosen.cancelCellEditing();
        super.cancelCellEditing();
    }
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
        chosen = generator.getEditor(row);
        return chosen.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
    @Override
    public void addCellEditorListener(CellEditorListener l) {
        chosen.addCellEditorListener(l);
        super.addCellEditorListener(l);
    }
    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        chosen.removeCellEditorListener(l);
        super.removeCellEditorListener(l);
    }
}
