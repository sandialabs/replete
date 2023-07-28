package replete.ui.button;

import java.awt.Cursor;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.mnemonics.Mnemonics;

/**
 * @author Derek Trumbo
 *
 * Mnemonic JButton:    "&Accept && Cancel"
 */

public class RButton extends JButton {


    ///////////
    // FIELD //
    ///////////

    private ImageModelConcept concept;
    private String originalText;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RButton() {
    }
    public RButton(Icon icon) {
        super(icon);
    }
    public RButton(ImageModelConcept concept) {
        super(ImageLib.get(concept));
        this.concept = concept;
    }
    public RButton(String text) {
        super(text);
    }
    public RButton(Action action) {
        super(action);
    }
    public RButton(String text, Icon icon) {
        super(text, icon);
    }
    public RButton(String text, ImageModelConcept concept) {
        super(text, ImageLib.get(concept));
        this.concept = concept;
    }
    public RButton(ActionListener listener) {
        init(listener);
    }
    public RButton(Icon icon, ActionListener listener) {
        super(icon);
        init(listener);
    }
    public RButton(ImageModelConcept concept, ActionListener listener) {
        super(ImageLib.get(concept));
        this.concept = concept;
        init(listener);
    }
    public RButton(String text, ActionListener listener) {
        super(text);
        init(listener);
    }
    public RButton(Action a, ActionListener listener) {
        super(a);
        init(listener);
    }
    public RButton(String text, Icon icon, ActionListener listener) {
        super(text, icon);
        init(listener);
    }
    public RButton(String text, ImageModelConcept concept, ActionListener listener) {
        super(text, ImageLib.get(concept));
        this.concept = concept;
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
    public ImageModelConcept getConcept() {
        return concept;
    }


    //////////
    // MISC //
    //////////

    public void focus() {
        requestFocusInWindow();
    }
    public void setIcon(ImageModelConcept concept) {
        setIcon(ImageLib.get(concept));
        this.concept = concept;
    }
    public void setRolloverIcon(ImageModelConcept concept) {
        setRolloverIcon(ImageLib.get(concept));
    }
    public void setPressedIcon(ImageModelConcept concept) {
        setPressedIcon(ImageLib.get(concept));
    }

    public void setCursorHand() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    public void setCursorDefault() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        final RButton a, b;
        Lay.FLtg(Lay.fr("RButton Test"),
            a = new RButton("A&BC"),
            b = new RButton("&&XY&Z"),
            "size=[600,200],center,visible"
        );

        a.addActionListener(e -> {
            a.setText("&&Abraham &Lin&&col&n");
            System.out.println(a.getMnemonic() + " " + new Character((char) a.getMnemonic()));
        });

        System.out.println(a.getMnemonic() + " " + new Character((char) a.getMnemonic()));
        System.out.println(b.getMnemonic() + " " + new Character((char) b.getMnemonic()));
    }
}
