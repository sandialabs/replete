package replete.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import replete.ui.mnemonics.Mnemonics;
import replete.ui.windows.Dialogs;
import replete.ui.windows.ExampleFrame;


public class RRadioButtonMenuItem extends JRadioButtonMenuItem {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RRadioButtonMenuItem() {
    }
    public RRadioButtonMenuItem(Icon icon) {
        super(icon);
    }
    public RRadioButtonMenuItem(String text) {
        super(text);
        setText(text);
    }
    public RRadioButtonMenuItem(Action a) {
        super(a);
    }
    public RRadioButtonMenuItem(String text, Icon icon) {
        super(text, icon);
        setText(text);
    }
    public RRadioButtonMenuItem(String text, boolean selected) {
        super(text, selected);
        setText(text);
    }
    public RRadioButtonMenuItem(Icon icon, boolean selected) {
        super(icon, selected);
    }
    public RRadioButtonMenuItem(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
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
        JMenuItem mi = new RRadioButtonMenuItem("&Saturn && Mars");
        JMenuItem mi2 = new RRadioButtonMenuItem("&Jupiter");
        ButtonGroup grp = new ButtonGroup();
        grp.add(mi);
        grp.add(mi2);
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
