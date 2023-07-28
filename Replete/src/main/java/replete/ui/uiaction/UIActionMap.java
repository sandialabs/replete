package replete.ui.uiaction;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

/**
 * @author Derek Trumbo
 */

public class UIActionMap {

    // Available map methods are locked down to these 3 methods
    // (as opposed to extending LinkedHashMap) so that it's
    // exceedingly obvious how to use this class.

    // Order is very important in this map.
    protected Map<String, UIAction> actionMap = new LinkedHashMap<>();

    public void execute(String id) {
        UIAction action = getAction(id);
        action.execute();
    }
    public void addAction(UIAction action) {
        if(actionMap.containsKey(action.id)) {
            throw new IllegalStateException("Already have added action with ID '" +
                action + "' to this map.");
        }
        actionMap.put(action.id, action);
    }
    public UIAction getAction(String id) {
        return actionMap.get(id);
    }
    public Collection<UIAction> getActions() {
        return actionMap.values();
    }
    public UIAction createAction() {
        return createAction(null, null, null);
    }
    public UIAction createAction(String id) {
        return createAction(id, null, null);
    }
    public UIAction createAction(String id, UIActionListener listener) {
        return createAction(id, listener, null);
    }
    public UIAction createAction(String id, ActionValidator validator) {
        return createAction(id, null, validator);
    }
    public UIAction createAction(String id, UIActionListener listener, ActionValidator validator) {
        UIAction action = new UIAction(id, listener, validator);
        addAction(action);
        return action;
    }


    // Enabled state members.

    // Just don't put this string into any enable state maps.
    public static String DISABLE_ALL_STATE = "{disable all state}";
    protected String currentState = DISABLE_ALL_STATE;

    public void setState(String state) {
        currentState = state;
        validate();
    }

    // These are convenience methods: one more each
    // type of descriptor in the framework.

    public JMenuItem getMenuBarComponent(String id) {
        UIAction action = actionMap.get(id);
        if(action == null) {
            return null;
        }
        MenuBarActionDescriptor mDesc =
            (MenuBarActionDescriptor) action.getDescriptor(MenuBarActionDescriptor.class);
        if(mDesc == null) {
            return null;
        }
        return (JMenuItem) mDesc.component;
    }

    public JMenuItem getPopupMenuComponent(String id) {
        UIAction action = actionMap.get(id);
        if(action == null) {
            return null;
        }
        PopupMenuActionDescriptor mDesc =
            (PopupMenuActionDescriptor) action.getDescriptor(PopupMenuActionDescriptor.class);
        if(mDesc == null) {
            return null;
        }
        return (JMenuItem) mDesc.component;
    }

    public AbstractButton getToolBarComponent(String id) {
       UIAction action = actionMap.get(id);
        if(action == null) {
            return null;
        }
        ToolBarActionDescriptor tDesc =
            (ToolBarActionDescriptor) action.getDescriptor(ToolBarActionDescriptor.class);
        if(tDesc == null) {
            return null;
        }
        return tDesc.component;
    }

    // Check state can be called independently of setState
    // in order to re-query any UIActionEnabledDecider's
    // that might be registered on the descriptors.
    public void validate() {
        for(UIAction action : actionMap.values()) {
            action.validate(currentState);
        }
    }

    // Synchronization between descriptors.

    public void syncToggleableDescriptors() {
        for(UIAction action : actionMap.values()) {
            action.syncDescriptors();
        }
    }

    @Override
    public String toString() {
        return actionMap.toString();
    }

    public void cleanUp() {}
}
