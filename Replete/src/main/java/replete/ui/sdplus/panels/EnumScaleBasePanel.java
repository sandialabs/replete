package replete.ui.sdplus.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.ui.GuiUtil;
import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.UiDefaults;
import replete.ui.sdplus.events.EnumScalePanelIndvChangedEvent;


/**
 * Displays unique values and the ability to turn on and
 * off each value for an enumerated scale.  Null values
 * receive their own check box or radio button but display
 * "<no value>".
 *
 * @author Derek Trumbo
 */

public abstract class EnumScaleBasePanel extends ScalePanel {

    ////////////
    // Fields //
    ////////////

    // Model
    protected EnumScaleBasePanelModel emodel;

    // UI
    protected List<JLabel> countLabels;
    protected JPanel pnlFilterInner;
    protected JPanel pnlOuterMargin;
    protected List<JPanel> innerSpacerPanels;   // Set by subclasses, but known to base class.

    // UI Settings
    protected boolean showValueCounts = UiDefaults.ENUM_SHOW_VALUE_COUNTS;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public EnumScaleBasePanel(ScaleSetPanel p, EnumScaleBasePanelModel m) {
        super(p, m);

        // This section sets those UI settings that are controlled by
        // instance variables of this class but are referenced when
        // the super class constructor builds the panel by calling
        // methods in this class.  At that point they are not yet
        // initialized so these calls are required to actually apply
        // the default values.  Especially important with autonomous
        // panels since the ScaleSetPanel will set all UI settings
        // after the panels are built.
        setShowValueCounts(showValueCounts);
    }

    ///////////////
    // Pre-Build //
    ///////////////

    @Override
    protected void initBeforeBuild() {
        super.initBeforeBuild();
        emodel = (EnumScaleBasePanelModel) model;
    }

    @Override
    protected ImageIcon getTitleIcon() {
        return UiDefaults.ENUM_ICON;
    }

    ///////////
    // Build //
    ///////////

    @Override
    protected String generateTitleCountText() {
        if(!showTitleCounts) {
            return "";
        }

        int nonNullTotal = 0;
        int nonNullUnique = 0;
        for(Entry<Object, Integer> entry : emodel.getUniqueValueCounts().entrySet()) {
            if(entry.getKey() != null) {
                nonNullTotal = nonNullTotal + entry.getValue();
                nonNullUnique += 1;
            }
        }
        return "(" + nonNullUnique + " unique, " + nonNullTotal + " total)";
    }

    @Override
    protected JPanel buildFilterPanel() {

        pnlFilterInner = new JPanel();
        pnlFilterInner.setLayout(new BoxLayout(pnlFilterInner, BoxLayout.Y_AXIS));
        pnlFilterInner.setOpaque(false);

        buildInnerFilterPanel();

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

    protected abstract void buildInnerFilterPanel();

    // Fire individual event.
    protected void fireIndvChangedEvent(Object value, boolean newState) {
        EnumScalePanelIndvChangedEvent e =
            new EnumScalePanelIndvChangedEvent(parent, model.getKey(), this,
                model, value, newState);
        fireValueChangedEvent(e);
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Accessors

    public boolean isShowValueCounts() {
        return showValueCounts;
    }

    // Mutators

    @Override
    public void setFilterFont(Font font) {
        super.setFilterFont(font);
        for(JLabel lbl : countLabels) {
            lbl.setFont(font);
        }
    }
    public void setShowValueCounts(boolean sv) {
        showValueCounts = sv;
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
        for(JPanel pnlSpacer : innerSpacerPanels) {
            pnlSpacer.setBorder(BorderFactory.createEmptyBorder(
                innerSpacing, 0, 0, 0));
        }
    }

    ////////////
    // Update //
    ////////////

    @Override
    public void updateUIFromModel() {
        super.updateUIFromModel();
        buildInnerFilterPanel();
        updateScaleIsSubselected();  // Duplicate call since it's based off of UI.
    }

    @Override
    public void updateColor() {
        updateInternalColor(emodel.getVisualizationType() == VisualizationType.COLOR);
    }

    protected abstract void updateInternalColor(boolean isVisTypeColor);
}
