package replete.ui.progress;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;


/**
 * @author Derek Trumbo
 */

public class WarningProgressBar extends JProgressBar {


    ////////////
    // FIELDS //
    ////////////

    protected Color fgSave;
    protected int limit = Integer.MAX_VALUE;
    protected Color warningColor = Color.red;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Use setModel / setOrientation to set the properties
    // normally provided by other JProgressBar constructors.

    public WarningProgressBar() {
        fgSave = getForeground();
    }
    public WarningProgressBar(int lim) {
        limit = lim;
        fgSave = getForeground();
    }
    public WarningProgressBar(int min, int max, int lim) {
        super(min, max);
        limit = lim;
        fgSave = getForeground();
    }
    public WarningProgressBar(BoundedRangeModel newModel) {
        super(newModel);
        limit = newModel.getMaximum();
        fgSave = getForeground();
    }
    public WarningProgressBar(int min, int max) {
        super(min, max);
        limit = max;
        fgSave = getForeground();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    @Override
    public void setForeground(Color c) {
        super.setForeground(c);
        fgSave = c;
    }

    public void setWarningLimit(int lim) {
        limit = lim;
        updateForegroundColor();
    }
    public int getWarningLimit() {
        return limit;
    }
    public void setWarningColor(Color clr) {
        warningColor = clr;
        updateForegroundColor();
    }
    public Color getWarningColor() {
        return warningColor;
    }

    protected void setWarningForeground() {
        super.setForeground(warningColor);
    }

    @Override
    public void setValue(int x) {
        super.setValue(x);
        updateForegroundColor();
    }

    protected void updateForegroundColor() {
        if(getValue() >= limit) {
            setWarningForeground();
        } else {
            setForeground(fgSave);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

        final JTextField txtMin = Lay.tx(  "0", 10, "selectall");
        final JTextField txtMax = Lay.tx("100", 10, "selectall");
        final JTextField txtVal = Lay.tx( "80", 10, "selectall");
        final JTextField txtLim = Lay.tx( "70", 10, "selectall");
        final WarningProgressBar pgb = new WarningProgressBar(0, 100, 70);
        pgb.setValue(80);
        pgb.setStringPainted(true);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pgb.setMinimum(Integer.parseInt(txtMin.getText()));
                pgb.setMaximum(Integer.parseInt(txtMax.getText()));
                pgb.setValue(Integer.parseInt(txtVal.getText()));
                pgb.setWarningLimit(Integer.parseInt(txtLim.getText()));
                pgb.setWarningColor(Color.blue);

                txtVal.requestFocusInWindow();
                txtVal.selectAll();
            }
        });

        EscapeFrame frame = new EscapeFrame("WarningProgressBar");
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        frame.add(new JLabel("Min Val:"), c);
        c.gridy = 1;
        frame.add(new JLabel("Max Val:"), c);
        c.gridy = 2;
        frame.add(new JLabel("Cur Val:"), c);
        c.gridy = 3;
        frame.add(new JLabel("Limit:"), c);
        c.gridx = 1;
        c.gridy = 0;
        frame.add(txtMin, c);
        c.gridy = 1;
        frame.add(txtMax, c);
        c.gridy = 2;
        frame.add(txtVal, c);
        c.gridy = 3;
        frame.add(txtLim, c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        frame.add(btnUpdate, c);
        c.gridy = 5;
        frame.add(pgb, c);

        frame.setDefaultButton(btnUpdate);

        Lay.hn(frame, "pack");
        Lay.hn(frame, "size=[" +
            (frame.getWidth() + 50) + "," +
            frame.getHeight() + "],center=2,visible");
    }
}
