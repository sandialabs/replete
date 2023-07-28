package replete.ui.sdplus.panels;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.GuiUtil;
import replete.ui.images.concepts.ImageLib;
import replete.ui.panels.GradientPanel;
import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.UiDefaults;
import replete.ui.sdplus.color.ColorDialog;
import replete.ui.sdplus.color.ColorDialog.ColorDialogResult;
import replete.ui.sdplus.color.ColorMap;
import replete.ui.sdplus.events.ColorsChangedEvent;
import replete.ui.sdplus.events.ColorsChangedListener;
import replete.ui.sdplus.events.ListedChangedEvent;
import replete.ui.sdplus.events.ListedChangedListener;
import replete.ui.sdplus.events.PopupMenuClickedEvent;
import replete.ui.sdplus.events.PopupMenuClickedListener;
import replete.ui.sdplus.events.ScaleSetChangedListener;
import replete.ui.sdplus.events.ValueChangedEvent;
import replete.ui.sdplus.events.VisTypeChangedEvent;
import replete.ui.sdplus.events.VisTypeChangedListener;
import replete.ui.sdplus.images.SdPlusImageModel;
import replete.ui.sdplus.menu.MenuConfiguration;
import replete.util.OsUtil;


/**
 * Base class for all the scale panels.  This panel is
 * a gradient panel.  At a future point, one could make
 * just the title panel a gradient panel, and make the
 * filter panel one flat color -- that might look appealing
 * as well.  This class contains individual UI settings
 * that can be changed separately from all other scale
 * panels and separately from the scale set panel.  For the
 * most part the title panel is handled by this base class,
 * and it's the filter panel that will vary greatly from
 * subclass to subclass.
 *
 * @author Derek Trumbo
 */

public class ScalePanel extends GradientPanel {

    ////////////
    // Fields //
    ////////////

    public static final String NO_VALUE_TEXT = "<no value>";
    public static final String NO_VALUES_TEXT = "<no values>";

    // Parent reference for events - can be null if the scale panel is
    // not contained within a ScaleSetPanel (autonomous).
    protected ScaleSetPanel parent;

    // Model
    protected ScalePanelModel model;

    // UI
    protected JPanel pnlNotes;
    protected JPanel pnlFilter;
    protected JLabel lblIcon;
    protected JLabel lblTitle;
    protected JLabel lblNotes;
    protected JPanel pnlTitle;
    protected JLabel lblTitleCounts;
    protected MenuConfiguration menuConfig = new MenuConfiguration();

    // UI Settings
    protected Font titleFont = UiDefaults.SP_TITLE_FONT;
    protected Font titleCountsFont = UiDefaults.SP_TITLE_COUNTS_FONT;
    protected Font notesFont = UiDefaults.SP_NOTES_FONT;
    protected Font filterFont = UiDefaults.SP_FILTER_FONT;
    protected Color backgroundColor = UiDefaults.SP_BACKGROUND_COLOR;
    protected Color borderColor = UiDefaults.SP_BORDER_COLOR;
    protected Color subselectedBorderColor = UiDefaults.SP_SUBSELECTED_BORDER_COLOR;
    protected Color highlightColor = UiDefaults.SP_HIGHLIGHT_COLOR;
    protected int titleMargin = UiDefaults.SP_TITLE_MARGIN;
    protected boolean showTitleCounts = UiDefaults.SP_SHOW_TITLE_COUNTS;
    protected ImageIcon icon = UiDefaults.SP_BASE_ICON;
    protected boolean popupSectionLabels = UiDefaults.SP_POPUP_SECTION_LABELS;
    protected int outerMargin = UiDefaults.SP_OUTER_MARGIN;
    protected int innerSpacing = UiDefaults.SP_INNER_SPACING;
    protected List<ScaleSetChangedListener> valueListeners = new ArrayList<ScaleSetChangedListener>();
    protected List<VisTypeChangedListener> visTypeListeners = new ArrayList<VisTypeChangedListener>();
    protected List<ColorsChangedListener> colorListeners = new ArrayList<ColorsChangedListener>();
    protected List<ListedChangedListener> listedListeners = new ArrayList<ListedChangedListener>();
    protected List<PopupMenuClickedListener> popupListeners = new ArrayList<PopupMenuClickedListener>();

    // Other
    protected boolean highlighted;
    protected boolean subselected;
    protected boolean topScalePanel = true;
    protected boolean bottomScalePanel = true;
    protected ChangeNotifier highlightedNotifier = new ChangeNotifier(this);
    protected ChangeNotifier subselectedChangedNotifier = new ChangeNotifier(this);
    protected boolean defaultStateAllSelected = true;
        // True if parent's MatchType is INTERSECTION,
        // False if parent's MatchType is UNION or
        // can be toggled independently for autonomous scale panels.

    //////////////////
    // Constructors //
    //////////////////

    public ScalePanel(ScalePanelModel m) {
        this(null, m);
    }
    public ScalePanel(ScaleSetPanel s, ScalePanelModel m) {
        parent = s;  // Can be null if ScalePanel is autonomous.
        model = m;

        // Catches everything except the title panel which has its own listener.
        addMouseListener(new FilterPanelMouseListener());

        initBeforeBuild();
        build();
        rebuildDefaultPopupMenu();
    }

    ///////////////
    // Pre-Build //
    ///////////////

    protected void initBeforeBuild() {
        icon = getTitleIcon();
    }

    protected ImageIcon getTitleIcon() {
        return UiDefaults.SP_BASE_ICON;
    }

    ///////////
    // Build //
    ///////////

    public void build() {
        setColors(getBackground(), getBackground().darker());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        buildTitleLabel();
        buildTitlePanel();

        // Things for all title panels.
        pnlTitle.setBorder(BorderFactory.createEmptyBorder(titleMargin,
            titleMargin, titleMargin, titleMargin));
        pnlTitle.addMouseListener(new TitlePanelMouseListener());

        add(pnlTitle);

        pnlNotes = buildNotesPanel();
        if(pnlNotes != null) {
            add(pnlNotes);
        }

        pnlFilter = buildFilterPanel();
        if(pnlFilter != null) {
            add(pnlFilter);
        }

        setOpen(model.isOpen());

        updateScaleIsSubselected();
    }

    protected void buildTitlePanel() {
        lblIcon = new JLabel(icon);

        lblTitleCounts = new JLabel(generateTitleCountText());

        pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTitle.setAlignmentX(LEFT_ALIGNMENT);
        pnlTitle.setOpaque(false);

        pnlTitle.add(lblIcon);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblTitleCounts);
    }

    public void buildTitleLabel() {
        lblTitle = new JLabel(generateTitleText());
        lblTitle.setOpaque(false);
        lblTitle.setFont(titleFont);
    }

    public JPanel buildNotesPanel() {
        lblNotes = new JLabel(generateNotesText());
        lblNotes.setOpaque(false);
        lblNotes.setFont(notesFont);

        JPanel pnlNotes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPanel pnlNotes2 = GuiUtil.addBorderedComponent(pnlNotes, lblNotes,
            BorderFactory.createEmptyBorder(0, (titleMargin - 1), 0, 0));
        pnlNotes2.setOpaque(false);
        pnlNotes.setAlignmentX(LEFT_ALIGNMENT);
        pnlNotes.setOpaque(false);
        return pnlNotes;
    }

    protected JPanel buildFilterPanel() {
        return null;
    }

    protected String generateTitleCountText() {
        return "";
    }

    protected String generateTitleText() {
        String additional = (model.getUnits() == null ? "" : "in " + model.getUnits() + " ");
        return " " + model.getName() + " " + additional;
    }

    protected String generateNotesText() {
        String notesStr = "";
        String someNote = model.getNote();
        if(someNote != null && !someNote.equals("")) {
            notesStr = "<html>" + someNote + "</html>";
        }
        return notesStr;
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Accessors

    // Parent

    public ScaleSetPanel getScaleSetPanel() {
        return parent;
    }

    // Model

    public ScalePanelModel getScalePanelModel() {
        return model;
    }

    // UI Settings

    public Font getTitleFont() {
        return titleFont;
    }
    public Font getTitleCountsFont() {
        return titleCountsFont;
    }
    public Font getNotesFont() {
        return notesFont;
    }
    public Font getFilterFont() {
        return filterFont;
    }
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    public Color getBorderColor() {
        return borderColor;
    }
    public Color getSubselectedBorderColor() {
        return subselectedBorderColor;
    }
    public Color getHighlightColor() {
        return highlightColor;
    }
    public int getTitleMargin() {
        return titleMargin;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public boolean isShowTitleCounts() {
        return showTitleCounts;
    }
    public boolean isPopupSectionLabels() {
        return popupSectionLabels;
    }
    public int getOuterMargin() {
        return outerMargin;
    }
    public int getInnerSpacing() {
        return innerSpacing;
    }

    // Other

    public boolean isHighlighted() {
        return highlighted;
    }
    public boolean isSubselected() {
        return subselected;
    }
    public boolean isTopScalePanel() {
        return topScalePanel;
    }
    public boolean isBottomScalePanel() {
        return bottomScalePanel;
    }
    public boolean isDefaultStateAllSelected() {
        return defaultStateAllSelected;
    }
    public String getTitle() {
        if(lblTitle == null) {
            return null;
        }
        return lblTitle.getText().trim();
    }

    // Mutators

    // UI Settings

    public void setTitleFont(Font font) {
        titleFont = font;
        if(lblTitle != null) {
            lblTitle.setFont(font);
        }
    }
    public void setTitleCountsFont(Font font) {
        titleCountsFont = font;
        if(lblTitleCounts != null) {
            lblTitleCounts.setFont(font);
        }
    }
    public void setNotesFont(Font font) {
        notesFont = font;
        if(lblNotes != null) {
            lblNotes.setFont(font);
        }
    }
    public void setFilterFont(Font font) {
        filterFont = font;
        // Nothing to be done here for base class, which does
        // not have a filter panel.
    }
    public void setBackgroundColor(Color color) {
        backgroundColor = color;
        updateBackground();
    }
    public void setBorderColor(Color color) {
        borderColor = color;
        updateBorder();
    }
    public void setSubselectedBorderColor(Color color) {
        subselectedBorderColor = color;
        updateBorder();
    }
    public void setHighlightColor(Color color) {
        highlightColor = color;
        updateHighlight();
    }
    public void setTitleMargin(int margin) {
        titleMargin = margin;
        pnlTitle.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
    }
    public void setShowTitleCounts(boolean show) {
        showTitleCounts = show;
        if(lblTitleCounts != null) {   // Base ScalePanel class doesn't use this label.
            lblTitleCounts.setText(generateTitleCountText());
        }
    }
    public void setIcon(ImageIcon i) {
        icon = i;
        lblIcon.setIcon(i);
    }
    public void setPopupSectionLabels(boolean labels) {
        popupSectionLabels = labels;
        menuConfig.setGroupNameLabelsVisible(popupSectionLabels);
    }
    public void setOuterMargin(int margin) {
        outerMargin = margin;
    }
    public void setInnerSpacing(int spacing) {
        innerSpacing = spacing;
    }

    // Other

    public void setHighlighted(boolean highlt) {
        highlighted = highlt;
        updateHighlight();
        if(highlighted) {
            highlightedNotifier.fireStateChanged();
        }
    }
    public void setTopScalePanel(boolean top) {
        topScalePanel = top;
        updateBorder();
    }
    public void setBottomScalePanel(boolean bot) {
        bottomScalePanel = bot;
        updateBorder();
    }
    public void setDefaultStateAllSelected(boolean defaultStateAllSel) {
        // True if parent's MatchType is INTERSECTION,
        // False if parent's MatchType is UNION or
        // can be toggled independently for autonomous scale panels.
        defaultStateAllSelected = defaultStateAllSel;
        updateScaleIsSubselected();
    }

    ////////////
    // Update //
    ////////////

    protected void updateBackground() {
        if(backgroundColor == null) {
            setColors(getBackground(), getBackground().darker());
        } else {
            setColors(backgroundColor, backgroundColor.darker());
        }
    }

    // Updates the scale panel's border based on:
    // - Whether or not the panel is the top or bottom panel
    // - Whether or not the panel is subselected
    // - Border and subselected border colors
    // This should be called any time one of the above is changed.
    protected void updateBorder() {
        int top = (topScalePanel ? 2 : 1);
        int bottom = (bottomScalePanel ? 2 : 1);
        int sides = 2;
        Border outer, inner;
        if(subselected) {
            outer = BorderFactory.createMatteBorder(top, sides, bottom, sides, subselectedBorderColor);
            inner = BorderFactory.createMatteBorder(1, 0, 1, 0, subselectedBorderColor);
        } else {
            outer = BorderFactory.createMatteBorder(top, sides, bottom, sides, borderColor);
            inner = BorderFactory.createEmptyBorder(1, 0, 1, 0);
        }
        Border composite = BorderFactory.createCompoundBorder(outer, inner);
        setBorder(composite);
    }

    // Updates the scale panel's highlighted color based on:
    // - Whether or not the panel is highlighted
    // - Highlighted color
    // This should be called any time one of the above is changed.
    protected void updateHighlight() {
        if(highlighted) {
            setColors(highlightColor, highlightColor.darker());
        } else {
            updateBackground();  // Sets the regular, non-highlight BG.
        }
    }

    protected void updateVisualizationType() {
        updateColor();
        //updateShape();
    }

    // Updates the scale panel's subselected state based on:
    // - Whether or not the panel is listed in the table (handled by base class)
    // - Whether or not the data on the filter panels indicate
    //   that the user has changed something (handled by the individual derived
    //   classes), which itself depends on whether or not intersection or
    //   union is selected.
    // This should be called any time one of the above is changed.
    protected void updateScaleIsSubselected() {
        boolean prevSubselected = subselected;
        subselected = determineIsSubselected();
        updateBorder();
        if(subselected != prevSubselected) {
            subselectedChangedNotifier.fireStateChanged();
        }
    }

    // Overriden by subclasses to determine whether the user has subselected
    // the scale panel.  The super method should be checked before running
    // scale panel-specific implementations.
    protected boolean determineIsSubselected() {
        return !model.isListedInTable();
    }

    public void updateModelFromUI() {
        updateScaleIsSubselected();
    }

    public void updateUIFromModel() {
        updateScaleIsSubselected();
        lblTitle.setText(generateTitleText());
        lblNotes.setText(generateNotesText());
        setOpen(model.isOpen());
    }

    protected void updateColor() {

    }

    ////////////
    // Events //
    ////////////

    // Value Changed

    public ScaleSetChangedListener[] getScalePanelChangedListeners() {
        return valueListeners.toArray(new ScaleSetChangedListener[0]);
    }
    public void addScalePanelChangedListener(ScaleSetChangedListener listener) {
        valueListeners.add(listener);
    }
    public void removeScalePanelChangedListener(ScaleSetChangedListener listener) {
        valueListeners.remove(listener);
    }
    protected void fireValueChangedEvent(ValueChangedEvent e) {
        for(ScaleSetChangedListener listener : valueListeners) {
            listener.valueChanged(e);
        }
    }

    // Visualization Type

    public VisTypeChangedListener[] getVisTypeChangedListeners() {
        return visTypeListeners.toArray(new VisTypeChangedListener[0]);
    }
    public void addVisTypeChangedListener(VisTypeChangedListener listener) {
        visTypeListeners.add(listener);
    }
    public void removeVisTypeChangedListener(VisTypeChangedListener listener) {
        visTypeListeners.remove(listener);
    }
    protected void fireVisTypeChangedEvent(VisTypeChangedEvent e) {
        for(VisTypeChangedListener listener : visTypeListeners) {
            listener.valueChanged(e);
        }
    }

    // Colors

    public ColorsChangedListener[] getColorsChangedListeners() {
        return colorListeners.toArray(new ColorsChangedListener[0]);
    }
    public void addColorsChangedListener(ColorsChangedListener listener) {
        colorListeners.add(listener);
    }
    public void removeColorsChangedListener(ColorsChangedListener listener) {
        colorListeners.remove(listener);
    }
    protected void fireColorsChangedEvent(ColorsChangedEvent e) {
        for(ColorsChangedListener listener : colorListeners) {
            listener.valueChanged(e);
        }
    }

    // Listed

    public ListedChangedListener[] getListedChangedListeners() {
        return listedListeners.toArray(new ListedChangedListener[0]);
    }
    public void addListedChangedListener(ListedChangedListener listener) {
        listedListeners.add(listener);
    }
    public void removeListedChangedListener(ListedChangedListener listener) {
        listedListeners.remove(listener);
    }
    protected void fireListedChangedEvent(ListedChangedEvent e) {
        for(ListedChangedListener listener : listedListeners) {
            listener.valueChanged(e);
        }
    }

    // Popup Menu

    public PopupMenuClickedListener[] getPopupMenuClickedListeners() {
        return popupListeners.toArray(new PopupMenuClickedListener[0]);
    }
    public void addPopupMenuClickedListener(PopupMenuClickedListener listener) {
        popupListeners.add(listener);
    }
    public void removePopupMenuClickedListener(PopupMenuClickedListener listener) {
        popupListeners.remove(listener);
    }
    protected void firePopupMenuClickedEvent(PopupMenuClickedEvent e) {
        for(PopupMenuClickedListener listener : popupListeners) {
            listener.valueChanged(e);
        }
    }

    //////////
    // Misc //
    //////////

    // This is more of a model-change and update from model
    // in one.
    public void setOpen(boolean open) {
        model.setOpen(open);
        if(pnlNotes != null) {
            pnlNotes.setVisible(open);
        }
        if(pnlFilter != null) {
            pnlFilter.setVisible(open);
        }
    }

    // This acts as a pass-through to the model.  It exists here
    // so that the panel can also be updated visually.
    public void setVisualizationType(VisualizationType type) {
        model.setVisualizationType(type);
        updateVisualizationType();
    }
    // This acts as a pass-through to the model.  It exists here
    // so that the panel can also be updated visually.
    public void setOverrideColorMap(ColorMap colorMap) {
        model.setOverrideColorMap(colorMap);
        updateColor();
    }

    public void addHighlightedListener(ChangeListener listener) {
        highlightedNotifier.addListener(listener);
    }
    public void addSubselectedListener(ChangeListener listener) {
        subselectedChangedNotifier.addListener(listener);
    }

    public void selectAll(boolean suppressEvents) {}
    public void deselectAll(boolean suppressEvents) {}

    ////////////////
    // Popup Menu //
    ////////////////

    public JPopupMenu getPopUpMenu() {
        return menuConfig.getPopupMenu();
    }

    public MenuConfiguration getMenuConfiguration() {
        return menuConfig;
    }

    protected void rebuildDefaultPopupMenu() {

        menuConfig.clear();

        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_SEL, "Select All",
            ImageLib.get(SdPlusImageModel.SEL_ON), false, e -> {
                selectAll(false);
                sendPopupEvent(e);
            }
        );
        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_SEL, "Deselect All",
            ImageLib.get(SdPlusImageModel.SEL_OFF), false, e -> {
                deselectAll(false);
                sendPopupEvent(e);
            }
        );

        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_VIS, "Mark On X",
            ImageLib.get(SdPlusImageModel.VIZ_MARK_X), true, e -> {
                setVisualizationTypeInternal(VisualizationType.MARK_ON_X);
                sendPopupEvent(e);
            }
        );
        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_VIS, "Mark On Y",
            ImageLib.get(SdPlusImageModel.VIZ_MARK_Y), true, e -> {
                setVisualizationTypeInternal(VisualizationType.MARK_ON_Y);
                sendPopupEvent(e);
            }
        );
        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_VIS, "X Axis",
            ImageLib.get(SdPlusImageModel.VIZ_X), false, e -> {
                setVisualizationTypeInternal(VisualizationType.X_AXIS);
                sendPopupEvent(e);
            }
        );
        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_VIS, "Y Axis",
            ImageLib.get(SdPlusImageModel.VIZ_Y), false, e -> {
                setVisualizationTypeInternal(VisualizationType.Y_AXIS);
                sendPopupEvent(e);
            }
        );
        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_VIS, "Color",
            ImageLib.get(SdPlusImageModel.VIZ_COLOR), false, e -> {
                setVisualizationTypeInternal(VisualizationType.COLOR);
                sendPopupEvent(e);
            }
        );
        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_VIS, "Shape",
            ImageLib.get(SdPlusImageModel.VIZ_SHAPE), false, e -> {
                setVisualizationTypeInternal(VisualizationType.SHAPE);
                sendPopupEvent(e);
            }
        );
        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_TBL, "Hide Column",
            ImageLib.get(SdPlusImageModel.TBL_HIDE), false, e -> {
                if(model.isListedInTable()) {
                    JMenuItem mnu = menuConfig.getMenuItem(
                        MenuConfiguration.MNU_GRP_TBL, "Hide Column");
                    mnu.setText("Show Column");
                } else {
                    JMenuItem mnu = menuConfig.getMenuItem(
                        MenuConfiguration.MNU_GRP_TBL, "Hide Column");
                    mnu.setText("Hide Column");
                }
                model.setListedInTable(!model.isListedInTable());
                updateScaleIsSubselected();
                ListedChangedEvent ev =
                    new ListedChangedEvent(parent, model.getKey(), ScalePanel.this, model, model.isListedInTable());
                fireListedChangedEvent(ev);
                sendPopupEvent(e);
            }
        );
        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_TBL, "Go To Column",
            ImageLib.get(SdPlusImageModel.TBL_GOTO), false, e -> sendPopupEvent(e)
        );

        menuConfig.definePopupMenuItem(
            MenuConfiguration.MNU_GRP_OPT, "Choose Colors",
            ImageLib.get(SdPlusImageModel.COLORS), false, e -> {
                showChooseColors();
                sendPopupEvent(e);
            }
        );

        menuConfig.buildPopupMenu(popupSectionLabels);

        configureMenu();
    }

    // Allows subclasses to always configure certain menus in a
    // consistent manner each time the menu is rebuilt.
    protected void configureMenu() {}

    protected void setVisualizationTypeInternal(VisualizationType type) {
        setVisualizationType(type);
        VisTypeChangedEvent ev =
            new VisTypeChangedEvent(parent, model.getKey(), this, model, type);
        fireVisTypeChangedEvent(ev);
    }

    public void setOverrideColorMapInternal(boolean restore, ColorMap colorMap) {
        setOverrideColorMap(restore ? null : colorMap);
        ColorsChangedEvent ev =
            new ColorsChangedEvent(parent, model.getKey(), this, model, colorMap);
        fireColorsChangedEvent(ev);
    }

    protected void sendPopupEvent(ActionEvent e) {
        JMenuItem mnu = (JMenuItem) e.getSource();
        PopupMenuClickedEvent ev =
            new PopupMenuClickedEvent(parent, model.getKey(), this, model, mnu.getText());
        firePopupMenuClickedEvent(ev);
    }

    protected void showChooseColors() {

        ColorMap map = model.buildActiveColorMap();
        String extra;
        String type;

        if(this instanceof EnumScaleBasePanel) {
            type = "Enumerated Scale: ";
            extra = "each category of this enumerated scale.";
        } else {
            type = "Continuous Scale: ";
            extra = "the extremes of a color gradient for this continuous scale.";
        }

        ColorDialog dlg = new ColorDialog((JFrame) SwingUtilities.getRoot(this),
            "Choose Colors: Override Settings: " + type + model.getKey(),
            "Use the color picker below to assign a color to " + extra,
            "Removes any override colors, effectively reverting the color settings to either the local or project settings depending on the Wave preferences.",
            map, false);

        dlg.setVisible(true);

        if(dlg.getResult() != ColorDialogResult.CANCELED) {
            boolean restore = (dlg.getResult() == ColorDialogResult.RESTORE);
            setOverrideColorMapInternal(restore, dlg.getColorMap());
        }
    }

    /////////////////////
    // Mouse Listeners //
    /////////////////////

    // Handles right-click menu, highlighting and expanding/collapsing for the
    // title panel.
    protected class TitlePanelMouseListener extends MouseAdapter {

        // There is OS-specific code in this inner class because
        // it is convention on Windows to open a popup menu on
        // MOUSE DOWN of the right-click button and it's convention
        // on Linux and Mac to open a popup menu on MOUSE UP of the
        // right-click button.  This is enforced by Swing because
        // the isPopupTrigger's return value will vary based on
        // mousePressed/mouseReleased and the operating system.

        // The code in this class was carefully crafted for a
        // specific user experience.

        @Override
        public void mouseReleased(MouseEvent e) {

            // If the right-click button was clicked (this will
            // happen on Windows) on mouse up, then pop open the
            // context menu.
            if(e.isPopupTrigger()) {
                JPopupMenu mnuPopup = getPopUpMenu();
                if(mnuPopup != null) {
                    mnuPopup.show(ScalePanel.this, e.getX(), e.getY());
                }

            // Else if the regular click button was clicked and we're
            // in Windows, toggle the open-state.
            } else if(OsUtil.isWindows()) {

                // This sequence of events attempts to open or close
                // the scale panel before setting the panel as the
                // highlighted panel in the hopes that when the scroll
                // pane scrolls to the panel, it will do so on an
                // already expanded or collapsed panel.  However,
                // the real solution was to add code in ScaleSetPanel's
                // saveHighlightedNode method (see this method for
                // more documentation).

                setOpen(!model.isOpen());
                updateUI();
                setHighlighted(true);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

            // The method setHighlighted is called in all cases in
            // mousePressed to change the color as early as possible,
            // but because setHighlighted also causes the re-centering
            // of the panel in the scroll pane, setHighlighted is also
            // called in mouseReleased after the panel is opened or
            // closed.

            // If the right-click button was clicked (this will happen
            // on Linux or Mac) on mouse down, then pop open the
            // context menu.
            if(e.isPopupTrigger()) {
                setHighlighted(true);
                JPopupMenu mnuPopup = getPopUpMenu();
                if(mnuPopup != null) {
                    mnuPopup.show(ScalePanel.this, e.getX(), e.getY());
                }

            // Else if the regular click button was clicked and we're
            // not in Windows, toggle the open-state.
            } else if(!OsUtil.isWindows()) {

                // This sequence of events attempts to open or close
                // the scale panel before setting the panel as the
                // highlighted panel in the hopes that when the scroll
                // pane scrolls to the panel, it will do so on an
                // already expanded or collapsed panel.  However,
                // the real solution was to add code in ScaleSetPanel's
                // saveHighlightedNode method (see this method for
                // more documentation).

                setOpen(!model.isOpen());
                updateUI();
                setHighlighted(true);

            } else {
                setHighlighted(true);
            }
        }
    }

    // Handles right-click and highlighting on the whole scale panel except
    // for the title panel, which has its own listener.
    protected class FilterPanelMouseListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {

            // If the right-click button was clicked (this will happen
            // on Windows) on mouse up, then pop open the
            // context menu.
            if(e.isPopupTrigger()) {
                JPopupMenu mnuPopup = getPopUpMenu();
                if(mnuPopup != null) {
                    mnuPopup.show(ScalePanel.this, e.getX(), e.getY());
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            setHighlighted(true);

            // If the right-click button was clicked (this will happen
            // on Linux or Mac) on mouse down, then pop open the
            // context menu.
            if(e.isPopupTrigger()) {
                JPopupMenu mnuPopup = getPopUpMenu();
                if(mnuPopup != null) {
                    mnuPopup.show(ScalePanel.this, e.getX(), e.getY());
                }
            }
        }
    }
}
