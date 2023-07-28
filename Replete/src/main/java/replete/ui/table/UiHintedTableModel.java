package replete.ui.table;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public interface UiHintedTableModel {
    public Color             getForegroundColor(int row, int col);
    public Color             getBackgroundColor(int row, int col);
    public Color             getSelectionBackgroundColor(int row, int col);
    public Border            getBorder(int row, int col);
    public Border            getSelectionBorder(int row, int col);
    public Icon              getIcon(int row, int col);
    public int               getRowHeight(int row);
    public Insets            getInsets(int row, int col);
    public int               getAlignment(int row, int col);
    public Font              getFont(int row, int col);
    public Boolean           isBold(int row, int col);
    public Boolean           isItalic(int row, int col);
    public TableCellRenderer getRenderer(int row, int col);
    public DefaultCellEditor getEditor(int row, int col);
    public Cursor            getCursor(int row, int col);
}
