package replete.ui.table;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;

public class JButtonTableCellRenderer implements TableCellRenderer {


    ///////////
    // FIELD //
    ///////////

    private JButton btn;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public JButtonTableCellRenderer() {
        btn = new JButton();
    }
    public JButtonTableCellRenderer(JButton btn) {
        this.btn = btn;
    }
    public JButtonTableCellRenderer(String text) {
        this(text, null, null);
    }
    public JButtonTableCellRenderer(String text, Icon icon) {
        this(text, icon, null);
    }
    public JButtonTableCellRenderer(String text, Icon icon, ActionListener listener) {
        btn = Lay.btn(text, icon, listener);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public JButton getButton() {
        return btn;
    }

    // Mutators

    public JButtonTableCellRenderer setText(String text) {
        btn.setText(text);
        return this;
    }
    public JButtonTableCellRenderer setIcon(Icon icon) {
        btn.setIcon(icon);
        return this;
    }
    public JButtonTableCellRenderer setIcon(ImageModelConcept concept) {
        btn.setIcon(ImageLib.get(concept));
        return this;
    }
    public JButtonTableCellRenderer addActionListener(ActionListener listener) {
        btn.addActionListener(listener);
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
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if(isSelected) {
            btn.setForeground(table.getSelectionForeground());
            btn.setBackground(table.getSelectionBackground());
        } else {
            btn.setForeground(table.getForeground());
            btn.setBackground(UIManager.getColor("Button.background"));
        }
        if(useValueAsText()) {
            btn.setText((value == null) ? "" : value.toString());
        }
        return btn;
    }
}
