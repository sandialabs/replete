package replete.ui.uiaction;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class UIActionKeyPressedListener extends KeyAdapter {

    private UIActionMap actionMap;

    public UIActionKeyPressedListener(UIActionMap actionMap) {
        this.actionMap = actionMap;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        for(UIAction action : actionMap.getActions()) {
            List<UIActionDescriptor> uiDescriptors =
                action.getDescriptors(KeyPressedActionDescriptor.class);

            // If this action is to be manifested as key presses...
            if(uiDescriptors != null) {
                for(UIActionDescriptor descriptor : uiDescriptors) {
                    KeyPressedActionDescriptor pDesc =
                        (KeyPressedActionDescriptor) descriptor;
                    if(e.getKeyCode() == pDesc.getKeyCode() &&
                                    e.isShiftDown() == pDesc.isShift() &&
                                    e.isControlDown() == pDesc.isControl()) {
                        if(action.getListener() != null) {
                            if(action.getValidator() == null || action.getValidator().isValid(action.getId())) {
                                action.execute();
                            }
                        }
                    }
                }
            }
        }
    }
}
