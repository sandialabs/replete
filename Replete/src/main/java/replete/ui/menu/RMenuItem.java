package replete.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.mnemonics.Mnemonics;
import replete.ui.windows.Dialogs;
import replete.ui.windows.ExampleFrame;


public class RMenuItem extends JMenuItem {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RMenuItem() {
    }
    public RMenuItem(Icon icon) {
        super(icon);
    }
    public RMenuItem(ImageModelConcept concept) {
        super(ImageLib.get(concept));
    }
    public RMenuItem(String text) {
        super(text);
    }
    public RMenuItem(Action a) {
        super(a);
    }
    public RMenuItem(String text, Icon icon) {
        super(text, icon);
    }
    public RMenuItem(String text, ImageModelConcept concept) {
        super(text, ImageLib.get(concept));
    }
    public RMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
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
        RMenuItem mi = new RMenuItem("&Saturn");
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dialogs.showMessage(frame, "Clicked");
            }
        });
        RMenu mnu = new RMenu("&File");
        System.out.println(mnu.getMnemonic());
        System.out.println(mi.getMnemonic());
        mnu.add(mi);
        JMenuBar bar = new JMenuBar();
        bar.add(mnu);
        frame.setJMenuBar(bar);
        frame.setVisible(true);
    }
}
