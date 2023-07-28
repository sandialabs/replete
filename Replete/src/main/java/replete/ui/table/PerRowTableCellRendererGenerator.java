package replete.ui.table;

import javax.swing.table.TableCellRenderer;

public interface PerRowTableCellRendererGenerator {
    public TableCellRenderer getRenderer(int row);
}
