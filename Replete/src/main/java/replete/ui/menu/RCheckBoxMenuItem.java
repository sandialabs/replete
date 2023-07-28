package replete.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import replete.ui.mnemonics.Mnemonics;
import replete.ui.windows.Dialogs;
import replete.ui.windows.ExampleFrame;


public class RCheckBoxMenuItem extends JCheckBoxMenuItem {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RCheckBoxMenuItem() {
    }
    public RCheckBoxMenuItem(Icon icon) {
        super(icon);
    }
    public RCheckBoxMenuItem(String text) {
        super(text);
        setText(text);
    }
    public RCheckBoxMenuItem(Action a) {
        super(a);
    }
    public RCheckBoxMenuItem(String text, Icon icon) {
        super(text, icon);
        setText(text);
    }
    public RCheckBoxMenuItem(String text, boolean b) {
        super(text, b);
        setText(text);
    }
    public RCheckBoxMenuItem(String text, Icon icon, boolean b) {
        super(text, icon, b);
        setText(text);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setText(String text) {
        Mnemonics mn = Mnemonics.resolve(text);
        super.setText(mn.getResolvedText());
        Mnemonics.apply(mn, this);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        final JFrame frame = new ExampleFrame();
        JMenuItem mi = new RCheckBoxMenuItem("&Saturn && Mars");
        JMenuItem mi2 = new RCheckBoxMenuItem("&Jupiter");
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dialogs.showMessage(frame, "Clicked");
            }
        });
        mi2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dialogs.showMessage(frame, "Clicked");
            }
        });
        RMenu mnu = new RMenu("&File");
        System.out.println(mnu.getMnemonic());
        System.out.println(mi.getMnemonic());
        System.out.println(mi2.getMnemonic());
        mnu.add(mi);
        mnu.add(mi2);
        JMenuBar bar = new JMenuBar();
        bar.add(mnu);
        frame.setJMenuBar(bar);
        frame.setVisible(true);
    }
}
