package replete.ui.button;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

import replete.ui.lay.Lay;
import replete.ui.mnemonics.Mnemonics;


public class RRadioButton extends JRadioButton {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RRadioButton() {
    }
    public RRadioButton(Icon icon) {
        super(icon);
    }
    public RRadioButton(Action action) {
        super(action);
    }
    public RRadioButton(String text) {
        super(text);
    }
    public RRadioButton(Icon icon, boolean selected) {
        super(icon, selected);
    }
    public RRadioButton(String text, boolean selected) {
        super(text, selected);
    }
    public RRadioButton(String text, Icon icon) {
        super(text, icon);
    }
    public RRadioButton(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
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
        RRadioButton r1, r2;
        Lay.FLtg(Lay.fr("RRadioButton Test"),
            r1 = Lay.opt("&Saturn && Jupiter"),
            r2 = Lay.opt("M&ars"),
            "size=600,center,visible"
        );
        Lay.grp(r1, r2);
    }
}
