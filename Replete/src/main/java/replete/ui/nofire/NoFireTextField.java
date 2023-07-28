package replete.ui.nofire;

import javax.swing.text.Document;

import replete.ui.text.RTextField;

public class NoFireTextField extends RTextField {


    ///////////
    // FIELD //
    ///////////

    private boolean suppressFire = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NoFireTextField() {
        super();
    }
    public NoFireTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }
    public NoFireTextField(int columns) {
        super(columns);
    }
    public NoFireTextField(String text, int columns) {
        super(text, columns);
    }
    public NoFireTextField(String text) {
        super(text);
    }


    /////////////////////
    // NO FIRE METHODS //
    /////////////////////

    // Adding

    public void setTextNoFire(String text) {
        suppressFire = true;
        super.setText(text); // More to do....................
        suppressFire = false;
    }


    ////////////////////////
    // EVENTS TO SUPPRESS //
    ////////////////////////
/*
    @Override
    protected void fireItemStateChanged(ItemEvent e) {
        if(!suppressFire) {
            super.fireItemStateChanged(e);
        }
    }

    @Override
    public void fireActionEvent() {
        if(!suppressFire) {
            super.fireActionEvent();
        }
    }
*/

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // For manual control of fire suppression.  Probably no
    // longer needed.  See NoFireComboBoxModel.
    public void setNoFireEnabled(boolean noFire) {
        suppressFire = noFire;
    }
}
