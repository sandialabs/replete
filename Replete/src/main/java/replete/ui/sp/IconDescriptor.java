package replete.ui.sp;

import javax.swing.ImageIcon;

public class IconDescriptor extends RulerDescriptor {


    ///////////
    // FIELD //
    ///////////

    private ImageIcon icon;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IconDescriptor(ImageIcon icon) {
        this(icon, null, null);
    }
    public IconDescriptor(ImageIcon icon, String toolTip) {
        this(icon, toolTip, null);
    }
    public IconDescriptor(ImageIcon icon, RulerClickListener clickListener) {
        this(icon, null, clickListener);
    }
    public IconDescriptor(ImageIcon icon, String toolTip, RulerClickListener clickListener) {
        super(toolTip, clickListener);
        this.icon = icon;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public ImageIcon getIcon() {
        return icon;
    }
}
