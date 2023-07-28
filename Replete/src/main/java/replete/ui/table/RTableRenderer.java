package replete.ui.table;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RTableRenderer extends DefaultTableCellRenderer {


    ////////////
    // FIELDS //
    ////////////

    protected Insets insetsRenderer;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTableRenderer() {
    }
    public RTableRenderer(int insets) {
        setInsetsRenderer(insets);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public Insets getInsetsRenderer() {
        return insetsRenderer;
    }
    public void setInsetsRenderer(int i) {
        setInsetsRenderer(new Insets(i, i, i, i));
    }
    public void setInsetsRenderer(Insets i) {
        insetsRenderer = i;
    }
    public void setRightAlignment() {
        setHorizontalAlignment(JLabel.RIGHT);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int visualRow, int column) {
        JLabel lbl = (JLabel) super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, visualRow, column );
        // No translation from visual row to model row needed here.
        if(insetsRenderer != null) {
            lbl.setBorder(BorderFactory.createCompoundBorder(lbl.getBorder(),
                BorderFactory.createEmptyBorder(
                    insetsRenderer.top,
                    insetsRenderer.left,
                    insetsRenderer.bottom,
                    insetsRenderer.right
                )
            ));
        }
        return lbl;
    }
}
