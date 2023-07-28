package replete.ui.sdplus.menu;

import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * Contains groups of menu items.  Each group and each menu item
 * in each group can be individually made visible or invisible.
 *
 * @author Derek Trumbo
 */

public class MenuConfiguration {

    public static final String MNU_GRP_SEL = "Selection";
    public static final String MNU_GRP_VIS = "Visualization";
    public static final String MNU_GRP_TBL = "Table";
    public static final String MNU_GRP_OPT = "Options";

    protected Map<String, MenuGroup> groups;
    protected JPopupMenu mnuPopup;

    public MenuConfiguration() {
        clear();
    }

    public void clear() {
        groups = new LinkedHashMap<>();
    }

    public JPopupMenu getPopupMenu() {
        return mnuPopup;
    }

    public void definePopupMenuItem(String groupName, String origCaption, Icon icon, boolean checked, ActionListener listener) {

        // Create the menu item.
        JMenuItem mnu;
        if(checked) {
            mnu = new JCheckBoxMenuItem(origCaption);
        } else {
            mnu = new JMenuItem(origCaption);
        }
        mnu.setIcon(icon);
        mnu.addActionListener(listener);

        // Get or create the group for the menu item.
        MenuGroup group = groups.get(groupName);
        if(group == null) {
            group = new MenuGroup(groupName);
            groups.put(groupName, group);
        }

        // JMenuItem stored in the map using the original caption of the menu item.
        // The caption might change over the time, but the key will remain the same.
        group.addMenuItem(origCaption, mnu);
    }

    public void buildPopupMenu(boolean popupSectionLabels) {
        mnuPopup = new JPopupMenu("ScalePanelPopup");
        int groupCount = 1;
        for(MenuGroup group : groups.values()) {
            mnuPopup.add(group.getNameLabel());
            Map<String, JMenuItem> groupMenus = group.getMenuItems();
            for(JMenuItem mnu : groupMenus.values()) {
                mnuPopup.add(mnu);
            }
            if(groupCount != groups.size()) {
                mnuPopup.add(group.getSeparator());
            }
            groupCount++;
        }
    }

    public JMenuItem getMenuItem(String groupName, String caption) {
        MenuGroup group = groups.get(groupName);
        if(group != null) {
            return group.getMenuItems().get(caption);
        }
        return null;
    }

    public void setGroupNameLabelsVisible(boolean visible) {
        for(MenuGroup group : groups.values()) {
            group.setNameLabelVisible(visible);
        }
    }

    public boolean isMenuItemVisible(String groupName, String menuOrigCaption) {
        MenuGroup group = groups.get(groupName);
        if(group != null) {
            return group.isMenuItemVisible(menuOrigCaption);
        }
        return false;
    }

    public void setMenuItemVisible(String groupName, String menuOrigCaption, boolean visible) {
        MenuGroup group = groups.get(groupName);
        if(group != null) {
            group.setMenuItemVisible(menuOrigCaption, visible);
        }
    }

    public boolean isGroupVisible(String groupName) {
        MenuGroup group = groups.get(groupName);
        if(group != null) {
            return group.isVisible();
        }
        return false;
    }

    public void setGroupVisible(String groupName, boolean visible) {
        MenuGroup group = groups.get(groupName);
        if(group != null) {
            group.setVisible(visible);
        }
    }
}
