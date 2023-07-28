package replete.ui.uiaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

/**
 * @author Derek Trumbo
 */

public class UIAction {


    ////////////
    // FIELDS //
    ////////////

    protected Map<Class<? extends UIActionDescriptor>, List<UIActionDescriptor>> descriptors =
        new LinkedHashMap<>();
    protected String id;
    private UIActionListener listener;
    private ActionValidator validator;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public UIAction() {
        this(null, null, null);
    }
    public UIAction(String id) {
        this(id, null, null);
    }
    public UIAction(String id, UIActionListener listener) {
        this(id, listener, null);
    }
    public UIAction(String id, UIActionListener listener, ActionValidator validator) {
        if(id == null) {
            id = UUID.randomUUID().toString();
        }
        this.id = id;
        this.listener = listener;
        this.validator = validator;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getId() {
        return id;
    }
    public UIActionListener getListener() {
        return listener;
    }
    public ActionValidator getValidator() {
        return validator;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        String ref = "UIAction[" + id + "]\n";
        for(Class c : descriptors.keySet()) {
            ref += "   " + descriptors.get(c) + "\n";
        }
        return ref;
    }


    /////////////////
    // DESCRIPTORS //
    /////////////////

    // Available map methods are locked down to these 3 methods
    // (as opposed to extending LinkedHashMap) so that it's
    // exceedingly obvious how to use this class.

    public UIAction addDescriptor(UIActionDescriptor descriptor) {
        List<UIActionDescriptor> uiDescriptors = descriptors.get(descriptor.getClass());
        if(uiDescriptors == null) {
            uiDescriptors = new ArrayList<UIActionDescriptor>();
            descriptors.put(descriptor.getClass(), uiDescriptors);
        }
        uiDescriptors.add(descriptor);
        return this;
    }
    public UIActionDescriptor getDescriptor(Class<? extends UIActionDescriptor> clazz) {
        List<UIActionDescriptor> uiDescriptors = getDescriptors(clazz);
        if(uiDescriptors == null) {
            return null;
        }
        return uiDescriptors.get(0);
    }
    public List<UIActionDescriptor> getDescriptors(Class<? extends UIActionDescriptor> clazz) {
        return descriptors.get(clazz);
    }

    public void syncDescriptors() {
        // These action listeners will actually fire before
        // the action listeners added when the menu bar and/or
        // tool bar were built even those these two are being
        // added after the others.  Swing fires the action
        // listeners in reverse order.  This means that the
        // components will be sync'ed before the main listeners
        // fire.

        MenuBarActionDescriptor mDesc =
            (MenuBarActionDescriptor) getDescriptor(MenuBarActionDescriptor.class);
        PopupMenuActionDescriptor pDesc =
            (PopupMenuActionDescriptor) getDescriptor(PopupMenuActionDescriptor.class);
        ToolBarActionDescriptor tDesc =
            (ToolBarActionDescriptor) getDescriptor(ToolBarActionDescriptor.class);

        final JMenuItem mnub = (mDesc != null  && mDesc.isCheckMenu ? (JMenuItem) mDesc.component : null);
        final JMenuItem mnup = (pDesc != null && pDesc.isCheckMenu ? (JMenuItem) pDesc.component : null);
        final AbstractButton btn = (tDesc != null  && tDesc.isToggle ? tDesc.component : null);

        if(mnub != null) {
            mnub.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JCheckBoxMenuItem mnu = (JCheckBoxMenuItem) e.getSource();
                    if(mnup != null) {
                        mnup.setSelected(mnu.isSelected());
                    }
                    if(btn != null) {
                        btn.setSelected(mnu.isSelected());
                    }
                }
            });
        }

        if(mnup != null) {
            mnup.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JCheckBoxMenuItem mnu = (JCheckBoxMenuItem) e.getSource();
                    if(mnub != null) {
                        mnub.setSelected(mnu.isSelected());
                    }
                    if(btn != null) {
                        btn.setSelected(mnu.isSelected());
                    }
                }
            });
        }

        if(btn != null) {
            btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JToggleButton btnToggle = (JToggleButton) e.getSource();
                    if(mnub != null) {
                        mnub.setSelected(btnToggle.isSelected());
                    }
                    if(mnup != null) {
                        mnup.setSelected(btnToggle.isSelected());
                    }
                }
            });
        }
    }


    //////////
    // MISC //
    //////////

    public void validate(String state) {
        boolean valid = (validator == null) || validator.isValid(id);
        for(List<UIActionDescriptor> uiDescriptors : descriptors.values()) {
            for(UIActionDescriptor descriptor : uiDescriptors) {
                descriptor.validate(state, valid);
            }
        }
    }

    public void execute() {
        if(listener != null) {
            listener.actionPerformed(null, this);
        }
    }
    public JMenuItem getMenuBarComponent() {
        MenuBarActionDescriptor mDesc =
            (MenuBarActionDescriptor) getDescriptor(MenuBarActionDescriptor.class);
        if(mDesc == null) {
            return null;
        }
        return (JMenuItem) mDesc.component;
    }

    public JMenuItem getPopupMenuComponent() {
        PopupMenuActionDescriptor mDesc =
            (PopupMenuActionDescriptor) getDescriptor(PopupMenuActionDescriptor.class);
        if(mDesc == null) {
            return null;
        }
        return (JMenuItem) mDesc.component;
    }

    public AbstractButton getToolBarComponent() {
        ToolBarActionDescriptor tDesc =
            (ToolBarActionDescriptor) getDescriptor(ToolBarActionDescriptor.class);
        if(tDesc == null) {
            return null;
        }
        return tDesc.component;
    }
}
