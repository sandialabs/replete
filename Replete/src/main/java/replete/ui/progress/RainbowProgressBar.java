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

// Could highly configure this class with mutators
// for color and ranges.

public class RainbowProgressBar extends JProgressBar {


    ////////////
    // FIELDS //
    ////////////

    private static final Color progStart = Lay.clr("E57200");
    private static final Color progMid = Lay.clr("F1E004");
    private static final Color progEnd = Lay.clr("8DD600");

    protected int limit = Integer.MAX_VALUE;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Use setModel / setOrientation to set the properties
    // normally provided by other JProgressBar constructors.

    public RainbowProgressBar() {
    }
    public RainbowProgressBar(BoundedRangeModel newModel) {
        super(newModel);
    }
    public RainbowProgressBar(int min, int max) {
        super(min, max);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    @Override
    public void setValue(int x) {
        super.setValue(x);
        updateForegroundColor();
    }

    @Override
    public void setMaximum(int n) {
        super.setMaximum(n);
        updateForegroundColor();
    }

    protected void updateForegroundColor() {
        Color bg;
        if(!isIndeterminate()) {
            int rf, gf, bf;
            double pct = getPercentComplete();
            int rm = progMid.getRed();
            int gm = progMid.getGreen();
            int bm = progMid.getBlue();

            if(pct < 0.5) {
                int rs = progStart.getRed();
                int gs = progStart.getGreen();
                int bs = progStart.getBlue();
                pct = pct * 2;
                rf = rs + (int) ((rm - rs) * pct);
                gf = gs + (int) ((gm - gs) * pct);
                bf = bs + (int) ((bm - bs) * pct);

            } else {
                int re = progEnd.getRed();
                int ge = progEnd.getGreen();
                int be = progEnd.getBlue();
                pct = (pct - 0.5) * 2;
                rf = rm + (int) ((re - rm) * pct);
                gf = gm + (int) ((ge - gm) * pct);
                bf = bm + (int) ((be - bm) * pct);
            }
            bg = new Color(rf, gf, bf);
        } else {
            bg = progMid;
        }

        setForeground(bg);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

        final JTextField txtMin = Lay.tx(  "0", 10, "selectall");
        final JTextField txtMax = Lay.tx("100", 10, "selectall");
        final JTextField txtVal = Lay.tx( "80", 10, "selectall");
        final JTextField txtLim = Lay.tx( "70", 10, "selectall");
        final RainbowProgressBar pgb = new RainbowProgressBar(0, 100);
        pgb.setValue(80);
        pgb.setStringPainted(true);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pgb.setMinimum(Integer.parseInt(txtMin.getText()));
                pgb.setMaximum(Integer.parseInt(txtMax.getText()));
                pgb.setValue(Integer.parseInt(txtVal.getText()));

                txtVal.requestFocusInWindow();
                txtVal.selectAll();
            }
        });

        EscapeFrame frame = new EscapeFrame("RainbowProgressBar");
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
