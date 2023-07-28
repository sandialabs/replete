package replete.ui.button;

import javax.swing.Action;
import javax.swing.Icon;

import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageLib;

// https://stackoverflow.com/questions/1263323/tristate-checkboxes-in-java

public class TristateCheckBox extends RCheckBox {


    ////////////
    // FIELDS //
    ////////////

    private static Icon selected     = ImageLib.get(RepleteImageModel.CHECKBOX_ON); // new javax.swing.ImageIcon(TristateCheckBox.class.getResource("selected.png"));
    private static Icon unselected   = ImageLib.get(RepleteImageModel.CHECKBOX_OFF);
    private static Icon halfselected = ImageLib.get(RepleteImageModel.CHECKBOX_HALF);

    private boolean halfState;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TristateCheckBox() {
        super();
    }
    public TristateCheckBox(Action action) {
        super(action);
    }
    public TristateCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
    }
    public TristateCheckBox(Icon icon) {
        super(icon);
    }
    public TristateCheckBox(String icon, boolean selected) {
        super(icon, selected);
    }
    public TristateCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
    }
    public TristateCheckBox(String text, Icon icon) {
        super(text, icon);
    }
    public TristateCheckBox(String text) {
        super(text);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isHalfSelected() {
        return halfState;
    }

    // Mutators

    public void setHalfSelected(boolean halfState) {
        this.halfState = halfState;
        if(halfState) {
            super.setSelected(false);
            updateIcon();
        }
    }

    @Override
    public void setSelected(boolean b) {
        halfState = false;
        super.setSelected(b);
        updateIcon();
    }


    //////////
    // MISC //
    //////////

    private void updateIcon() {
        setIcon(halfState ? halfselected : isSelected() ? selected : unselected);
    }
}
