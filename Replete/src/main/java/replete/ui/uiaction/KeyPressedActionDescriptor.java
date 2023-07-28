package replete.ui.uiaction;

import javax.swing.JComponent;

public class KeyPressedActionDescriptor extends UIActionDescriptor {


    ////////////
    // FIELDS //
    ////////////

    private int keyCode;
    private boolean control;
    private boolean shift;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getKeyCode() {
        return keyCode;
    }
    public boolean isControl() {
        return control;
    }
    public boolean isShift() {
        return shift;
    }

    // Mutators (Builder Pattern)

    public KeyPressedActionDescriptor setKeyCode(int key) {
        keyCode = key;
        return this;
    }
    public KeyPressedActionDescriptor setControl(boolean control) {
        this.control = control;
        return this;
    }
    public KeyPressedActionDescriptor setShift(boolean shift) {
        this.shift = shift;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public JComponent getComponent() {
        return null;
    }

    @Override
    public void validate(String state, boolean valid) {
        // TODO ?
    }

    @Override
    public String toString() {
        return "KeyPressedActionDescriptor [keyCode=" + keyCode + ", control=" + control +
            ", shift=" + shift + ", enabledStateMap=" + enabledStateMap + ", stateCheckers=" +
            stateCheckers + "]";
    }
}

