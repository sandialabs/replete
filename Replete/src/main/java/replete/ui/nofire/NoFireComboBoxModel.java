package replete.ui.nofire;

import javax.swing.DefaultComboBoxModel;

public class NoFireComboBoxModel extends DefaultComboBoxModel {

    ///////////
    // FIELD //
    ///////////

    private boolean suppressFire = false;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public boolean isSuppress() {
        return suppressFire;
    }


    /////////////////////
    // NO FIRE METHODS //
    /////////////////////

    // Adding

    public void addElementNoFire(Object anObject) {
        suppressFire = true;
        super.addElement(anObject);
        suppressFire = false;
    }
    public void insertElementAtNoFire(Object anObject,int index) {
        suppressFire = true;
        super.insertElementAt(anObject, index);
        suppressFire = false;
    }

    // Removing

    public void removeElementAtNoFire(int index) {
        suppressFire = true;
        super.removeElementAt(index);
        suppressFire = false;
    }
    public void removeElementNoFire(Object anObject) {
        suppressFire = true;
        super.removeElement(anObject);
        suppressFire = false;
    }
    public void removeAllElementsNoFire() {
        suppressFire = true;
        super.removeAllElements();
        suppressFire = false;
    }

    // Selection

    public void setSelectedItemNoFire(Object anObject) {
        suppressFire = true;
        super.setSelectedItem(anObject);
        suppressFire = false;
    }
}
