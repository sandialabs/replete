package replete.ui.uiaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.menu.RCheckBoxMenuItem;
import replete.ui.menu.RMenuItem;
import replete.ui.menu.RRadioButtonMenuItem;
import replete.ui.mnemonics.Mnemonics;

/**
 * @author Derek Trumbo
 */

public class PopupMenuActionDescriptor extends MenuBarActionDescriptor {


    ////////////
    // FIELDS //
    ////////////

    private boolean labelMenu;
    protected MouseListener lastMouseListener;  // To enable caching


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessor

    public boolean isLabelMenu() {
        return labelMenu;
    }

    // Mutator (Builder)

    @Override
    public PopupMenuActionDescriptor setEnabledStateMap(Map<String, Boolean> enabledStateMap) {
        this.enabledStateMap = enabledStateMap;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setPath(String path) {
        this.path = path;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setSeparator(boolean isSeparator) {
        this.isSeparator = isSeparator;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setText(String text) {
        this.text = text;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setIcon(ImageIcon icon) {
        this.icon = icon;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setIcon(ImageModelConcept concept) {
        icon = ImageLib.get(concept);
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setListener(UIActionListener listener) {
        this.listener = listener;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setCheckMenu(boolean isCheckMenu) {
        this.isCheckMenu = isCheckMenu;
        clearComponent();
        return this;
    }
    @Override
    public MenuBarActionDescriptor setCheckMenu(boolean isCheckMenu, String checkGroup) {
        this.isCheckMenu = isCheckMenu;
        this.checkOptGroupId = checkGroup;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setMnemonic(int mnemonic) {
        this.mnemonic = mnemonic;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setAccCtrl(boolean accCtrl) {
        this.accCtrl = accCtrl;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setAccShift(boolean accShift) {
        this.accShift = accShift;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setAccKey(int accKey) {
        this.accKey = accKey;
        clearComponent();
        return this;
    }
    @Override
    public MenuBarActionDescriptor setSepGroup(String sepGroup) {
        this.sepGroup = sepGroup;
        clearComponent();
        return this;
    }
    @Override
    public PopupMenuActionDescriptor setComponent(JComponent component) {     // JLabel, JMenu, JMenuItem, or JCheckBoxMenuItem
        this.component = component;
        return this;
    }
    public PopupMenuActionDescriptor setLabelMenu(boolean labelMenu) {
        this.labelMenu = labelMenu;
        clearComponent();
        return this;
    }


    //////////
    // MISC //
    //////////

    @Override
    public JComponent getOrCreateMenuComponent(final UIAction action, boolean menu, MouseListener mouseListener) {

        if(getComponent() != null) {
            JComponent cmp = getComponent();
            if(cmp instanceof JMenu) {
                ((JMenu) cmp).removeAll();
            }
            if(lastMouseListener != null) {
                cmp.removeMouseListener(lastMouseListener);
            }
            if(mouseListener != null) {
                cmp.addMouseListener(mouseListener);
            }
            return cmp;
        }

        JComponent cmp;

        if(menu) {
            if(isLabelMenu()) {
                Mnemonics mn = Mnemonics.resolve(getText());
                cmp = Lay.lb(mn.getResolvedText(), "underline");
            } else {
                cmp = Lay.mn(getText(), getIcon(), mouseListener, "delay=0");
            }

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
                        (isAccCtrl() ? InputEvent.CTRL_MASK : 0) +
                        (isAccShift() ? InputEvent.SHIFT_MASK : 0))
                );
            }
        }

        if(cmp instanceof JMenuItem) {            // JMenuItem, JMenu, NOT JLabel
            JMenuItem mnu = (JMenuItem) cmp;
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
        }

        setComponent(cmp);
        cmp.addMouseListener(mouseListener);

        lastMouseListener = mouseListener;

        return cmp;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "PopupMenuActionDescriptor [labelMenu=" + labelMenu + ", path=" + path +
            ", isSeparator=" + isSeparator + ", text=" + text + ", icon=" + icon + ", listener=" +
            listener + ", isCheckMenu=" + isCheckMenu + ", mnemonic=" + mnemonic + ", accKey=" +
            accKey + ", accCtrl=" + accCtrl + ", accShift=" + accShift + ", menuComponent=" +
            component + ", enabledStateMap=" + enabledStateMap + ", stateCheckers=" +
            stateCheckers + "]";
    }
}
