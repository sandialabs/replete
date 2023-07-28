package replete.ui.form2;

import java.awt.Component;

import javax.swing.Icon;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;

public class FieldDescriptor {


    ////////////
    // FIELDS //
    ////////////

    public String     caption           = null;
    public Icon       icon              = null;
    public Component  component         = null;
    public int        height            = NewRFormPanel.DEFAULT_HEIGHT;
    public boolean    expandable        = false;
    public boolean    fill              = false;
    public String     helpText          = null;
    public String     captionLabelHints = null;
    public int        marginTop         = 0;
    public int        marginBottom      = 0;
    public HelpStyle  helpStyle         = HelpStyle.INLINE_UNDER;
    public int        helpPadding       = 0;      // Currently only used for INLINE_RIGHT

    public NewFieldPanel pnlField;        // Internal use... eventually should be removed from this class.


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public NewFieldPanel getPnlField() {
        return pnlField;
    }
    public String getCaption() {
        return caption;
    }
    public Icon getIcon() {
        return icon;
    }
    public Component getComponent() {
        return component;
    }
    public int getHeight() {
        return height;
    }
    public boolean isExpandable() {
        return expandable;
    }
    public String getHelpText() {
        return helpText;
    }
    public String getCaptionLabelHints() {
        return captionLabelHints;
    }
    public int getMarginTop() {
        return marginTop;
    }
    public int getMarginBottom() {
        return marginBottom;
    }
    public boolean isFill() {
        return fill;
    }
    public HelpStyle getHelpStyle() {
        return helpStyle;
    }
    public int getHelpPadding() {
        return helpPadding;
    }

    // Mutators

    public FieldDescriptor setPnlField(NewFieldPanel pnlField) {
        this.pnlField = pnlField;
        return this;
    }
    public FieldDescriptor setCaption(String caption) {
        this.caption = caption;
        return this;
    }
    public FieldDescriptor setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }
    public FieldDescriptor setIcon(ImageModelConcept concept) {
        icon = ImageLib.get(concept);
        return this;
    }
    public FieldDescriptor setComponent(Component component) {
        this.component = component;
        return this;
    }
    public FieldDescriptor setHeight(int height) {
        this.height = height;
        return this;
    }
    public FieldDescriptor setExpandable(boolean expandable) {
        this.expandable = expandable;
        return this;
    }
    public FieldDescriptor setHelpText(String helpText) {
        this.helpText = helpText;
        return this;
    }
    public FieldDescriptor setCaptionLabelHints(String captionLabelHints) {
        this.captionLabelHints = captionLabelHints;
        return this;
    }
    public FieldDescriptor setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }
    public FieldDescriptor setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }
    public FieldDescriptor setFill(boolean fill) {
        this.fill = fill;
        return this;
    }
    public FieldDescriptor setHelpStyle(HelpStyle helpStyle) {
        this.helpStyle = helpStyle;
        return this;
    }
    public FieldDescriptor setHelpPadding(int helpPadding) {
        this.helpPadding = helpPadding;
        return this;
    }
}
