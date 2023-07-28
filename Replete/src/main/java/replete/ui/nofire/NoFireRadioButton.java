package replete.ui.nofire;

import java.awt.event.ItemEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

public class NoFireRadioButton extends JRadioButton {
    private boolean suppressFire = false;

    public NoFireRadioButton() {
    }
    public NoFireRadioButton(Icon arg0) {
        super(arg0);
    }
    public NoFireRadioButton(Action arg0) {
        super(arg0);
    }
    public NoFireRadioButton(String arg0) {
        super(arg0);
    }
    public NoFireRadioButton(Icon arg0, boolean arg1) {
        super(arg0, arg1);
    }
    public NoFireRadioButton(String arg0, boolean arg1) {
        super(arg0, arg1);
    }
    public NoFireRadioButton(String arg0, Icon arg1) {
        super(arg0, arg1);
    }
    public NoFireRadioButton(String arg0, Icon arg1, boolean arg2) {
        super(arg0, arg1, arg2);
    }

    public void setSelectedNoFire(boolean state) {
        suppressFire = true;
        super.setSelected(state);
        suppressFire = false;
    }

    @Override
    protected void fireItemStateChanged(ItemEvent e) {
        if(!suppressFire) {
            super.fireItemStateChanged(e);
        }
    }
}
