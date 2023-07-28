package replete.ui.sdplus.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import replete.ui.GuiUtil;
import replete.ui.panels.GradientPanel;
import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.UiDefaults;
import replete.ui.sdplus.events.ContScalePanelChangedEvent;
import replete.ui.sdplus.menu.MenuConfiguration;


/**
 * Displays the range and filter criteria for a continuous
 * scale for floating-point numbers.
 *
 * @author Derek Trumbo
 */

public class ContScalePanel extends ScalePanel {

    ////////////
    // Fields //
    ////////////

    // The string value for a text field that indicates that
    // there is no restriction in that direction.
    protected final String SEL_ALL = "";

    // Model
    protected ContScalePanelModel cmodel;

    // UI
    protected JLabel lblRange;                      // First line
    protected JLabel lblRangeLowerValue;
    protected JLabel lblRangeTo;
    protected JLabel lblRangeUpperValue;
    protected GradientPanel pnlColor;
    protected JLabel lblFilter;                     // Second line
    protected JTextField txtFilterLowerValue;
    protected JLabel lblFilterTo;
    protected JTextField txtFilterUpperValue;
    protected JCheckBox chkFilterNulls;
    protected JPanel pnlOuterMargin;
    protected JPanel pnlInnerSpacing;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ContScalePanel(ScaleSetPanel p, ContScalePanelModel m) {
        super(p, m);
    }

    ///////////////
    // Pre-Build //
    ///////////////

    @Override
    protected void initBeforeBuild() {
        super.initBeforeBuild();
        cmodel = (ContScalePanelModel) model;
    }

    @Override
    protected ImageIcon getTitleIcon() {
        return UiDefaults.CONT_ICON;
    }

    ///////////
    // Build //
    ///////////

    @Override
    protected String generateTitleCountText() {
        if(!showTitleCounts) {
            return "";
        }

        return "(" + cmodel.getValidValuesCount() + " total)";
    }

    @Override
    protected JPanel buildFilterPanel() {

        lblRange = new JLabel("Range: ");
        lblRangeLowerValue = new JLabel();
        lblRangeTo = new JLabel(" to ");
        lblRangeUpperValue = new JLabel();

        updateRangeLabels();

        lblRange.setFont(filterFont);
        lblRangeLowerValue.setFont(bigger(filterFont));
        lblRangeTo.setFont(filterFont);
        lblRangeUpperValue.setFont(bigger(filterFont));

        lblFilter = new JLabel("Filter: ");
        lblFilterTo = new JLabel(" to ");

        lblFilter.setFont(filterFont);
        lblFilterTo.setFont(filterFont);

        txtFilterLowerValue = createRangeTextField(cmodel.getFilterLowerValue());
        txtFilterUpperValue = createRangeTextField(cmodel.getFilterUpperValue());
        txtFilterLowerValue.setHorizontalAlignment(SwingConstants.CENTER);
        txtFilterUpperValue.setHorizontalAlignment(SwingConstants.CENTER);

        chkFilterNulls = new JCheckBox(
            "<html>Include <i>" +
            NO_VALUE_TEXT.replaceAll("<", "&lt;") +
            "</i>?</html>");
        chkFilterNulls.setOpaque(false);
        chkFilterNulls.setFont(filterFont);
        chkFilterNulls.setSelected(cmodel.isFilterIncludeNulls());
        chkFilterNulls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setHighlighted(true);
                updateModelFromUI();
                ContScalePanelChangedEvent ev =
                    new ContScalePanelChangedEvent(parent, model.getKey(),
                        ContScalePanel.this, model);
                fireValueChangedEvent(ev);
            }
        });

        pnlColor = new GradientPanel(Color.blue, Color.red);
        pnlColor.setAngle(90);
        pnlColor.setBorder(BorderFactory.createLineBorder(Color.black));
        pnlColor.setPreferredSize(new Dimension(60, 15));
        pnlColor.setVisible(false);

        JPanel pnlFilterLabels = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlFilterLabels.setOpaque(false);
        pnlFilterLabels.add(lblRange);
        pnlFilterLabels.add(lblRangeLowerValue);
        pnlFilterLabels.add(lblRangeTo);
        pnlFilterLabels.add(lblRangeUpperValue);
        pnlFilterLabels.add(new JLabel("    "));
        pnlFilterLabels.add(pnlColor);

        JPanel pnlFilterText = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlFilterText.setOpaque(false);
        pnlFilterText.add(lblFilter);
        pnlFilterText.add(txtFilterLowerValue);
        pnlFilterText.add(lblFilterTo);
        pnlFilterText.add(txtFilterUpperValue);
        pnlFilterText.add(chkFilterNulls);

        Double[] range = cmodel.getValidValuesRange();
        boolean someValues = !Double.isNaN(range[0]) && !Double.isNaN(range[1]);
        txtFilterLowerValue.setEnabled(someValues);
        txtFilterUpperValue.setEnabled(someValues);

        JPanel pnlFilterInner = new JPanel();
        pnlFilterInner.setLayout(new BoxLayout(pnlFilterInner, BoxLayout.Y_AXIS));
        pnlFilterInner.setOpaque(false);

        pnlFilterInner.add(pnlFilterLabels);

        pnlInnerSpacing = GuiUtil.addBorderedComponent(pnlFilterInner, pnlFilterText,
            BorderFactory.createEmptyBorder(innerSpacing, 0, 0, 0));
        pnlInnerSpacing.setOpaque(false);

        JPanel pnlFilterOuter = new JPanel(new BorderLayout());
        pnlFilterOuter.setOpaque(false);
        pnlFilterOuter.setAlignmentX(LEFT_ALIGNMENT);
        pnlFilterOuter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));

        pnlOuterMargin = GuiUtil.addBorderedComponent(pnlFilterOuter, pnlFilterInner,
            BorderFactory.createEmptyBorder(outerMargin, outerMargin,
                outerMargin, outerMargin), BorderLayout.CENTER);
        pnlOuterMargin.setOpaque(false);

        return pnlFilterOuter;
    }

    protected JTextField createRangeTextField(double val) {
        String valStr = SEL_ALL;

        if(!Double.isNaN(val)) {
            valStr = cmodel.convertNumericToString(val);
        }

        // Create text field.
        JTextField txtRange = new JTextField(valStr, getTextFieldLength());
        Dimension pSize = txtRange.getPreferredSize();
        txtRange.setMaximumSize(new Dimension(pSize.width, pSize.height));

        // Highlight the scale panel and set text field background
        // color when key pressed.
        txtRange.getDocument().addDocumentListener(
            new TextFieldChangedListener(txtRange));

        // Select all text when focus gained.
        txtRange.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {}
            public void focusGained(FocusEvent e) {
                JTextField txt = (JTextField) e.getSource();
                txt.selectAll();
            }
        });

        // Enter key pressed on text fields.
        txtRange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtFilterLowerValue.setText(txtFilterLowerValue.getText().trim());
                txtFilterUpperValue.setText(txtFilterUpperValue.getText().trim());

                boolean invalidNumbers = false;

                // These two blocks look duplicated from updateModelFromUI but
                // they need to be distinct for a carefully thought-out
                // user experience.

                // Validate lower text field.
                if(!checkValue(txtFilterLowerValue)) {
                    txtFilterLowerValue.setBackground(UiDefaults.CONT_ERROR_COLOR);
                    txtFilterLowerValue.requestFocusInWindow();
                    invalidNumbers = true;
                }

                // Validate upper text field.
                if(!checkValue(txtFilterUpperValue)) {
                    txtFilterUpperValue.setBackground(UiDefaults.CONT_ERROR_COLOR);
                    if(!invalidNumbers) {
                        txtFilterUpperValue.requestFocusInWindow();
                    }
                    invalidNumbers = true;
                }

                if(!invalidNumbers) {
                    updateModelFromUI();
                    ContScalePanelChangedEvent ev =
                        new ContScalePanelChangedEvent(parent, model.getKey(),
                            ContScalePanel.this, model);
                    fireValueChangedEvent(ev);
                }
            }
        });

        return txtRange;
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Mutators

    @Override
    public void setFilterFont(Font font) {
        super.setFilterFont(font);

        // Change the filter font of all relevant UI components.
        lblRange.setFont(font);
        lblRangeLowerValue.setFont(bigger(font));
        lblRangeTo.setFont(font);
        lblRangeUpperValue.setFont(bigger(font));
        lblFilter.setFont(font);
        lblFilterTo.setFont(font);
        chkFilterNulls.setFont(font);
    }

    @Override
    public void setOuterMargin(int margin) {
        super.setOuterMargin(margin);
        pnlOuterMargin.setBorder(BorderFactory.createEmptyBorder(
            outerMargin, outerMargin, outerMargin, outerMargin));
    }
    @Override
    public void setInnerSpacing(int spacing) {
        super.setInnerSpacing(spacing);
        pnlInnerSpacing.setBorder(BorderFactory.createEmptyBorder(
            innerSpacing, 0, 0, 0));
    }

    ////////////
    // Update //
    ////////////

    // Updates the continuous scale panel's range labels based on
    // - The valid values range
    // This should be called any time one of the above is changed.
    protected void updateRangeLabels() {
        Double[] range = cmodel.getValidValuesRange();
        if(Double.isNaN(range[0]) || Double.isNaN(range[1])) {
            lblRangeLowerValue.setText("<html><i>no values</i></html>");
            lblRangeUpperValue.setText("");
            lblRangeTo.setText("");
        } else {
            lblRangeLowerValue.setText("<html>" + cmodel.convertNumericToString(range[0]) + "</html>");
            lblRangeUpperValue.setText("<html>" + cmodel.convertNumericToString(range[1]) + "</html>");
            lblRangeTo.setText(" to ");
        }
    }

    @Override
    public void updateModelFromUI() {
        boolean invalidNumbers = false;

        // Validate lower text field.
        if(!checkValue(txtFilterLowerValue)) {
            txtFilterLowerValue.setBackground(UiDefaults.CONT_ERROR_COLOR);
            invalidNumbers = true;
        }

        // Validate upper text field.
        if(!checkValue(txtFilterUpperValue)) {
            txtFilterUpperValue.setBackground(UiDefaults.CONT_ERROR_COLOR);
            invalidNumbers = true;
        }

        // Update the model from the UI state.
        if(!invalidNumbers) {

            if(txtFilterLowerValue.getText().equals(SEL_ALL)) {
                cmodel.setFilterLowerValue(Double.NaN);
            } else {
                cmodel.setFilterLowerValue(cmodel.convertStringToNumeric(txtFilterLowerValue.getText()));
                txtFilterLowerValue.setText(cmodel.convertNumericToString(cmodel.getFilterLowerValue()));
            }

            if(txtFilterUpperValue.getText().equals(SEL_ALL)) {
                cmodel.setFilterUpperValue(Double.NaN);
            } else {
                cmodel.setFilterUpperValue(cmodel.convertStringToNumeric(txtFilterUpperValue.getText()));
                txtFilterUpperValue.setText(cmodel.convertNumericToString(cmodel.getFilterUpperValue()));
            }

            txtFilterLowerValue.setBackground(Color.white);
            txtFilterUpperValue.setBackground(Color.white);
        }

        cmodel.setFilterIncludeNulls(chkFilterNulls.isSelected());
        updateScaleIsSubselected();
    }

    // Decide if the string value in the given text field is valid
    // for this continuous scale panel's model.
    protected boolean checkValue(JTextField txt) {
        if(txt.getText().equals(SEL_ALL)) {
            return true;
        }

        return cmodel.isValidString(txt.getText());
    }

    @Override
    public void updateUIFromModel() {
        super.updateUIFromModel();

        // Update lower filter text field.
        if(!Double.isNaN(cmodel.getFilterLowerValue())) {
            txtFilterLowerValue.setText(cmodel.convertNumericToString(cmodel.getFilterLowerValue()));
        } else {
            txtFilterLowerValue.setText(SEL_ALL);
        }

        // Update upper filter text field.
        if(!Double.isNaN(cmodel.getFilterUpperValue())) {
            txtFilterUpperValue.setText(cmodel.convertNumericToString(cmodel.getFilterUpperValue()));
        } else {
            txtFilterUpperValue.setText(SEL_ALL);
        }

        // Update nulls check box.
        chkFilterNulls.setSelected(cmodel.isFilterIncludeNulls());

        // Reset text field colors to white since their values equal
        // those from the model right now.
        txtFilterLowerValue.setBackground(Color.white);
        txtFilterUpperValue.setBackground(Color.white);

        // Rebuild the title counts label.
        lblTitleCounts.setText(generateTitleCountText());

        // Reset the subselected value and range labels.
        updateScaleIsSubselected();  // Duplicate call since it's based off of UI.
        updateRangeLabels();
    }

    @Override
    public void updateColor() {
        updateInternalColor(cmodel.getVisualizationType() == VisualizationType.COLOR);
    }

    protected void updateInternalColor(boolean isVisTypeColor) {
        if(!isVisTypeColor) {
            pnlColor.setVisible(false);
        } else {
            Color[] colors = cmodel.getMinMaxColors();
            pnlColor.setColor1(colors[0]);
            pnlColor.setColor2(colors[1]);
            pnlColor.setVisible(true);
        }
    }

    //////////
    // Misc //
    //////////

    // Select/Deselect All

    @Override
    public void selectAll(boolean suppressEvents) {

        // If it's not already "all-selected"...
        if(!txtFilterLowerValue.getText().equals(SEL_ALL) ||
                    !txtFilterUpperValue.getText().equals(SEL_ALL) ||
                    !chkFilterNulls.isSelected()) {

            // Select all in the controls.
            txtFilterLowerValue.setText(SEL_ALL);
            txtFilterUpperValue.setText(SEL_ALL);
            chkFilterNulls.setSelected(true);

            // Update the model.
            updateModelFromUI();

            // Fire an event if the parent does not have coalesce events on.
            if(!suppressEvents) {
                ContScalePanelChangedEvent ev =
                    new ContScalePanelChangedEvent(parent, model.getKey(),
                        this, model);
                fireValueChangedEvent(ev);
            }
        }
    }

    @Override
    public void deselectAll(boolean suppressEvents) {

        // If it's not already "none-selected"...
        if(!txtFilterLowerValue.getText().equals(getDeselectAllLowerValue()) ||
                    !txtFilterUpperValue.getText().equals(getDeselectAllUpperValue()) ||
                    chkFilterNulls.isSelected()) {

            // Deselect all in the controls.
            txtFilterLowerValue.setText(getDeselectAllLowerValue());
            txtFilterUpperValue.setText(getDeselectAllUpperValue());
            chkFilterNulls.setSelected(false);

            // Update the model.
            updateModelFromUI();

            // Fire an event if the parent does not have coalesce events on.
            if(!suppressEvents) {
                ContScalePanelChangedEvent ev =
                    new ContScalePanelChangedEvent(parent, model.getKey(),
                        this, model);
                fireValueChangedEvent(ev);
            }
        }
    }

    @Override
    protected boolean determineIsSubselected() {
        if(super.determineIsSubselected()) {
            return true;
        }

        // If the scale set panel is in intersection mode, then any
        // deviation from an "all-selected" state is considered
        // subselected.
        if(defaultStateAllSelected) {
            return !txtFilterLowerValue.getText().equals(SEL_ALL) ||
                   !txtFilterUpperValue.getText().equals(SEL_ALL) ||
                   !chkFilterNulls.isSelected();
        }

        // If the scale set panel is in union mode, then any
        // deviation from a "none-selected" state is considered
        // subselected.
        return !txtFilterLowerValue.getText().equals(getDeselectAllLowerValue()) ||
               !txtFilterUpperValue.getText().equals(getDeselectAllUpperValue()) ||
               chkFilterNulls.isSelected();
    }

    protected String getDeselectAllLowerValue() {
        return "1.0";
    }

    protected String getDeselectAllUpperValue() {
        return "0.0";
    }

    protected int getTextFieldLength() {
        return 4;
    }

    protected Font bigger(Font font) {
        return font.deriveFont(font.getSize() + 2.0F).deriveFont(Font.BOLD);
    }

    ////////////////
    // Popup Menu //
    ////////////////

    @Override
    protected void configureMenu() {
        menuConfig.setMenuItemVisible(MenuConfiguration.MNU_GRP_VIS,
            "Shape", false);
    }

    ///////////////////
    // Text Listener //
    ///////////////////

    protected class TextFieldChangedListener implements DocumentListener {
        protected JTextField txt;

        public TextFieldChangedListener(JTextField t) {
            txt = t;
        }

        // Not applicable for JTextField's.
        public void changedUpdate(DocumentEvent e) {}

        public void insertUpdate(DocumentEvent e) {
            changed();
        }

        public void removeUpdate(DocumentEvent e) {
            changed();
        }

        protected void changed() {
            setHighlighted(true);
            txt.setBackground(UiDefaults.CONT_EDITED_COLOR);
        }
    }
}
