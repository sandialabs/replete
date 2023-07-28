package replete.ui.sdplus.panels;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import replete.ui.GuiUtil;
import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.UiDefaults;
import replete.ui.sdplus.color.ColorUtil;


/**
 * Displays unique values and the ability to turn on and
 * off each value for an enumerated scale.  Uses radio buttons
 * so only one value is considered selected at any given time
 * (single-select).
 *
 * @author Derek Trumbo
 */

public class EnumScaleSinglePanel extends EnumScaleBasePanel {

    ////////////
    // Fields //
    ////////////

    // Model
    protected EnumScaleSinglePanelModel emodel;

    // UI
    protected List<EnumRadioButton> radioButtons;
    protected EnumRadioButton btnNone;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public EnumScaleSinglePanel(ScaleSetPanel p, EnumScaleSinglePanelModel m) {
        super(p, m);
    }

    ///////////////
    // Pre-Build //
    ///////////////

    @Override
    protected void initBeforeBuild() {
        super.initBeforeBuild();
        emodel = (EnumScaleSinglePanelModel) model;
    }

    @Override
    protected ImageIcon getTitleIcon() {
        return UiDefaults.ENUM_ICON;
    }

    ///////////
    // Build //
    ///////////

    @Override
    protected void buildInnerFilterPanel() {

        pnlFilterInner.removeAll();

        buildRadioButtons();

        countLabels = new ArrayList<JLabel>();
        innerSpacerPanels = new ArrayList<JPanel>();

        // Construct and lay out the check boxes in the order they
        // appear in the constructed list.
        boolean first = true;
        for(EnumRadioButton chkValue : radioButtons) {
            JPanel pnlCheckBox = new JPanel();
            pnlCheckBox.setLayout(new BoxLayout(pnlCheckBox, BoxLayout.X_AXIS));
            pnlCheckBox.setOpaque(false);
            pnlCheckBox.setAlignmentX(LEFT_ALIGNMENT);
            pnlCheckBox.add(chkValue);

            Integer cnt = emodel.getUniqueValueCounts().get(chkValue.value);

            String countLabelText = (showValueCounts ? "  (" + cnt + ") " : "");
            JLabel lblCount = new JLabel(countLabelText);
            lblCount.setFont(filterFont);
            lblCount.setOpaque(false);

            countLabels.add(lblCount);

            pnlCheckBox.add(lblCount);

            if(!first) {
                JPanel pnlTemp = GuiUtil.addBorderedComponent(pnlFilterInner, pnlCheckBox,
                    BorderFactory.createEmptyBorder(innerSpacing, 0, 0, 0));
                pnlTemp.setAlignmentX(LEFT_ALIGNMENT);
                pnlTemp.setOpaque(false);
                innerSpacerPanels.add(pnlTemp);
            } else {
                pnlFilterInner.add(pnlCheckBox);
            }

            first = false;
        }

        // For case when the model has no values for some reason.
        if(radioButtons.size() == 0) {
            JLabel lblNone = new JLabel("<html>&nbsp;&nbsp;<i>" +
                NO_VALUES_TEXT.replaceAll("<", "&lt;") +
                "</i></html>");
            lblNone.setFont(filterFont);
            lblNone.setOpaque(false);
            pnlFilterInner.add(lblNone);
        }

        pnlFilterInner.add(btnNone);

        pnlFilterInner.updateUI();
    }

    protected void buildRadioButtons() {

        radioButtons = new ArrayList<EnumRadioButton>();

        List<Object> uniqueSortedVals = emodel.getUniqueSortedValues();
        Set<Object> selEnumVals = emodel.getSelectedValues();
        Set<Object> forceChecked = emodel.getForceChecked();
        Set<Object> forceUnchecked = emodel.getForceUnchecked();

        ButtonGroup grp = new ButtonGroup();

        for(Object val : uniqueSortedVals) {

            // Decide caption based on object.
            String caption = (val == null) ? NO_VALUE_TEXT : val.toString();

            // Construct check box.
            EnumRadioButton btn = new EnumRadioButton(caption, val);
            btn.setOpaque(false);
            btn.setFont(filterFont);
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Decide initial selection value.
            boolean initSelected;
            if(forceChecked.contains(val)) {
                initSelected = true;
                btn.setEnabled(false);
            } else if(forceUnchecked.contains(val)) {
                initSelected = false;
                btn.setEnabled(false);
            } else if(selEnumVals.contains(val)) {
                initSelected = true;
            } else {
                initSelected = false;
            }
            btn.setSelected(initSelected);

            // Add listener.
            btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateScaleIsSubselected();
                    setHighlighted(true);

                    EnumRadioButton btn = (EnumRadioButton) e.getSource();

                    // Update the model from GUI state.
                    Set<Object> selValues = emodel.getSelectedValues();
                    selValues.clear();
                    selValues.add(btn.value);

                    // Fire individual event.
                    fireIndvChangedEvent(btn.value, btn.isSelected());
                }
            });

            // Add to list.
            radioButtons.add(btn);
            grp.add(btn);
        }

        btnNone = new  EnumRadioButton("none-selected-button", null);
        btnNone.setVisible(false);
        grp.add(btnNone);
        if(radioButtons.size() == 0) {
            btnNone.setSelected(true);
        }

        // Updates the color of the check boxes based on fields in the model.
        updateColor();
        //updateShapes();
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Mutators

    @Override
    public void setFilterFont(Font font) {
        super.setFilterFont(font);
        for(EnumRadioButton chk : radioButtons) {
            chk.setFont(font);
        }
    }
    @Override
    public void setShowValueCounts(boolean sv) {
        super.setShowValueCounts(sv);
        for(int l = 0; l < countLabels.size(); l++) {
            JLabel lbl = countLabels.get(l);
            if(showValueCounts) {
                EnumRadioButton chk = radioButtons.get(l);
                Integer cnt = emodel.getUniqueValueCounts().get(chk.value);
                lbl.setText("  (" + cnt + ") ");
            } else {
                lbl.setText("");
            }
        }
    }

    ////////////
    // Update //
    ////////////

    // This method not actually called - more here for completeness.
    // This is due primarily to the need to fire both individual and
    // coalesced events depending on the circumstance.  So updating
    // the model happens in other places in this class (see check box
    // ActionListener and changeSelectionAll).
    @Override
    public void updateModelFromUI() {
        for(EnumRadioButton chk : radioButtons) {
            if(chk.isSelected()) {
                emodel.getSelectedValues().remove(chk.value);
            } else {
                emodel.getSelectedValues().add(chk.value);
            }
        }
        updateScaleIsSubselected();
        fireIndvChangedEvent(
            (emodel.getSelectedValues().size() == 0 ? null :
                emodel.getSelectedValues().toArray()[0]),
            emodel.getSelectedValues().size() != 0);
    }

    @Override
    protected void updateInternalColor(boolean isVisTypeColor) {
        for(EnumRadioButton cb : radioButtons) {
            if(isVisTypeColor) {
                cb.setBackground(ColorUtil.lighten(emodel.getColor(cb.value)));
                cb.setOpaque(true);
            } else {
                cb.setBackground(getBackground());
                cb.setOpaque(false);
            }
        }
    }

    //////////
    // Misc //
    //////////

    // Select/Deselect All

    @Override
    public void selectAll(boolean suppressEvents) {
        if(radioButtons.size() != 0) {
            EnumRadioButton btn = radioButtons.get(0);
            if(!btn.isSelected()) {
                btn.setSelected(true);
                updateScaleIsSubselected();
                if(!suppressEvents) {
                    fireIndvChangedEvent(btn.value, true);
                }
            }
        }
    }

    @Override
    public void deselectAll(boolean suppressEvents) {
        emodel.getSelectedValues().clear();
        if(!btnNone.isSelected()) {
            btnNone.setSelected(true);
            updateScaleIsSubselected();
            if(!suppressEvents) {
                fireIndvChangedEvent(null, false);
            }
        }
    }

    // An EnumScalePanel is subselected if any of the check boxes
    // is not checked.
    @Override
    protected boolean determineIsSubselected() {
        if(super.determineIsSubselected()) {
            return true;
        }
        if(defaultStateAllSelected) {
            return btnNone.isSelected();
        }
        return !btnNone.isSelected();
    }

    /////////////////////////
    // Custom Radio Button //
    /////////////////////////

    // A radio button that knows the object to which it corresponds.
    protected class EnumRadioButton extends JRadioButton {
        protected Object value;
        public EnumRadioButton(String caption, Object v) {
            super(caption);
            value = v;
        }
        @Override
        public void setFont(Font f) {
            if(value == null) {
                super.setFont(f.deriveFont(Font.ITALIC));
            } else {
                super.setFont(f);
            }
        }
    }

// Shape Stuff (not yet implemented)
//  @Override
//  public void updateShape(boolean isShape) {
//      for (ECheckBox cb : checkboxes) {
//          if (isShape) {
//              Shape shp = esvs.getShape(cb.getText(), null);
//              ImageIcon imgA = createIcon(shp, new byte[] { -128, -1 });
//              ImageIcon imgB = createIcon(shp, new byte[] { 0, -1 });
//              cb.setIcon(imgA);
//              cb.setSelectedIcon(imgB);
//          } else {
//              cb.setIcon(null);
//              cb.setSelectedIcon(null);
//          }
//      }
//  }
//  private ImageIcon createIcon(Shape shp, byte[] intens) {
//      // In color model, first index is background (0 = black)
//      // second index is ignored, fill color is always white).
//      BufferedImage img = new BufferedImage(24, 12,
//              BufferedImage.TYPE_BYTE_BINARY, new IndexColorModel(1, 2,
//                      intens, intens, intens));
//      Graphics2D gr = img.createGraphics();
//      gr.translate(18, 6);
//      gr.fill(shp);
//      return new ImageIcon(img);
//  }
}
