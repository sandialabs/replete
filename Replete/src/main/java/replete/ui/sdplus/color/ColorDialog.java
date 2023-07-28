package replete.ui.sdplus.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.GuiUtil;
import replete.ui.sdplus.images.SdPlusImageModel;
import replete.ui.windows.escape.EscapeDialog;


/**
 * @author Derek Trumbo
 */

public class ColorDialog extends EscapeDialog {


    ///////////
    // ENUMS //
    ///////////

    public enum ColorDialogResult {
        APPROVED,
        RESTORE,
        CANCELED
    }


    ////////////
    // FIELDS //
    ////////////

    protected JColorChooser colorChooser;
    protected ColorSpecPanel pnlSpec;
    protected ColorDialogResult result = ColorDialogResult.CANCELED;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ColorDialog(JFrame parent, String title, String desc, String rd, ColorMap colorMap, boolean allowEdits) {
        super(parent, title, true);
        initGUI(title, desc, rd, colorMap, allowEdits);
    }

    public ColorDialog(JDialog parent, String title, String desc, String rd, ColorMap colorMap, boolean allowEdits) {
        super(parent, title, true);
        initGUI(title, desc, rd, colorMap, allowEdits);
    }

    protected void initGUI(String title, String desc, String rd, ColorMap colorMap, boolean allowEdits) {
        setIcon(SdPlusImageModel.COLORS);

        setLayout(new BorderLayout());

        pnlSpec = new ColorSpecPanel(colorMap, allowEdits);
        pnlSpec.selectedItemChangeNotifier.addListener(selectedItemChangeListener);

        colorChooser = new JColorChooser();
        colorChooser.setColor(pnlSpec.getSelectedColor());

        Border b = BorderFactory.createEmptyBorder(10, 10, 10, 0);
        JPanel pnl = GuiUtil.addBorderedComponent(this, pnlSpec, b, BorderLayout.WEST);
        pnl.setMinimumSize(new Dimension(200, 10000));
        pnl.setPreferredSize(new Dimension(200, 10000));
        pnl.setMaximumSize(new Dimension(200, 10000));
        Border b2 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        GuiUtil.addBorderedComponent(this, colorChooser, b2, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel();

        JButton btnSave = new JButton("Save");
        btnSave.setMnemonic('S');
        btnSave.addActionListener(e -> {
            result = ColorDialogResult.APPROVED;
            dispose();                            // getColorMap can now be queried.
        });

        JButton btnRestore = new JButton("Restore Defaults");
        btnRestore.setMnemonic('R');
        btnRestore.addActionListener(e -> {
            result = ColorDialogResult.RESTORE;
            dispose();
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic('C');
        btnCancel.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(btnSave);

        pnlButtons.add(btnSave);
        pnlButtons.add(btnRestore);
        pnlButtons.add(btnCancel);

        Border b3 = BorderFactory.createEmptyBorder(0, 10, 10, 10);
        GuiUtil.addBorderedComponent(this, pnlButtons, b3, BorderLayout.SOUTH);

        JLabel lblDescription = new JLabel("<HTML>" + desc + "<BR><U>Restore Defaults</U>: " + rd + "</HTML>");

        Border b4 = BorderFactory.createEmptyBorder(10, 10, 0, 10);
        GuiUtil.addBorderedComponent(this, lblDescription, b4, BorderLayout.NORTH);

        colorChooser.getSelectionModel().addChangeListener(colorChooserChangeListener);

        colorChooserChangeNotifier.addListener(pnlSpec.colorChooserChangeListener);

        setSize(700, 520);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public ColorDialogResult getResult() {
        return result;
    }
    public ColorMap getColorMap() {
        return pnlSpec.listModelToMap();
    }


    /////////////////////////
    // LISTENER / NOTIFIER //
    /////////////////////////

    protected ChangeNotifier colorChooserChangeNotifier = new ChangeNotifier(this);
    protected ChangeListener colorChooserChangeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent arg0) {
            if(!suppressEvent) {
                colorChooserChangeNotifier.setSource(colorChooser.getColor());
                colorChooserChangeNotifier.fireStateChanged();
            }
        }
    };

    // We only want to fire events when the user has changed
    // the color.
    protected boolean suppressEvent = false;

    protected ChangeListener selectedItemChangeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent arg0) {
            ColorMapping cm = (ColorMapping) arg0.getSource();
            suppressEvent = true;
            colorChooser.setColor(cm.value);
            suppressEvent = false;
        }
    };


    ///////////////
    // TEST MAIN //
    ///////////////

    public static void main(String[] args) {
        ColorMap map = new ColorMap();
        map.put("Colorado", Color.blue);
        map.put("Kentucky", Color.red);
        map.put("Washington", Color.green);
        ColorDialog dlg = new ColorDialog((JFrame) null, "Dude where's my car?", "Dude!", "Will do stuff!", map, true);
        dlg.setVisible(true);
        if(dlg.getResult() == ColorDialogResult.APPROVED) {
            ColorMap newMap = dlg.getColorMap();
            for(Object s : newMap.keySet()) {
                System.out.println(s+"="+newMap.get(s));
            }
        }
    }
}
