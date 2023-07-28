package replete.ui.uiaction;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;

/**
 * @author Derek Trumbo
 */

public class ToolBarActionDescriptor extends UIActionDescriptor {


    ////////////
    // FIELDS //
    ////////////

    protected String group;
    protected String toolTipText;
    protected ImageIcon icon;
    protected UIActionListener listener;
    protected boolean isToggle;
    protected AbstractButton component;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getGroup() {
        return group;
    }
    public String getToolTipText() {
        return toolTipText;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public UIActionListener getListener() {
        return listener;
    }
    public boolean isToggle() {
        return isToggle;
    }
    @Override
    public AbstractButton getComponent() {
        return component;
    }

    // Mutators

    public ToolBarActionDescriptor setGroup(String group) {
        this.group = group;
        return this;
    }
    public ToolBarActionDescriptor setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
        return this;
    }
    public ToolBarActionDescriptor setIcon(ImageIcon icon) {
        this.icon = icon;
        return this;
    }
    public ToolBarActionDescriptor setIcon(ImageModelConcept concept) {
        icon = ImageLib.get(concept);
        return this;
    }
    public ToolBarActionDescriptor setListener(UIActionListener listener) {
        this.listener = listener;
        return this;
    }
    public ToolBarActionDescriptor setToggle(boolean isToggle) {
        this.isToggle = isToggle;
        return this;
    }
    public ToolBarActionDescriptor setComponent(AbstractButton component) {
        this.component = component;
        return this;
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
}
