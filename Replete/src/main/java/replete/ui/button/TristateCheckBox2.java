package replete.ui.button;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.text.RLabel;

public class TristateCheckBox2 extends RLabel {


    ////////////
    // FIELDS //
    ////////////

    private static final ImageIcon selected     = ImageLib.get(RepleteImageModel.CHECKBOX_ON);
    private static final ImageIcon unselected   = ImageLib.get(RepleteImageModel.CHECKBOX_OFF);
    private static final ImageIcon halfSelected = ImageLib.get(RepleteImageModel.CHECKBOX_HALF);

    private Icon userIcon;

    private TristateValue value = TristateValue.UNSELECTED;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TristateCheckBox2() {
        super();
        init();
    }
    public TristateCheckBox2(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        init();
    }
    public TristateCheckBox2(Icon image) {
        super(image);
        init();
    }
    public TristateCheckBox2(ImageModelConcept concept) {
        super(concept);
        init();
    }
    public TristateCheckBox2(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        init();
    }
    public TristateCheckBox2(String text, Icon icon) {
        super(text, icon);
        init();
    }
    public TristateCheckBox2(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        init();
    }
    public TristateCheckBox2(String text) {
        super(text);
        init();
    }


    public TristateCheckBox2(TristateValue value) {
        super();
        this.value = value;
        init();
    }
    public TristateCheckBox2(String text, TristateValue value) {
        super(text);
        this.value = value;
        init();
    }

    private void init() {
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(isEnabled()) {
                    if(value == TristateValue.UNSELECTED || value == TristateValue.HALF_SELECTED) {
                        setValue(TristateValue.SELECTED);
                    } else {
                        setValue(TristateValue.UNSELECTED);
                    }
                }
            }
        });
        setValue(value);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public TristateValue getValue() {
        return value;
    }

    public Icon getUserIcon() {
        return userIcon;
    }

    public void setValue(TristateValue value) {
        this.value = value;
        setIconInternal();
        fireValueNotifier();
    }

    public void setUserIcon(Icon userIcon) {
        this.userIcon = userIcon;
        setIconInternal();
    }

    private void setIconInternal() {
        ImageIcon checkIcon;
        switch(value) {
            case SELECTED:   checkIcon = selected;     break;
            case UNSELECTED: checkIcon = unselected;   break;
            default:         checkIcon = halfSelected; break;
        }
        int width = checkIcon.getIconWidth();
        width += userIcon != null ? userIcon.getIconWidth() + 2 : 0;
        int height = checkIcon.getIconHeight();
        height = userIcon != null ? Math.max(height, userIcon.getIconHeight()) : height;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(checkIcon.getImage(), 0, 0, null);
        if(userIcon != null) {
            userIcon.paintIcon(new JLabel(), g2d, checkIcon.getIconWidth() + 2, 0);
        }
        super.setIcon(new ImageIcon(bufferedImage));
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier valueNotifier = new ChangeNotifier(this);
    public void addValueListener(ChangeListener listener) {
        valueNotifier.addListener(listener);
    }
    private void fireValueNotifier() {
        valueNotifier.fireStateChanged();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        TristateCheckBox2 shitBox;
        Lay.fr("Shit",
            Lay.FL("L",
                shitBox = new TristateCheckBox2("shit", TristateValue.HALF_SELECTED),
                Lay.btn("Disable", (ActionListener) e -> shitBox.setEnabled(!shitBox.isEnabled()))
            ),
            "size=600,visible,center=2"
        );
        shitBox.setUserIcon(ImageLib.get(RepleteImageModel.APPLE));
    }

}
