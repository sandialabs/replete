package replete.ui.panels;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import replete.extensions.ui.RotatedLabel;
import replete.ui.button.RCheckBox;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.uidebug.UiDebugUtil;
import replete.ui.windows.ExampleFrame;


public class StackPanel extends JPanel {

    // Currently it is not StackPanel's responsibility to
    // respond to the clicking or hovering over of the
    // individual stacked panels.  That is the burden
    // of the client code (push returns the constructed
    // panel).  If needed, one could imagine that the
    // client code passes an entire JPanel that is
    // wrapped up inside the stacked panel that is
    // returned (instead of just a JLabel).


    ////////////
    // FIELDS //
    ////////////

    public static final int DEFAULT_PANEL_WIDTH = 35;
    public static final RotatedLabel.Direction DEFAULT_VERTICAL_DIRECTION = RotatedLabel.Direction.VERTICAL_DOWN;
    public static final String NORTH = "N";  // For horizontal (laying down flat) stack orientation.
    public static final String SOUTH = "S";
    public static final String EAST  = "E";  // For vertical (standing up straight) stack orientation.
    public static final String WEST  = "W";

    private String pushDirection;  // Defines orientation and side where push/pops happens.
    private int panelWidth;
    private RotatedLabel.Direction vertLabelDir;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StackPanel(String pushDir) {
        this(pushDir, DEFAULT_PANEL_WIDTH, DEFAULT_VERTICAL_DIRECTION);
    }
    public StackPanel(String pushDir, int panelW) {
        this(pushDir, panelW, DEFAULT_VERTICAL_DIRECTION);
    }
    public StackPanel(String pushDir, RotatedLabel.Direction newDir) {
        this(pushDir, DEFAULT_PANEL_WIDTH, newDir);
    }
    public StackPanel(String pushDir, int panelW, RotatedLabel.Direction newDir) {
        pushDirection = pushDir.toUpperCase();
        panelWidth = panelW;
        vertLabelDir = newDir;
        if(pushDirection.equals(EAST) || pushDirection.equals(WEST)) {
            Lay.BxLtg(this, "X");
        } else if(pushDirection.equals(NORTH) || pushDirection.equals(SOUTH)){
            Lay.BxLtg(this, "Y");
        } else {
            throw new IllegalArgumentException("Must supply a valid push direction.");
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public int getPanelWidth() {
        return panelWidth;
    }
    public void setPanelWidth(int newWidth) {
        panelWidth = newWidth;
        for(int c = 0; c < getComponentCount(); c++) {
            JComponent cmp = (JComponent) getComponent(c);
            if(pushDirection.equals(EAST) || pushDirection.equals(WEST)) {
                Lay.hn(cmp, "prefW=" + panelWidth);
            } else {
                Lay.hn(cmp, "prefH=" + panelWidth);
            }
        }
        updateUI();
    }
    public RotatedLabel.Direction getVertLabelDir() {
        return vertLabelDir;
    }
    public void setVertLabelDir(RotatedLabel.Direction vertLabelDir) {
        this.vertLabelDir = vertLabelDir;
        if(pushDirection.equals(EAST) || pushDirection.equals(WEST)) {
            for(int c = 0; c < getComponentCount(); c++) {
                JComponent cmp = (JComponent) getComponent(c);
                RotatedLabel rl = (RotatedLabel) cmp.getComponent(0);
                rl.setDirection(vertLabelDir);
            }
        }
    }


    ////////////////
    // PUSH / POP //
    ////////////////

    public JPanel push(JLabel lbl) {
        JPanel pnl = new GradientPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        if(pushDirection.equals(EAST) || pushDirection.equals(WEST)) {
            RotatedLabel lblRotated = new RotatedLabel(lbl.getText(),
                lbl.getIcon(), SwingConstants.LEADING);
            lblRotated.setDirection(vertLabelDir);
            Lay.GBLtg(pnl, "eb=5t,prefW=" + panelWidth);
            gbc.anchor = GridBagConstraints.PAGE_START;
            gbc.weighty = 0.1;
            pnl.add(lblRotated, gbc);
        } else {
            Lay.GBLtg(pnl, "eb=5l,prefH=" + panelWidth);
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.weightx = 0.1;
            pnl.add(lbl, gbc);
        }
        Border raised = BorderFactory.createRaisedBevelBorder();
        Lay.augb(pnl, raised);
        if(pushDirection.equals(EAST) || pushDirection.equals(SOUTH)) {
            add(pnl);
        } else {
            add(pnl, 0);
        }
        updateUI();
        return pnl;
    }

    public void pop() {
        if(getComponentCount() == 0) {
            return;
        }
        if(pushDirection.equals(EAST) || pushDirection.equals(SOUTH)) {
            remove(getComponentCount() - 1);
        } else {
            remove(0);
        }
        updateUI();
    }

    public void popAll() {
        removeAll();
        updateUI();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

        StackPanel pnlStackNorth = new StackPanel(SOUTH);
        StackPanel pnlStackEast  = new StackPanel(WEST);
        StackPanel pnlStackWest  = new StackPanel(EAST);
        StackPanel pnlStackSouth = new StackPanel(NORTH);
        JTextField txtPanelWidth = new JTextField("" + DEFAULT_PANEL_WIDTH);
        JCheckBox chkFlip = new RCheckBox("Flip Label (E&&W)");

        JButton btnNorthAdd = Lay.btn("North", CommonConcepts.ADD,
            (ActionListener) e -> {
                pnlStackNorth.setPanelWidth(Integer.parseInt(txtPanelWidth.getText()));
                pnlStackNorth.push(Lay.lb("A North Label", RepleteImageModel.NORTH));
            }
        );
        JButton btnNorthRemove = Lay.btn("North", CommonConcepts.REMOVE,
            (ActionListener) e -> {
                pnlStackNorth.setPanelWidth(Integer.parseInt(txtPanelWidth.getText()));
                pnlStackNorth.pop();
            }
        );
        JButton btnSouthAdd = Lay.btn("South", CommonConcepts.ADD,
            (ActionListener) e -> {
                pnlStackSouth.setPanelWidth(Integer.parseInt(txtPanelWidth.getText()));
                pnlStackSouth.push(Lay.lb("A South Label", RepleteImageModel.SOUTH));
            }
        );
        JButton btnSouthRemove = Lay.btn("South", CommonConcepts.REMOVE,
            (ActionListener) e -> {
                pnlStackSouth.setPanelWidth(Integer.parseInt(txtPanelWidth.getText()));
                pnlStackSouth.pop();
            }
        );
        JButton btnEastAdd = Lay.btn("East", CommonConcepts.ADD,
            (ActionListener) e -> {
                pnlStackEast.setPanelWidth(Integer.parseInt(txtPanelWidth.getText()));
                pnlStackEast.setVertLabelDir((chkFlip.isSelected())
                    ?RotatedLabel.Direction.VERTICAL_UP:RotatedLabel.Direction.VERTICAL_DOWN);
                pnlStackEast.push(Lay.lb("An East Label", RepleteImageModel.EAST));
            }
        );
        JButton btnEastRemove = Lay.btn("East", CommonConcepts.REMOVE,
            (ActionListener) e -> {
                pnlStackEast.setPanelWidth(Integer.parseInt(txtPanelWidth.getText()));
                pnlStackEast.setVertLabelDir((chkFlip.isSelected())
                    ?RotatedLabel.Direction.VERTICAL_UP:RotatedLabel.Direction.VERTICAL_DOWN);
                pnlStackEast.pop();
            }
        );
        JButton btnWestAdd = Lay.btn("West", CommonConcepts.ADD,
            (ActionListener) e -> {
                pnlStackWest.setPanelWidth(Integer.parseInt(txtPanelWidth.getText()));
                pnlStackWest.setVertLabelDir((chkFlip.isSelected())
                    ?RotatedLabel.Direction.VERTICAL_UP:RotatedLabel.Direction.VERTICAL_DOWN);
                pnlStackWest.push(Lay.lb("A West Label", RepleteImageModel.WEST));
            }
        );
        JButton btnWestRemove = Lay.btn("West", CommonConcepts.REMOVE,
            (ActionListener) e -> {
                pnlStackWest.setPanelWidth(Integer.parseInt(txtPanelWidth.getText()));
                pnlStackWest.setVertLabelDir((chkFlip.isSelected())
                    ?RotatedLabel.Direction.VERTICAL_UP:RotatedLabel.Direction.VERTICAL_DOWN);
                pnlStackWest.pop();
            }
        );

        UiDebugUtil.enableColor();

        int w = 100;
        Lay.hn(btnNorthAdd,    "prefW=" + w);
        Lay.hn(btnNorthRemove, "prefW=" + w);
        Lay.hn(btnSouthAdd,    "prefW=" + w);
        Lay.hn(btnSouthRemove, "prefW=" + w);
        Lay.hn(btnEastAdd,     "prefW=" + w);
        Lay.hn(btnEastRemove,  "prefW=" + w);
        Lay.hn(btnWestAdd,     "prefW=" + w);
        Lay.hn(btnWestRemove,  "prefW=" + w);

        JLabel lblPanelWidth = new JLabel("Panel Width:");
        Lay.hn(lblPanelWidth, "prefW=" + w);
        Lay.hn(txtPanelWidth, "prefW=" + w);

        JPanel pnlControls = Lay.GBL();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; pnlControls.add(btnNorthAdd, gbc);
        gbc.gridx = 1; gbc.gridy = 0; pnlControls.add(btnNorthRemove, gbc);
        gbc.gridx = 0; gbc.gridy = 1; pnlControls.add(btnSouthAdd, gbc);
        gbc.gridx = 1; gbc.gridy = 1; pnlControls.add(btnSouthRemove, gbc);
        gbc.gridx = 0; gbc.gridy = 2; pnlControls.add(btnEastAdd, gbc);
        gbc.gridx = 1; gbc.gridy = 2; pnlControls.add(btnEastRemove, gbc);
        gbc.gridx = 0; gbc.gridy = 3; pnlControls.add(btnWestAdd, gbc);
        gbc.gridx = 1; gbc.gridy = 3; pnlControls.add(btnWestRemove, gbc);
        gbc.gridx = 0; gbc.gridy = 4; pnlControls.add(lblPanelWidth, gbc);
        gbc.gridx = 1; gbc.gridy = 4; pnlControls.add(txtPanelWidth, gbc);
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 5; pnlControls.add(chkFlip, gbc);

        Lay.BLtg(new ExampleFrame(),
            "N", pnlStackNorth,
            "E", pnlStackEast,
            "W", pnlStackWest,
            "S", pnlStackSouth,
            "C", pnlControls,
            "visible"
        );
    }
}
