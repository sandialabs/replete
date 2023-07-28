package replete.ui.uiaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.menu.RCheckBoxMenuItem;
import replete.ui.menu.RMenu;
import replete.ui.menu.RMenuItem;
import replete.ui.menu.RRadioButtonMenuItem;

/**
 * @author Derek Trumbo
 */

public class MenuBarActionDescriptor extends UIActionDescriptor {


    ////////////
    // FIELDS //
    ////////////

    protected String path;

    protected boolean isSeparator;

    protected String text;
    protected ImageIcon icon;
    protected UIActionListener listener;

    protected boolean isCheckMenu;
    protected boolean isOptMenu;
    protected String checkOptGroupId;  // Used for either check box menu items or radio button menu items

    protected int mnemonic;
    protected int accKey;
    protected boolean accCtrl;
    protected boolean accShift;

    protected String sepGroup;

    // The component that is currently representing this descriptor on screen.
    protected JComponent component;     // JLabel, JMenu, JMenuItem, or JCheckBoxMenuItem


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getPath() {
        return path;
    }
    public boolean isSeparator() {
        return isSeparator;
    }
    public String getText() {
        return text;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public UIActionListener getListener() {
        return listener;
    }
    public boolean isCheckMenu() {
        return isCheckMenu;
    }
    public boolean isOptMenu() {
        return isOptMenu;
    }
    public String getCheckOptGroupId() {
        return checkOptGroupId;
    }
    public int getMnemonic() {
        return mnemonic;
    }
    public int getAccKey() {
        return accKey;
    }
    public boolean isAccCtrl() {
        return accCtrl;
    }
    public boolean isAccShift() {
        return accShift;
    }
    public String getSepGroup() {
        return sepGroup;
    }
    @Override
    public JComponent getComponent() {
        return component;
    }

    // Mutators (Builder Pattern)

    @Override
    public MenuBarActionDescriptor setEnabledStateMap(Map<String, Boolean> enabledStateMap) {
        this.enabledStateMap = enabledStateMap;
        return this;
    }
    public MenuBarActionDescriptor setPath(String path) {
        this.path = path;
        return this;
    }
    public MenuBarActionDescriptor setSeparator(boolean isSeparator) {
        this.isSeparator = isSeparator;
        return this;
    }
    public MenuBarActionDescriptor setText(String text) {
        this.text = text;
        return this;
    }
    public MenuBarActionDescriptor setIcon(ImageIcon icon) {
        this.icon = icon;
        return this;
    }
    public MenuBarActionDescriptor setIcon(ImageModelConcept concept) {
        icon = ImageLib.get(concept);
        return this;
    }
    public MenuBarActionDescriptor setListener(UIActionListener listener) {
        this.listener = listener;
        return this;
    }
    public MenuBarActionDescriptor setCheckMenu(boolean isCheckMenu) {
        this.isCheckMenu = isCheckMenu;
        return this;
    }
    public MenuBarActionDescriptor setCheckMenu(boolean isCheckMenu, String checkOptGroupId) {
        this.isCheckMenu = isCheckMenu;
        this.checkOptGroupId = checkOptGroupId;
        return this;
    }
    public MenuBarActionDescriptor setOptMenu(boolean isOptMenu) {
        this.isOptMenu = isOptMenu;
        return this;
    }
    public MenuBarActionDescriptor setOptMenu(boolean isOptMenu, String checkOptGroupId) {
        this.isOptMenu = isOptMenu;
        this.checkOptGroupId = checkOptGroupId;
        return this;
    }
    public MenuBarActionDescriptor setMnemonic(int mnemonic) {
        this.mnemonic = mnemonic;
        return this;
    }
    public MenuBarActionDescriptor setAccCtrl(boolean accCtrl) {
        this.accCtrl = accCtrl;
        return this;
    }
    public MenuBarActionDescriptor setAccShift(boolean accShift) {
        this.accShift = accShift;
        return this;
    }
    public MenuBarActionDescriptor setAccKey(int accKey) {
        this.accKey = accKey;
        return this;
    }
    public MenuBarActionDescriptor setSepGroup(String sepGroup) {
        this.sepGroup = sepGroup;
        return this;
    }
    public MenuBarActionDescriptor setComponent(JComponent component) { // JLabel, JMenu, JMenuItem, or JCheckBoxMenuItem
        this.component = component;
        return this;
    }
    protected void clearComponent() {         // Convenience/Clarity
        setComponent(null);
    }


    //////////
    // MISC //
    //////////

    @Override
    public void validate(String state, boolean valid) {
        if(component != null) {
            component.setEnabled(doEnable(state) && valid);
        }
    }

    public JComponent getOrCreateMenuComponent(final UIAction action, boolean menu, MouseListener mouseListener) {

        if(getComponent() != null) {
            JComponent cmp = getComponent();
            return cmp;
        }

        JComponent cmp;

        // If this action needs to be an expandable menu in the
        // menu bar...
        if(menu) {
            // TODO: JLabel possible like popup menu?
            cmp = new RMenu(getText());

        // Else if this action is to be manifested as a menu item...
        } else {

            // Decide what kind of menu item to use.
            if(isCheckMenu()) {
                cmp = new RCheckBoxMenuItem(getText());
            } else if(isOptMenu()) {
                cmp = new RRadioButtonMenuItem(getText());
            } else {
                cmp = new RMenuItem(getText());
            }

            // Add accelerator key.
            if(getAccKey() != 0) {
                ((JMenuItem) cmp).setAccelerator(
                    KeyStroke.getKeyStroke(getAccKey(),
                        (isAccCtrl() ? KeyEvent.CTRL_MASK : 0) +
                        (isAccShift() ? KeyEvent.SHIFT_MASK : 0))
                );
            }
        }

        // Structured this way to make it look like popup menu action descriptor
        // even though label menus not implemented fully for menu bar yet
        if(cmp instanceof JMenuItem) {            // JMenuItem, JMenu, NOT JLabel
            JMenuItem mnu = (JMenuItem) cmp;

            // Set remaining parameters.
            if(getMnemonic() != 0) {
                mnu.setMnemonic(getMnemonic());
            }

            // Prefer action's listener
            if(action.getListener() != null) {
                mnu.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        action.getListener().actionPerformed(e, action);
                    }
                });

            } else if(getListener() != null) {
                mnu.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        getListener().actionPerformed(e, action);
                    }
                });
            }

            if(getIcon() != null) {
                mnu.setIcon(getIcon());
            }

            // This is one the things different than the ui action popup menu.
            // Popup menu removes items that shouldn't be there, menu bar disables
            // them.  So this starts out disabled, and then they are reenabled
            // later.
            mnu.setEnabled(false);
        }

        setComponent(cmp);

        return cmp;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "MenuBarActionDescriptor [path=" + path + ", isSeparator=" + isSeparator +
            ", text=" + text + ", icon=" + icon + ", listener=" + listener + ", isCheckMenu=" +
            isCheckMenu + ", mnemonic=" + mnemonic + ", accKey=" + accKey + ", accCtrl=" + accCtrl +
            ", accShift=" + accShift + ", sepGroup=" + sepGroup + ", menuComponent=" + component + "]";
    }
}
