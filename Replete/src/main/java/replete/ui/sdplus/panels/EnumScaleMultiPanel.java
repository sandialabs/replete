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
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.ui.GuiUtil;
import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.UiDefaults;
import replete.ui.sdplus.color.ColorUtil;
import replete.ui.sdplus.events.EnumScalePanelCoalescedChangedEvent;


/**
 * Displays unique values and the ability to turn on and
 * off each value for an enumerated scale.  Uses check boxes
 * so each value can be toggled independently (multi-select).
 *
 * @author Derek Trumbo
 */

public class EnumScaleMultiPanel extends EnumScaleBasePanel {

    ////////////
    // Fields //
    ////////////

    // Model
    protected EnumScaleMultiPanelModel emodel;

    // UI
    protected List<EnumCheckBox> checkboxes;

    // UI Settings
    protected boolean coalesceEvents = UiDefaults.ENUM_COALESCE_EVENTS;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public EnumScaleMultiPanel(ScaleSetPanel p, EnumScaleMultiPanelModel m) {
        super(p, m);
    }

    ///////////////
    // Pre-Build //
    ///////////////

    @Override
    protected void initBeforeBuild() {
        super.initBeforeBuild();
        emodel = (EnumScaleMultiPanelModel) model;
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

        buildCheckBoxes();

        countLabels = new ArrayList<JLabel>();
        innerSpacerPanels = new ArrayList<JPanel>();

        // Construct and lay out the check boxes in the order they
        // appear in the constructed list.
        boolean first = true;
        for(EnumCheckBox chkValue : checkboxes) {
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
        if(checkboxes.size() == 0) {
            JLabel lblNone = new JLabel("<html>&nbsp;&nbsp;<i>" +
                NO_VALUES_TEXT.replaceAll("<", "&lt;") +
                "</i></html>");
            lblNone.setFont(filterFont);
            lblNone.setOpaque(false);
            pnlFilterInner.add(lblNone);
        }

        pnlFilterInner.updateUI();
    }

    protected void buildCheckBoxes() {

        checkboxes = new ArrayList<EnumCheckBox>();

        List<Object> uniqueSortedVals = emodel.getUniqueSortedValues();
        Set<Object> selEnumVals = emodel.getSelectedValues();
        Set<Object> forceChecked = emodel.getForceChecked();
        Set<Object> forceUnchecked = emodel.getForceUnchecked();

        for(Object val : uniqueSortedVals) {

            // Decide caption based on object.
            String caption = (val == null) ? NO_VALUE_TEXT : val.toString();

            // Construct check box.
            EnumCheckBox chk = new EnumCheckBox(caption, val);
            chk.setOpaque(false);
            chk.setFont(filterFont);
            chk.setMargin(new Insets(0, 0, 0, 0));
            chk.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Decide initial selection value.
            boolean initSelected;
            if(forceChecked.contains(val)) {
                initSelected = true;
                chk.setEnabled(false);
            } else if(forceUnchecked.contains(val)) {
                initSelected = false;
                chk.setEnabled(false);
            } else if(selEnumVals.contains(val)) {
                initSelected = true;
            } else {
                initSelected = false;
            }
            chk.setSelected(initSelected);

            // Add listener.
            chk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateScaleIsSubselected();
                    setHighlighted(true);

                    EnumCheckBox echk = (EnumCheckBox) e.getSource();

                    // Update the model from GUI state.
                    Set<Object> selValues = emodel.getSelectedValues();
                    if(echk.isSelected()) {
                        selValues.add(echk.value);
                    } else {
                        selValues.remove(echk.value);
                    }

                    // Fire individual event.
                    fireIndvChangedEvent(echk.value, echk.isSelected());
                }
            });

            // Add to list.
            checkboxes.add(chk);
        }

        // Updates the color of the check boxes based on fields in the model.
        updateColor();
        //updateShapes();
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Accessors

    public boolean isCoalesceEvents() {
        return coalesceEvents;
    }

    // Mutators

    @Override
    public void setFilterFont(Font font) {
        super.setFilterFont(font);
        for(EnumCheckBox chk : checkboxes) {
            chk.setFont(font);
        }
    }
    public void setCoalesceEvents(boolean ce) {
        coalesceEvents = ce;
    }
    @Override
    public void setShowValueCounts(boolean sv) {
        super.setShowValueCounts(sv);
        for(int l = 0; l < countLabels.size(); l++) {
            JLabel lbl = countLabels.get(l);
            if(showValueCounts) {
                EnumCheckBox chk = checkboxes.get(l);
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
        for(EnumCheckBox chk : checkboxes) {
            if(chk.isSelected()) {
                emodel.getSelectedValues().remove(chk.value);
            } else {
                emodel.getSelectedValues().add(chk.value);
            }
        }
        updateScaleIsSubselected();
        EnumScalePanelCoalescedChangedEvent e =
            new EnumScalePanelCoalescedChangedEvent(parent, model.getKey(), this, model);
        fireValueChangedEvent(e);
    }

    @Override
    protected void updateInternalColor(boolean isVisTypeColor) {
        for(EnumCheckBox cb : checkboxes) {
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
        changeSelectionAll(true, suppressEvents);
    }

    @Override
    public void deselectAll(boolean suppressEvents) {
        changeSelectionAll(false, suppressEvents);
    }

    protected void changeSelectionAll(boolean toState, boolean suppressEvents) {
        Set<Object> selValues = emodel.getSelectedValues();

        for(EnumCheckBox chk : checkboxes) {

            // Only if this check box isn't in the right state...
            if(chk.isSelected() != toState && chk.isEnabled()) {

                // Update model.
                if(toState) {
                    selValues.add(chk.value);
                } else {
                    selValues.remove(chk.value);
                }

                // Update UI.
                chk.setSelected(toState);

                // Fire individual events.
                if(!suppressEvents && !coalesceEvents) {
                    fireIndvChangedEvent(chk.value, toState);
                }
            }
        }

        updateScaleIsSubselected();

        // Fire coalesced event.
        if(!suppressEvents && coalesceEvents) {
            EnumScalePanelCoalescedChangedEvent e =
                new EnumScalePanelCoalescedChangedEvent(parent, model.getKey(), this, model);
            fireValueChangedEvent(e);
        }
    }

    // An EnumScalePanel is subselected if any of the check boxes
    // is not checked.
    @Override
    protected boolean determineIsSubselected() {
        if(super.determineIsSubselected()) {
            return true;
        }
        for(EnumCheckBox chk : checkboxes) {
            if(!chk.isSelected() && defaultStateAllSelected ||
                    chk.isSelected() && !defaultStateAllSelected) {
                return true;
            }
        }
        return false;
    }

    //////////////////////
    // Custom Check Box //
    //////////////////////

    // A check box that knows the object to which it corresponds.
    protected class EnumCheckBox extends JCheckBox {
        protected Object value;
        public EnumCheckBox(String caption, Object v) {
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
