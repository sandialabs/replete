package replete.ui.nofire;

import java.awt.event.ItemEvent;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;

import replete.ui.combo.RComboBox;

public class NoFireComboBox<T> extends RComboBox<T> {


    ///////////
    // FIELD //
    ///////////

    private boolean suppressFire = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NoFireComboBox() {
    }
    public NoFireComboBox(ComboBoxModel<T> aModel) {
        super(aModel);
    }
    public NoFireComboBox(T[] items) {
        super(items);
    }
    public NoFireComboBox(Vector<T> items) {
        super(items);
    }


    /////////////////////
    // NO FIRE METHODS //
    /////////////////////

    // Adding

    public void addItemNoFire(T anObject) {
        suppressFire = true;
        super.addItem(anObject);
        suppressFire = false;
    }
    public void insertItemAtNoFire(T anObject, int index) {
        suppressFire = true;
        super.insertItemAt(anObject, index);
        suppressFire = false;
    }

    // Removing

    public void removeAllItemsNoFire() {
        suppressFire = true;
        super.removeAllItems();
        suppressFire = false;
    }
    public void removeItemNoFire(Object anObject) {
        suppressFire = true;
        super.removeItem(anObject);
        suppressFire = false;
    }
    public void removeItemAtNoFire(int anIndex) {
        suppressFire = true;
        super.removeItemAt(anIndex);
        suppressFire = false;
    }

    // Selection

    public void setSelectedItemNoFire(Object anObject) {
        suppressFire = true;
        super.setSelectedItem(anObject);
        suppressFire = false;
    }
    public void setSelectedIndexNoFire(int index) {
        suppressFire = true;
        super.setSelectedIndex(index);
        suppressFire = false;
    }

    // Overridden to suppress events from model changes.

    @Override
    public void intervalAdded(ListDataEvent e) {
        boolean preSuppress = suppressFire;
        ComboBoxModel mdl = getModel();
        if(mdl instanceof NoFireComboBoxModel) {
            if(((NoFireComboBoxModel) mdl).isSuppress()) {
                suppressFire = true;
            }
        }
        super.intervalAdded(e);
        suppressFire = preSuppress;
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        boolean preSuppress = suppressFire;
        ComboBoxModel mdl = getModel();
        if(mdl instanceof NoFireComboBoxModel) {
            if(((NoFireComboBoxModel) mdl).isSuppress()) {
                suppressFire = true;
            }
        }
        super.intervalRemoved(e);
        suppressFire = preSuppress;
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        boolean preSuppress = suppressFire;
        ComboBoxModel mdl = getModel();
        if(mdl instanceof NoFireComboBoxModel) {
            if(((NoFireComboBoxModel) mdl).isSuppress()) {
                suppressFire = true;
            }
        }
        super.contentsChanged(e);
        suppressFire = preSuppress;
    }


    ////////////////////////
    // EVENTS TO SUPPRESS //
    ////////////////////////

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


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // For manual control of fire suppression.  Probably no
    // longer needed.  See NoFireComboBoxModel.
    public void setNoFireEnabled(boolean noFire) {
        suppressFire = noFire;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        new NoFireComboBox<>();
    }
}
