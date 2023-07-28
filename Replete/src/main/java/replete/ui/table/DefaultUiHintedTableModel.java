package replete.ui.table;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

import replete.text.StringUtil;
import replete.ui.lay.Lay;
import replete.util.ReflectionUtil;

public class DefaultUiHintedTableModel extends RTableModel implements UiHintedTableModel {


    ////////////
    // FIELDS //
    ////////////

    private Map<Long, JButton> buttons = new HashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DefaultUiHintedTableModel() {
        addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                synchronized(buttons) {
                    buttons.clear();
                }
            }
        });
    }


    ///////////////////////////////////////
    // UI HINTED DEFAULT IMPLEMENTATIONS //
    ///////////////////////////////////////

    @Override
    public Color getForegroundColor(int row, int col) {
        return null;
    }
    @Override
    public Color getBackgroundColor(int row, int col) {
        return null;
    }
    @Override
    public Color getSelectionBackgroundColor(int row, int col) {
        return null;
    }
    @Override
    public Border getBorder(int row, int col) {
        return null;
    }
    @Override
    public Border getSelectionBorder(int row, int col) {
        return null;
    }
    @Override
    public Icon getIcon(int row, int col) {
        return null;
    }
    @Override
    public int getRowHeight(int row) {
        return -1;
    }
    @Override
    public Insets getInsets(int row, int col) {
        return null;
    }
    @Override
    public int getAlignment(int row, int col) {
        return -1;
    }
    @Override
    public Font getFont(int row, int col) {
        return null;
    }
    @Override
    public Boolean isBold(int row, int col) {
        return false;
    }
    @Override
    public Boolean isItalic(int row, int col) {
        return false;
    }
    @Override
    public TableCellRenderer getRenderer(int row, int col) {
        JButton btn = getButtonWrapper(row, col);
        if(btn != null) {
            return new JButtonTableCellRenderer(btn);
        }
        return null;
    }
    @Override
    public DefaultCellEditor getEditor(int row, int col) {
        final JButton btn = getButtonWrapper(row, col);
        if(btn != null) {
            JButtonTableCellEditorActionListener editorActionListener =
                (value, r, c) -> ReflectionUtil.invoke(btn, "fireStateChanged");
            JButtonTableCellEditor editor =
                new JButtonTableCellEditor(btn, editorActionListener);
            Lay.hn(btn, "cursor=hand");
            return editor;
        }
        return null;
    }
    @Override
    public Cursor getCursor(int row, int col) {
        JButton btn = getButtonWrapper(row, col);
        if(btn != null) {
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
        return null;
    }


    ///////////////////////////
    // TABLE MODEL OVERRIDES //
    ///////////////////////////

    // Although debatable, the presence of a button kind of
    // implies that the "value" doesn't matter as much as
    // far as a "table model" goes.  Here we take the toString
    // of the value and append other strings related to the
    // button so that filtering
    @Override
    public Object getValueAt(int row, int col) {
        JButton btn = getButtonWrapper(row, col);
        Object baseVal = getValueAtInner(row, col);
        if(btn != null) {
            String b1 = StringUtil.cleanNull(baseVal);
            String b2 = btn.getText();
            String b3 = "button,BUTTON,Button,BUtton,buttoN";  // TODO: make this simpler somehow
            return b1 + "|" + b2 + "|" + b3;
        }
        return baseVal;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        JButton btn = getButtonWrapper(row, col);
        if(btn != null) {
            return true;
        }
        return super.isCellEditable(row, col);
    }

    protected Object getValueAtInner(int row, int col) {
        return super.getValueAt(row, col);
    }


    //////////
    // MISC //
    //////////

    // Additional capability to enable easy buttons. If overriding
    // this method, dangerous to override these methods:
    //    getRenderer, getEditor, getCursor, isCellEditable, getValueAt (!)
    // Technically we could avoid this if developers had an option to
    // override a "Inner" method for each of the above which would only
    // be called if there was no button located at that cell location so
    // the developer would have less of a chance of breaking class
    // hierarchy functionality.
    protected JButton getButton(int row, int col) {
        return null;
    }
    private JButton getButtonWrapper(int row, int col) {
        long both = ((long) row << 32) + col;
        synchronized(buttons) {
            JButton btn = buttons.get(both);
            if(btn == null) {
                btn = getButton(row, col);
                buttons.put(both, btn);
            }
            return btn;
        }
    }
}
