package replete.ui.table;

import javax.swing.table.TableCellEditor;

public interface PerRowTableCellEditorGenerator {
    public TableCellEditor getEditor(int row);
}
