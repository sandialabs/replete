package replete.ui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import replete.ui.lay.Lay;

public class JButtonTableCellEditor extends DefaultCellEditor {


    ////////////
    // FIELDS //
    ////////////

    protected JButton btn;
    private String label;
    private boolean isPushed;
    private Object lastValue;
    private int lastModelRow;
    private int lastCol;
    private JButtonTableCellEditorActionListener cellActionListener;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public JButtonTableCellEditor() {
        this(new JButton());
    }
    public JButtonTableCellEditor(JButton btn) {
        this(btn, null);
    }
    public JButtonTableCellEditor(JButton btn, JButtonTableCellEditorActionListener cellActionListener) {
        super(new JCheckBox());                  // Just a technicality
        this.btn = btn;
        this.cellActionListener = cellActionListener;
        btn.addActionListener(e -> fireEditingStopped());
    }

    public JButtonTableCellEditor(JButtonTableCellEditorActionListener cellActionListener) {
        this(null, null, cellActionListener);
    }
    public JButtonTableCellEditor(Icon icon, JButtonTableCellEditorActionListener cellActionListener) {
        this(null, icon, cellActionListener);
    }
    public JButtonTableCellEditor(String text) {
        this(text, null, null);
    }
    public JButtonTableCellEditor(String text, Icon icon) {
        this(text, icon, null);
    }
    public JButtonTableCellEditor(String text, Icon icon, JButtonTableCellEditorActionListener cellActionListener) {
        super(new JCheckBox());                  // Just a technicality
        this.cellActionListener = cellActionListener;

        btn = Lay.btn(text, icon);
        btn.addActionListener(new ActionListener() {   // Does the main ActionListener *have* to be attached here?
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public JButton getButton() {
        return btn;
    }

    // Mutators

    public JButtonTableCellEditor setText(String text) {
        btn.setText(text);
        return this;
    }
    public JButtonTableCellEditor setIcon(Icon icon) {
        btn.setIcon(icon);
        return this;
    }
    public JButtonTableCellEditor addActionListener(ActionListener listener) {
        btn.addActionListener(listener);
        return this;
    }
    public JButtonTableCellEditor setCellActionListener(JButtonTableCellEditorActionListener cellActionListener) {
        this.cellActionListener = cellActionListener;
        return this;
    }


    //////////
    // MISC //
    //////////

    public boolean useValueAsText() {
        return false;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int visualRow, int column) {
        int modelRow = table.convertRowIndexToModel(visualRow);

        if(isSelected) {
            btn.setForeground(table.getSelectionForeground());
            btn.setBackground(table.getSelectionBackground());
        } else {
            btn.setForeground(table.getForeground());
            btn.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        if(useValueAsText()) {
            btn.setText(label);
        }

        isPushed = true;
        lastValue = value;
        lastModelRow = modelRow;
        lastCol = column;

        return btn;
    }

    @Override
    public Object getCellEditorValue() {
        if(isPushed) {
            cellActionListener.actionPerformed(lastValue, lastModelRow, lastCol);
        }
        isPushed = false;
        return label;
    }
    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}
