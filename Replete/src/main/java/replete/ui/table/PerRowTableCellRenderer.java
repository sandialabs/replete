package replete.ui.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class PerRowTableCellRenderer implements TableCellRenderer {


    ///////////
    // FIELD //
    ///////////

    private PerRowTableCellRendererGenerator generator;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PerRowTableCellRenderer(PerRowTableCellRendererGenerator gen) {
        generator = gen;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        TableCellRenderer chosen = generator.getRenderer(row);
        return chosen.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
