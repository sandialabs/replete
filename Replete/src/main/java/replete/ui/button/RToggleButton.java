package replete.ui.button;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.mnemonics.Mnemonics;

/**
 * @author Derek Trumbo
 *
 * Mnemonic JButton:    "&Accept && Cancel"
 */

public class RToggleButton extends JToggleButton {


    ///////////
    // FIELD //
    ///////////

    private String originalText;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RToggleButton() {
    }
    public RToggleButton(Icon icon) {
        super(icon);
    }
    public RToggleButton(String text) {
        super(text);
    }
    public RToggleButton(Action action) {
        super(action);
    }
    public RToggleButton(String text, Icon icon) {
        super(text, icon);
    }
    public RToggleButton(ActionListener listener) {
        init(listener);
    }
    public RToggleButton(Icon icon, ActionListener listener) {
        super(icon);
        init(listener);
    }
    public RToggleButton(String text, ActionListener listener) {
        super(text);
        init(listener);
    }
    public RToggleButton(Action a, ActionListener listener) {
        super(a);
        init(listener);
    }
    public RToggleButton(String text, Icon icon, ActionListener listener) {
        super(text, icon);
        init(listener);
    }

    private void init(ActionListener listener) {
        addActionListener(listener);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getOriginalText() {
        return originalText;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setText(String text) {
        originalText = text;
        Mnemonics mn = Mnemonics.resolve(text);
        super.setText(mn.getResolvedText());
        Mnemonics.apply(mn, this);
    }


    //////////
    // MISC //
    //////////

    public void focus() {
        requestFocusInWindow();
    }
    public void setIcon(ImageModelConcept concept) {
        setIcon(ImageLib.get(concept));
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        final RToggleButton a, b;
        Lay.FLtg(Lay.fr("RButton Test"),
            a = new RToggleButton("A&BC"),
            b = new RToggleButton("&&XY&Z"),
            "size=[600,200],center,visible"
        );

        a.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                a.setText("&&Abraham &Lin&&col&n");
                System.out.println(a.getMnemonic() + " " + new Character((char) a.getMnemonic()));
            }
        });

        System.out.println(a.getMnemonic() + " " + new Character((char) a.getMnemonic()));
        System.out.println(b.getMnemonic() + " " + new Character((char) b.getMnemonic()));
    }
}
