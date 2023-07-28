package learning.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import replete.ui.lay.GBC;
import replete.ui.lay.Lay;

public class GridBagCenterLeftTest {

    public static void main(String[] args) {
        JPanel pnl = new JPanel(new GridBagLayout());

        GridBagConstraints c = GBC.c().a(GBC.W).wx(0.1);

        JPanel pnlInner = new JPanel(new FlowLayout());
        Lay.hn(pnlInner, "bg=yellow");

        pnlInner.add(new JButton("Abc"));
        pnlInner.add(new JTextField("asdf"));
        pnlInner.add(new JCheckBox("AAAA"));
        pnlInner.add(Lay.hn(new JButton("aaaaa"), "prefh=100"));

        pnl.add(pnlInner, c);

        Lay.BLtg(Lay.fr("Test"), pnl, "size=400,center,visible");
    }
}
