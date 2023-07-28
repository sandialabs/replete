package replete.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.mnemonics.Mnemonics;
import replete.ui.windows.Dialogs;
import replete.ui.windows.ExampleFrame;


public class RMenu extends JMenu {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RMenu() {
    }
    public RMenu(Icon icon) {
        this(null, icon);
    }
    public RMenu(ImageModelConcept concept) {
        this(ImageLib.get(concept));
    }
    public RMenu(String text) {
        super(text);
    }
    public RMenu(Action action) {
        super(action);
    }
    public RMenu(String text, boolean tearOff) {
        super(text, tearOff);
    }
    public RMenu(String text, Icon icon) {
        super(text);
        setIcon(icon);
    }
    public RMenu(String text, ImageModelConcept concept) {
        this(text, ImageLib.get(concept));
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
