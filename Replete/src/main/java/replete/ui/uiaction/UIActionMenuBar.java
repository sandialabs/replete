package replete.ui.uiaction;

import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import replete.equality.EqualsUtil;
import replete.text.StringUtil;
import replete.ui.menu.RCheckBoxMenuItem;
import replete.ui.menu.RRadioButtonMenuItem;

/**
 * @author Derek Trumbo
 */

// TODO: Clean this up some day a la UIActionPopupMenu

public class UIActionMenuBar extends JMenuBar {

    private UIActionMap actionMap;
    protected Map<String, ButtonGroup> buttonGroups = new HashMap<>();

    public UIActionMenuBar(UIActionMap actionMap) {
        this.actionMap = actionMap;

        init(actionMap);
        actionMap.validate();
    }

    protected void init(UIActionMap actionMap) {
        Map<JMenu, Boolean> firstSepGroup = new HashMap<>();
        Map<JMenu, String> sepGroup = new HashMap<>();

        Set<String> parentMenuIds = getParentMenuIds(actionMap);

        boolean insertSeparator = false;

        // Look through the actions and decide which actions need
        // to have expandable menus or menu items created in the
        // menu bar.
        for(UIAction action : actionMap.getActions()) {

            // Validation check to continue or not?

            MenuBarActionDescriptor desc =
                (MenuBarActionDescriptor) action.getDescriptor(
                    MenuBarActionDescriptor.class);

            if(desc == null) {
                continue;
            }

            // If this action is to be manifested in the menu bar...

            if(desc.isSeparator()) {
                insertSeparator = true;
                continue;
            }

            JComponent addComp = desc.getOrCreateMenuComponent(
                action, parentMenuIds.contains(action.getId()),
                null /* only popup version has mouse listener currently */
            );

            // If there is no path, then this must be a top-level
            // expandable menu.
            if(StringUtil.isBlank(desc.getPath())) {
                // Can't put separators on the menu bar itself (insertSeparator ignored)
                add(addComp);

            // Else Make sure this menu gets added to the appropriate
            // expandable parent menu.
            } else {
                appendToParent(actionMap, insertSeparator,
                    firstSepGroup, sepGroup, desc, addComp);
            }

            checkAddToGroup(desc.getCheckOptGroupId(), addComp);

            insertSeparator = false;
        }
    }

    private void checkAddToGroup(String groupId, JComponent addComp) {
        if(groupId != null &&
                (addComp instanceof RCheckBoxMenuItem ||
                    addComp instanceof RRadioButtonMenuItem)) {
            ButtonGroup buttonGroup = buttonGroups.get(groupId);
            if(buttonGroup == null) {
                buttonGroup = new ButtonGroup();
                buttonGroups.put(groupId, buttonGroup);
            }
            buttonGroup.add((JMenuItem) addComp);
        }
    }

    private void appendToParent(UIActionMap actionMap, boolean insertSeparator,
                                Map<JMenu, Boolean> firstSepGroup, Map<JMenu, String> sepGroup,
                                MenuBarActionDescriptor mDesc, Component child) {

        // Capture the nearest parent ID in the menu path.
        String[] pathComponents = mDesc.getPath().split("/");
        String parent = pathComponents[pathComponents.length - 1];

        // Add this expandable menu to its parent expandable
        // menu if the action has been processed already.
        UIAction parentAction = actionMap.getAction(parent);
        if(parentAction != null) {
            MenuBarActionDescriptor descParent =
                (MenuBarActionDescriptor) parentAction.getDescriptor(MenuBarActionDescriptor.class);
            if(descParent != null) {
                JMenu parentMenu = (JMenu) descParent.getComponent();
                if(parentMenu != null) {

                    boolean first = firstSepGroup.containsKey(parentMenu) ?
                        (Boolean) firstSepGroup.get(parentMenu) : true;
                    String group = sepGroup.containsKey(parentMenu) ?
                        (String) sepGroup.get(parentMenu) : null;

                    if(insertSeparator || (!first && !EqualsUtil.equals(group, mDesc.getSepGroup()))) {
                        parentMenu.add(new JSeparator());
                    }

                    parentMenu.add(child);

                    first = false;
                    group = mDesc.getSepGroup();
                    firstSepGroup.put(parentMenu, first);
                    sepGroup.put(parentMenu, group);
                }
            }
        }
    }

    private Set<String> getParentMenuIds(UIActionMap actionMap) {

        // Look through the actions and decide which actions need
        // to be manifested as expandable menus as opposed to
        // menu items.
        Set<String> parentMenuIds = new HashSet<>();
        for(UIAction action : actionMap.getActions()) {
            MenuBarActionDescriptor mDesc =
                (MenuBarActionDescriptor) action.getDescriptor(MenuBarActionDescriptor.class);

            // If this action is to be manifested in the menu bar...
            if(mDesc != null) {

                if(mDesc.component instanceof JMenu) {
                    parentMenuIds.add(action.id);
                }

                // If there is no path for this action, then it must
                // be a top-level expandable menu in the menu bar.
                else if(mDesc.path == null || mDesc.path.equals("")) {
                    parentMenuIds.add(action.id);

                // Else add all the action ID's in this action's menu
                // path as all of those actions must be expandable
                // menus and not menu items.
                } else {
                    String[] pathComponents = mDesc.path.split("/");
                    for(String pc : pathComponents) {
                        parentMenuIds.add(pc);
                    }
                }
            }
        }
        return parentMenuIds;
    }

    public UIActionMap getUiActionMap() {
        return actionMap;
    }
}
