package replete.ui.sdplus.menu;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 * Describes and contains all the components necessary for
 * a group of menu items on a scale panel's context menu.
 * The group is defined by a name label, a trailing separator,
 * and a list of menu items.  The group itself can be made
 * visible or invisible.  Individual components' visible
 * states are saved when the group is made invisible.  Menu
 * items are accessed after construction based on the caption
 * they had at construction, regardless of how their captions
 * may change over time.
 *
 * @author Derek Trumbo
 */

public class MenuGroup {

    ////////////
    // Fields //
    ////////////

    // Name
    protected String groupName;

    // Components
    protected JLabel lblGroupName;
    protected JSeparator sepTrailing;
    protected Map<String, JMenuItem> menuItems;

    // Visibility
    protected boolean groupVisible;
    protected Map<JComponent, Boolean> componentsVisible;
    // The above visibility map is needed to record the visibility
    // of the components for when the entire group is made invisible.

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MenuGroup(String name) {

        groupName = name;

        // Initialize components.
        lblGroupName = buildGroupNameLabel();
        sepTrailing = new JPopupMenu.Separator();
        menuItems = new LinkedHashMap<String, JMenuItem>();

        // Initialize visibility.
        groupVisible = true;
        componentsVisible = new HashMap<JComponent, Boolean>();
        componentsVisible.put(sepTrailing, true);
        componentsVisible.put(lblGroupName, true);
    }

    protected JLabel buildGroupNameLabel() {
        JLabel lbl = new JLabel(groupName + ":");
        lbl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 0));
        return lbl;
    }

    public void addMenuItem(String origCaption, final JMenuItem mnu) {
        menuItems.put(origCaption, mnu);
        componentsVisible.put(mnu, true);
    }

    //////////////////////////
    // Accessors & Mutators //
    //////////////////////////

    // Accessors

    public String getName() {
        return groupName;
    }

    public JLabel getNameLabel() {
        return lblGroupName;
    }

    public JSeparator getSeparator() {
        return sepTrailing;
    }

    public Map<String, JMenuItem> getMenuItems() {
        return menuItems;
    }

    public boolean isVisible() {
        return groupVisible;
    }

    public boolean isNameLabelVisible() {
        return componentsVisible.get(lblGroupName);
    }

    public boolean isMenuItemVisible(String origCaption) {
        JMenuItem mnu = menuItems.get(origCaption);
        if(mnu != null) {
            Boolean visible = componentsVisible.get(mnu);
            if(visible != null) {
                return visible;
            }
            return false;
        }
        return false;
    }

    // Mutators

    public void setVisible(boolean visible) {
        groupVisible = visible;
        updateComponentsVisibility();
    }

    public void setNameLabelVisible(boolean visible) {
        componentsVisible.put(lblGroupName, visible);
        updateComponentsVisibility();
    }

    public void setMenuItemVisible(String origCaption, boolean visible) {
        JMenuItem mnu = menuItems.get(origCaption);
        if(mnu != null) {
            componentsVisible.put(mnu, visible);
            updateComponentsVisibility();
        }
    }

    ////////////
    // Update //
    ////////////

    // Update the visibility of all components in the group
    // based on the group visibility and the saved visibility
    // of the component.
    protected void updateComponentsVisibility() {
        updateComponentVisibility(sepTrailing);
        updateComponentVisibility(lblGroupName);
        for(JMenuItem mnu : menuItems.values()) {
            updateComponentVisibility(mnu);
        }
    }

    // Update the visibility of a component based on the
    // group visibility and the saved visibility of the
    // component.
    protected void updateComponentVisibility(JComponent comp) {
        Boolean visible = componentsVisible.get(comp);
        if(visible != null) {
            comp.setVisible(visible && groupVisible);
        }
    }
}
