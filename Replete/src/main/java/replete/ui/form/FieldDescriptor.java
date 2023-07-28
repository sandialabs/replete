package replete.ui.form;

import java.awt.Component;

import javax.swing.Icon;

public class FieldDescriptor {


    ////////////
    // FIELDS //
    ////////////

    public FieldPanel pnlField;
    public String     caption;
    public Icon       icon;
    public Component  cmp;
    public int        height;
    public boolean    expandable;
    public String     helpText;
    public String     captionLabelHints;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FieldDescriptor(String caption, Icon icon, Component cmp, int height,
                           boolean expandable, String helpText) {
        this.caption    = caption;
        this.icon       = icon;
        this.cmp        = cmp;
        this.height     = height;
        this.expandable = expandable;
        this.helpText   = helpText;
    }
}
