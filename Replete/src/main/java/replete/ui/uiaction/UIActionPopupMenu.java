package replete.ui.uiaction;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import replete.equality.EqualsUtil;
import replete.event.ExtChangeNotifier;
import replete.text.StringUtil;
import replete.ui.lay.Lay;
import replete.ui.menu.RCheckBoxMenuItem;
import replete.ui.menu.RRadioButtonMenuItem;

/**
 * @author Derek Trumbo
 */

public class UIActionPopupMenu extends JPopupMenu {

    private UIActionMap actionMap;
    protected Map<String, ButtonGroup> buttonGroups = new HashMap<>();
    private Map<JComponent, String> componentActions = new HashMap<>();
    private JPanel pnlInfo;

    public UIActionPopupMenu(UIActionMap actionMap) {
        this(actionMap, null);
    }
    public UIActionPopupMenu(UIActionMap actionMap, JPanel pnlInfo) {
        this.actionMap = actionMap;
        this.pnlInfo = pnlInfo;

        init(actionMap);

        // For code simplicity, we just remove empty submenus
        // after the fact instead of trying to never add them
        // in the first place.
        removeEmptySubmenus(this);

        // Make labels look nicer
        padLabels(this);
    }

    // Probably doesn't work with multiple back-to-back levels
    // of submenus all being represented as labels, but then
    // again that would not look very good at all without
    // some adjustment.  If that is needed then we could embed
    // the level information into the JLabel some how
    // so that a JLabel is not removed just because it has a
    // child submenu that is also a label.
    private void removeEmptySubmenus(JPopupMenu parent) {
        for(int i = parent.getComponentCount() - 1; i >= 0; i--) {
            Component cmp = parent.getComponent(i);
            if(cmp instanceof JMenuItem) {
                JMenuItem child = (JMenuItem) cmp;
                if(child instanceof JMenu) {
                    JPopupMenu mnuPop = ((JMenu) child).getPopupMenu();
                    removeEmptySubmenus(mnuPop);
                    if(mnuPop.getComponentCount() == 0) {
                        parent.remove(i);
                    }
                }
            } else if(cmp instanceof JLabel) {
                if(i == parent.getComponentCount() - 1) {
                    parent.remove(i);
                } else if(parent.getComponent(i + 1) instanceof JLabel) {
                    parent.remove(i);   // Assumed to be a label of same submenu level!
                }
            }
        }
    }

    private void padLabels(JPopupMenu parent) {
        for(int i = 0; i < parent.getComponentCount(); i++) {
            Component cmp = parent.getComponent(i);
            if(cmp instanceof JMenuItem) {
                JMenuItem child = (JMenuItem) cmp;
                if(child instanceof JMenu) {
                    JPopupMenu mnuPop = ((JMenu) child).getPopupMenu();
                    padLabels(mnuPop);
                }
            } else if(cmp instanceof JLabel && cmp != pnlInfo) {
                if(i != 0) {
                    Lay.hn(cmp, "eb=3l2b8t");
                } else {
                    Lay.hn(cmp, "eb=3l2b");
                }
            }
        }
    }

    protected void init(UIActionMap actionMap) {
        Map<JMenu, Boolean> firstSepGroup = new HashMap<>();
        Map<JMenu, String> sepGroup = new HashMap<>();

        if(pnlInfo != null) {
            add(pnlInfo);
            add(new JSeparator());
        }

        Set<String> parentMenuIds = getParentMenuIds(actionMap);

        boolean insertSeparator = false;

        // Look through the actions and decide which actions need
        // to have expandable menus or menu items created in the
        // menu bar.
        for(UIAction action : actionMap.getActions()) {

            if(action.getValidator() != null &&
                    !action.getValidator().isValid(action.getId())) {
                continue;
            }

            PopupMenuActionDescriptor desc =
                (PopupMenuActionDescriptor) action.getDescriptor(
                    PopupMenuActionDescriptor.class);

            if(desc == null) {
                continue;
            }

            // If this action is to be manifested in the menu bar...

            // If separator, make sure to insert a separator on
            // the next time around to prevent separator at
            // end of the menu.
            if(desc.isSeparator()) {
                insertSeparator = true;
                continue;
            }

            JComponent addComp = desc.getOrCreateMenuComponent(
                action, parentMenuIds.contains(action.getId()),
                mouseListener
            );
            componentActions.put(addComp, action.getId());

            // If there is no path, then this must be a top-level
            // menu (JLabel or JMenu) or menu item (JMenuItem or JCheckBoxMenuItem).
            if(StringUtil.isBlank(desc.getPath())) {
                if(insertSeparator) {
                    add(new JSeparator());
                }
                add(addComp);

            // Else Make sure this menu or menu item gets added to the
            // appropriate expandable parent menu.
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

    private Set<String> getParentMenuIds(UIActionMap actionMap) {

        // Look through the actions and decide which actions need
        // to be manifested as expandable menus (or labeled menu
        // groups) as opposed to menu items.
        Set<String> parentMenuIds = new HashSet<>();
        for(UIAction action : actionMap.getActions()) {
            PopupMenuActionDescriptor desc =
                (PopupMenuActionDescriptor) action.getDescriptor(
                    PopupMenuActionDescriptor.class);

            // If this action is to be manifested in the popup menu,
            // find all those menus that need to be parent menus.
            if(desc != null) {
                if(desc.getPath() != null && !desc.getPath().equals("")) {
                    String[] pathComponents = desc.getPath().split("/");
                    for(String pc : pathComponents) {
                        parentMenuIds.add(pc);
                    }
                }
            }
        }
        return parentMenuIds;
    }

    // Add this menu or menu item to its parent expandable
    // menu if the action has been processed already.
    private void appendToParent(UIActionMap actionMap, boolean insertSeparator,
                                Map<JMenu, Boolean> firstSepGroup, Map<JMenu, String> sepGroup,
                                PopupMenuActionDescriptor desc, Component child) {

        // Capture the nearest parent ID in the menu path.
        String[] pathComponents = desc.getPath().split("/");

        boolean added = false;
        for(int p = pathComponents.length - 1; p >= 0; p--) {
            String parentId = pathComponents[p];
            UIAction parentAction = actionMap.getAction(parentId);
            if(parentAction != null) {
                PopupMenuActionDescriptor descParent =
                    (PopupMenuActionDescriptor) parentAction.getDescriptor(
                        PopupMenuActionDescriptor.class);
                if(descParent != null) {
                    if(!descParent.isLabelMenu()) {
                        JMenu parentMenu = (JMenu) descParent.getComponent();
                        if(parentMenu != null) {

                            boolean first = firstSepGroup.containsKey(parentMenu) ?
                                (Boolean) firstSepGroup.get(parentMenu) : true;
                            String group = sepGroup.containsKey(parentMenu) ?
                                (String) sepGroup.get(parentMenu) : null;

                            if(insertSeparator || (!first && !EqualsUtil.equals(group, desc.getSepGroup()))) {
                                parentMenu.add(new JSeparator());
                            }
                            parentMenu.add(child);
                            added = true;

                            first = false;
                            group = desc.getSepGroup();
                            firstSepGroup.put(parentMenu, first);
                            sepGroup.put(parentMenu, group);

                            break;
                        }
                    }
                }
            }
        }
        if(!added) {
            add(child);
        }
    }

    private ExtChangeNotifier<HoverListener> hoverNotifier = new ExtChangeNotifier<>();

    public void addHoverListener(HoverListener listener) {
        hoverNotifier.addListener(listener);
    }
    private void fireHoverNotifier(String id, String type, JComponent component) {
        HoverEvent event = new HoverEvent(id, type, component);
        hoverNotifier.fireStateChanged(event);
    }

    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseExited(MouseEvent e) {
            JComponent cmp = (JComponent) e.getComponent();
            fireHoverNotifier(componentActions.get(cmp), "exit", cmp);
        }
        @Override
        public void mouseEntered(MouseEvent e) {
            JComponent cmp = (JComponent) e.getComponent();
            fireHoverNotifier(componentActions.get(cmp), "enter", cmp);
        }
    };

    public UIActionMap getUiActionMap() {
        return actionMap;
    }
}
