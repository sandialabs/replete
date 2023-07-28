package replete.ui.sdplus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.GuiUtil;
import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.sdplus.events.ColorsChangedListener;
import replete.ui.sdplus.events.ListedChangedListener;
import replete.ui.sdplus.events.PopupMenuClickedListener;
import replete.ui.sdplus.events.ScaleSetChangedCoalescedEvent;
import replete.ui.sdplus.events.ScaleSetChangedListener;
import replete.ui.sdplus.events.VisTypeChangedListener;
import replete.ui.sdplus.images.SdPlusImageModel;
import replete.ui.sdplus.panels.ContScalePanel;
import replete.ui.sdplus.panels.ContScalePanelModel;
import replete.ui.sdplus.panels.DateScalePanel;
import replete.ui.sdplus.panels.DateScalePanelModel;
import replete.ui.sdplus.panels.EnumScaleBasePanel;
import replete.ui.sdplus.panels.EnumScaleMultiPanel;
import replete.ui.sdplus.panels.EnumScaleMultiPanelModel;
import replete.ui.sdplus.panels.EnumScaleSinglePanel;
import replete.ui.sdplus.panels.EnumScaleSinglePanelModel;
import replete.ui.sdplus.panels.GroupPanel;
import replete.ui.sdplus.panels.GroupPanelModel;
import replete.ui.sdplus.panels.LongScalePanel;
import replete.ui.sdplus.panels.LongScalePanelModel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;
import replete.ui.sdplus.sort.AlphabeticalComparator;
import replete.ui.sdplus.sort.CategoryComparator;
import replete.ui.sdplus.sort.ScalePanelComparator;
import replete.ui.sdplus.sort.SubselectedComparator;


/**
 * Provides a container for a list of scale panels which show both
 * the range of values of some data source and a way to filter
 * which values are considered selected.  This panel provides an
 * options area to change the match type and sort applied to the
 * scale panels.  The scale set panel is backed by a model which
 * describes how the scale panels should be built and it maintains
 * a separate (i.e. default) set of UI settings that are applied to
 * the scale panels each time they are constructed.
 *
 * @author Derek Trumbo
 */

public class ScaleSetPanel extends JPanel {

    ////////////
    // Fields //
    ////////////

    // Static map which indicates which type of scale panel
    // should be constructed for a given type of scale panel
    // model provided by the scale set panel model.

    protected static
        Map<Class<? extends ScalePanelModel>,
            Class<? extends ScalePanel>> panelModelMap =
                new HashMap<Class<? extends ScalePanelModel>,
                    Class<? extends ScalePanel>>();

    static {
        panelModelMap.put(ScalePanelModel.class, ScalePanel.class);
        panelModelMap.put(EnumScaleMultiPanelModel.class, EnumScaleMultiPanel.class);
        panelModelMap.put(EnumScaleSinglePanelModel.class, EnumScaleSinglePanel.class);
        panelModelMap.put(ContScalePanelModel.class, ContScalePanel.class);
        panelModelMap.put(LongScalePanelModel.class, LongScalePanel.class);
        panelModelMap.put(DateScalePanelModel.class, DateScalePanel.class);
    }

    // The current model that is backing the scale set panel.
    protected ScaleSetPanelModel model;

    // List of most recently-built scale panels in the set.
    protected List<ScalePanel> scalePanels;

    // List of currently displayed group panels, if any are being shown.
    protected List<GroupPanel> groupPanels;

    // UI

    protected JPanel pnlScales;
    protected JPanel pnlOptionsAll;
    protected JRadioButton optInt;
    protected JRadioButton optUnion;
    protected JPanel pnlMatchType;
    protected JPanel pnlSortType;
    protected JButton btnActiveButton;
    protected JButton btnSelectAll;
    protected JButton btnDeselectAll;
    protected JCheckBox chkUseGroups;
    protected JPanel pnlOptionsCollapsible;
    protected RButton btnShowOptions;

    // UI Settings

    // ScaleSetPanel only
    protected Font searchFont = UiDefaults.SSP_SEARCH_FONT;
    protected boolean coalesceEvents = UiDefaults.SSP_COALESCE_EVENTS;
    protected Color scaleAreaColor = UiDefaults.SSP_SCALE_AREA_COLOR;

    // Most ScalePanel's
    protected Font titleFont = UiDefaults.SP_TITLE_FONT;
    protected Font titleCountsFont = UiDefaults.SP_TITLE_COUNTS_FONT;
    protected Font notesFont = UiDefaults.SP_NOTES_FONT;
    protected Font filterFont = UiDefaults.SP_FILTER_FONT;
    protected Color backgroundColor = UiDefaults.SP_BACKGROUND_COLOR;
    protected Color borderColor = UiDefaults.SP_BORDER_COLOR;
    protected Color subselectedBorderColor = UiDefaults.SP_SUBSELECTED_BORDER_COLOR;
    protected Color highlightColor = UiDefaults.SP_HIGHLIGHT_COLOR;
    protected int titleMargin = UiDefaults.SP_TITLE_MARGIN;
    protected ImageIcon baseIcon = UiDefaults.SP_BASE_ICON;
    protected boolean showTitleCounts = UiDefaults.SP_SHOW_TITLE_COUNTS;
    protected boolean popupSectionLabels = UiDefaults.SP_POPUP_SECTION_LABELS;
    protected int outerMargin = UiDefaults.SP_OUTER_MARGIN;
    protected int innerSpacing = UiDefaults.SP_INNER_SPACING;

    // EnumScale*Panel's
    protected ImageIcon enumIcon = UiDefaults.ENUM_ICON;
    protected boolean enumCoalesceEvents = UiDefaults.ENUM_COALESCE_EVENTS;
    protected boolean enumShowValueCounts = UiDefaults.ENUM_SHOW_VALUE_COUNTS;

    // ContScalePanel's
    protected ImageIcon contIcon = UiDefaults.CONT_ICON;
    // Cont edited color and error color not configurable yet.

    // LongScalePanel's
    protected ImageIcon longIcon = UiDefaults.LONG_ICON;

    // DateScalePanel's
    protected ImageIcon dateIcon = UiDefaults.DATE_ICON;

    // GroupPanel's
    protected Font groupTitleFont = UiDefaults.GRP_TITLE_FONT;
    protected Font groupNotesFont = UiDefaults.GRP_NOTES_FONT;
    protected int groupTitleMargin = UiDefaults.GRP_TITLE_MARGIN;
    protected int groupIndent = UiDefaults.GRP_INDENT;
    protected ImageIcon groupOpenIcon = UiDefaults.GRP_OPEN_ICON;
    protected ImageIcon groupClosedIcon = UiDefaults.GRP_CLOSED_ICON;
    protected Color groupBackground = UiDefaults.GRP_BACKGROUND_COLOR;

    // Listeners

    protected List<ScaleSetChangedListener> valueListeners = new ArrayList<ScaleSetChangedListener>();
    protected List<VisTypeChangedListener> visTypeListeners = new ArrayList<VisTypeChangedListener>();
    protected List<ColorsChangedListener> colorsListeners = new ArrayList<ColorsChangedListener>();
    protected List<ListedChangedListener> listedListeners = new ArrayList<ListedChangedListener>();
    protected List<PopupMenuClickedListener> popupListeners = new ArrayList<PopupMenuClickedListener>();

    // Search

    protected JPanel pnlSearch;
    protected JTextField txtSearch;
    protected JLabel lblSearch;
    protected String lastSearch;
    protected List<ScalePanel> searchResults;
    protected int searchPos = -1;

    // Other

    protected MatchType matchType = MatchType.INTERSECTION;
    protected ChangeNotifier matchTypeNotifier = new ChangeNotifier(this);
    protected ScalePanel highlightedPanel;
    protected ScalePanelComparator scalePanelComparator;
    protected boolean optionsExpanded = false;
    protected boolean useGroups = false;
    protected Map<String, GroupPanelModel> groupMap = new HashMap<String, GroupPanelModel>();
    protected GroupPanelModel nullGroupModel = new GroupPanelModel("Other", null);

    //////////////////
    // Constructors //
    //////////////////

    public ScaleSetPanel(ScaleSetPanelModel m) {
        this(m, new CategoryComparator());
    }
    public ScaleSetPanel(ScaleSetPanelModel m, ScalePanelComparator comparator) {
        scalePanelComparator = comparator;
        buildControlPanel();
        buildSearchPanel();
        setModel(m);
    }

    ///////////
    // Build //
    ///////////

    // Rebuilds the scale set panel by rebuilding each scale
    // panel from the model and then relaying all the components.
    // out.  This method should be called if the model ever changes.
    public void rebuildAll() {
        buildScalePanels();
        rebuildUIOnly();
    }

    // Rebuilds the scale set panel using the existing
    // scale panels (assumes scale panels have been built).
    // This method should be called after the following
    // things have changed:
    // - The scale panels
    // - The scale panel comparator
    // - Whether or not to use groups
    // - Group membership
    public void rebuildUIOnly() {

        // Sort existing scale panels with current settings.
        sortScalePanels();

        // Remove all the components in the scale set panel.
        removeAll();

        // Initialize a new panel for the scale panels.
        pnlScales = new JPanel(new GridBagLayout());
        if(scaleAreaColor != null) {
            pnlScales.setBackground(scaleAreaColor);
        }

        // Initialize grid bag constraints.
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.FIRST_LINE_START;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weighty = 0.0;
        groupPanels = new ArrayList<GroupPanel>();

        // Build the scale panel area either with or without groups.
        if(useGroups) {
            rebuildUIWithGroups(g);
        } else {
            rebuildUIWithoutGroups(g);
        }

        // Add remaining dummy panels to force layout.
        constrain(g);

        // Add all components together.
        finalLayout();

        updateUI();
    }

    protected void rebuildUIWithGroups(GridBagConstraints g) {

        // Reset whether any of the panels think they're the top
        // or bottom panels.
        for(ScalePanel scalePanel : scalePanels) {
            scalePanel.setBottomScalePanel(false);
            scalePanel.setTopScalePanel(false);
        }

        boolean first = true;
        String prevGroup = null;
        ScalePanel prevPanel = null;
        GroupPanel groupPanel = null;

        for(ScalePanel scalePanel : scalePanels) {

            // Grab the group model assigned to this scale.
            GroupPanelModel curGroupModel = groupMap.get(scalePanel.getScalePanelModel().getKey());
            if(curGroupModel == null) {
                curGroupModel = nullGroupModel;     // "Other" group
            }
            String curGroup = curGroupModel.getName();

            // If the group has changed from the previous group, add
            // the next group panel to the scale set panel.
            if(isNewGroup(first, prevGroup, curGroup)) {

                if(prevPanel != null) {
                    prevPanel.setBottomScalePanel(true);
                }
                scalePanel.setTopScalePanel(true);

                // Create, customize and record.
                groupPanel = new GroupPanel(curGroupModel);
                groupPanel.addHighlightedListener(groupOpenCloseListener);
                applyScaleSetUISettings(groupPanel);
                groupPanels.add(groupPanel);

                // Insert group panel into container.
                g.gridy++;
                g.insets = new Insets(0, 0, 0, 0);
                pnlScales.add(groupPanel, g);
            }

            // Only show the current scale panel if the group is "open".
            scalePanel.setVisible(curGroupModel.isOpen());

            // Insert scale panel into container.
            g.gridy++;
            g.insets = new Insets(0, groupIndent, 0, 0);
            pnlScales.add(scalePanel, g);

            // Bookkeeping.
            first = false;
            prevGroup = curGroup;
            prevPanel = scalePanel;
        }

        g.insets = new Insets(0, 0, 0, 0);
        if(prevPanel != null) {
            prevPanel.setBottomScalePanel(true);
        }
    }

    protected boolean isNewGroup(boolean first, String prevGroup, String curGroup) {

        // If this is the first scale panel, signal new group.
        if(first) {
            return true;
        }

        // Check changes from null-group to real group and vice versa.
        if(prevGroup == null && curGroup == null) {
            return false;
        } else if(prevGroup == null || curGroup == null) {
            return true;
        }

        // Check changes between to real groups.
        return !prevGroup.equals(curGroup);
    }

    // Handles when a group is opened or closed.
    protected ChangeListener groupOpenCloseListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            GroupPanel pnl = (GroupPanel) e.getSource();
            GroupPanelModel changedGroupModel = pnl.getGroupPanelModel();
            boolean open = changedGroupModel.isOpen();

            // Loop through each scale panel and make it visible or invisible
            // depending on whether the affected group opened or closed.
            Map<String, ScalePanel> panelMap = getScalePanelMap();
            for(String key : panelMap.keySet()) {
                GroupPanelModel grp = groupMap.get(key);
                if(grp == null) {
                    grp = nullGroupModel;
                }
                if(changedGroupModel.equals(grp)) {
                    ScalePanel spnl = panelMap.get(key);
                    spnl.setVisible(open);
                }
            }
        }
    };

    protected void rebuildUIWithoutGroups(GridBagConstraints g) {
        for(ScalePanel scalePanel : scalePanels) {
            scalePanel.setVisible(true);        // Make sure they are all made visible
                                                // (could be invisible if groups were on).
            g.gridy++;
            pnlScales.add(scalePanel, g);
        }
    }

    // Constrain the scale panel layout with dummy panels.
    protected void constrain(GridBagConstraints g) {
        JPanel pnlTmp = new JPanel();
        pnlTmp.setBackground(Color.blue);
        Dimension zero = new Dimension(0,0);
        pnlTmp.setMinimumSize(zero);
        pnlTmp.setPreferredSize(zero);
        pnlTmp.setMaximumSize(zero);
        g.weighty = 1.0;
        g.gridy++;
        pnlScales.add(pnlTmp, g);
        g.gridx++;
        g.gridy--;
        g.weightx = 1.0;
        pnlTmp = new JPanel();
        pnlTmp.setOpaque(false);
        Dimension zeroHeight= new Dimension(1,0);
        pnlTmp.setPreferredSize(zeroHeight);
        pnlTmp.setMinimumSize(zeroHeight);
        pnlTmp.setMaximumSize(zeroHeight);
        pnlScales.add(pnlTmp, g);
    }

    protected void finalLayout() {

        // Add control panel.
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        add(pnlOptionsAll, gbc);

        // Add scale panels.
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.gridy = 1;
        add(new JScrollPane(pnlScales), gbc);

        // Add search panel.
        gbc.weighty = 0.0;
        gbc.gridy = 2;
        add(pnlSearch, gbc);
    }

    // Builds new scale panels from the scale panel models provided
    // by the scale set panel model.
    protected void buildScalePanels() {

        // Lazy attempt to keep the same scale panels open that
        // were open before the building.  Open state is saved
        // currently in the scale panel models, so they will
        // retain their open state as long as the scale panel
        // models are reused.
        /*Map<String, Boolean> openMap = new HashMap<String, Boolean>();
        if(scalePanels != null) {
            for(ScalePanel pnlScale : scalePanels) {
                openMap.put(pnlScale.getScalePanelModel().getKey(),
                    pnlScale.getScalePanelModel().isOpen());
            }
        }*/

        scalePanels = new ArrayList<ScalePanel>();

        String[] keys = model.getScaleKeys();

        // For each scale in the model, create an appropriate scale panel.
        for(int k = 0; k < keys.length; k++) {
            String key = keys[k];
            ScalePanelModel spm = model.getScalePanelModel(key);

            /*if(openMap.get(key) != null) {
                spm.setOpen(openMap.get(key));
            }*/

            // Create the scale panel based on the model.
            ScalePanel pnlScale = chooseScalePanel(spm);

            // Apply the scale set panel's current UI settings.
            applyScaleSetUISettings(pnlScale);

            // Panels' subselected states depend on the match type.
            pnlScale.setDefaultStateAllSelected(matchType == MatchType.INTERSECTION);

            pnlScale.addHighlightedListener(highlightedPanelListener);
            pnlScale.addSubselectedListener(subselectedPanelListener);
            scalePanels.add(pnlScale);

            // Keep the previously selected panel selected.
            if(highlightedPanel != null &&
                    highlightedPanel.getScalePanelModel().getKey().equals(key)) {
                pnlScale.setHighlighted(true);
            }
        }
    }

    // Create a scale panel using reflection based on the
    // given model.  Every scale panel should have the same
    // constructor signature.
    protected ScalePanel chooseScalePanel(ScalePanelModel spm) {
        try {
            Class<? extends ScalePanel> panelType = panelModelMap.get(spm.getClass());
            Constructor<? extends ScalePanel> ctor = panelType.getConstructor(ScaleSetPanel.class, spm.getClass());
            return ctor.newInstance(this, spm);
        } catch(Exception e) {

            // Should only happen if there is a problem with the code at design time.
            e.printStackTrace();
            return null;
        }
    }

    protected void applyScaleSetUISettings(ScalePanel pnlScale) {
        pnlScale.setTitleFont(titleFont);
        pnlScale.setTitleCountsFont(titleCountsFont);
        pnlScale.setNotesFont(notesFont);
        pnlScale.setFilterFont(filterFont);
        pnlScale.setBackgroundColor(backgroundColor);
        pnlScale.setBorderColor(borderColor);
        pnlScale.setSubselectedBorderColor(subselectedBorderColor);
        pnlScale.setHighlightColor(highlightColor);
        pnlScale.setTitleMargin(titleMargin);
        pnlScale.setShowTitleCounts(showTitleCounts);
        pnlScale.setPopupSectionLabels(popupSectionLabels);
        pnlScale.setOuterMargin(outerMargin);
        pnlScale.setInnerSpacing(innerSpacing);
        if(pnlScale instanceof EnumScaleMultiPanel) {
            pnlScale.setIcon(enumIcon);
            EnumScaleMultiPanel pnlEScale = (EnumScaleMultiPanel) pnlScale;
            pnlEScale.setCoalesceEvents(enumCoalesceEvents);
            pnlEScale.setShowValueCounts(enumShowValueCounts);
        } else if(pnlScale instanceof EnumScaleSinglePanel) {
            pnlScale.setIcon(enumIcon);
            EnumScaleSinglePanel pnlEScale = (EnumScaleSinglePanel) pnlScale;
            pnlEScale.setShowValueCounts(enumShowValueCounts);
        } else if(pnlScale instanceof DateScalePanel) {
            pnlScale.setIcon(dateIcon);
        } else if(pnlScale instanceof LongScalePanel) {
            pnlScale.setIcon(longIcon);
        } else if(pnlScale instanceof ContScalePanel) {
            pnlScale.setIcon(contIcon);
            // Cont edited color and error color not configurable yet.
        } else {
            pnlScale.setIcon(baseIcon);
        }
        for(ScaleSetChangedListener listener : valueListeners) {
            pnlScale.addScalePanelChangedListener(listener);
        }
        for(VisTypeChangedListener listener : visTypeListeners) {
            pnlScale.addVisTypeChangedListener(listener);
        }
        for(ColorsChangedListener listener : colorsListeners) {
            pnlScale.addColorsChangedListener(listener);
        }
        for(ListedChangedListener listener : listedListeners) {
            pnlScale.addListedChangedListener(listener);
        }
        for(PopupMenuClickedListener listener : popupListeners) {
            pnlScale.addPopupMenuClickedListener(listener);
        }
    }
    protected void applyScaleSetUISettings(GroupPanel pnlGroup) {
        pnlGroup.setTitleFont(groupTitleFont);
        pnlGroup.setNotesFont(groupNotesFont);
        pnlGroup.setTitleMargin(groupTitleMargin);
        // groupIndent not property of individual panels, but rather
        // the grid bag layout.
        pnlGroup.setOpenIcon(groupOpenIcon);
        pnlGroup.setClosedIcon(groupClosedIcon);
        pnlGroup.setBackground(groupBackground);
    }

    protected void buildSearchPanel() {
        pnlSearch = new JPanel();
        pnlSearch.setLayout(new BoxLayout(pnlSearch, BoxLayout.X_AXIS));

        txtSearch = new JTextField("", 10);
        txtSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchForScale();
            }
        });

        lblSearch = new JLabel("Find Scale:  ");
        lblSearch.setFont(searchFont);
        pnlSearch.add(lblSearch);
        pnlSearch.add(txtSearch);
        pnlSearch.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    protected void buildControlPanel() {

        // Active button next to show/hide options button.
        btnActiveButton = new JButton("Select All");
        btnActiveButton.setIcon(ImageLib.get(SdPlusImageModel.SEL_ON));
        btnActiveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectAll();
            }
        });

        // Show/hide options button.
        btnShowOptions = new RButton("Options");
        btnShowOptions.setIcon(SdPlusImageModel.OPTIONS_EXPAND);
        btnShowOptions.setHorizontalTextPosition(SwingConstants.LEFT);

        // Buttons on options panel.
        btnSelectAll = new JButton("Select All");
        btnSelectAll.setIcon(ImageLib.get(SdPlusImageModel.SEL_ON));
        btnSelectAll.addActionListener(e -> selectAll());

        btnDeselectAll = new JButton("Deselect All");
        btnDeselectAll.setIcon(ImageLib.get(SdPlusImageModel.SEL_OFF));
        btnDeselectAll.addActionListener(e -> deselectAll());

        JButton btnExpandAll = new JButton("Expand All");
        btnExpandAll.setIcon(ImageLib.get(CommonConcepts.EXPAND_ALL));
        btnExpandAll.addActionListener(e ->{
            for(ScalePanel pnlScale : scalePanels) {
                pnlScale.setOpen(true);
            }
            for(GroupPanel pnlGroup : groupPanels) {
                pnlGroup.setOpen(true);
            }
        });
        JButton btnCollapseAll = new JButton("Collapse All");
        btnCollapseAll.setIcon(ImageLib.get(CommonConcepts.COLLAPSE_ALL));
        btnCollapseAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(ScalePanel pnlScale : scalePanels) {
                    pnlScale.setOpen(false);
                }
                for(GroupPanel pnlGroup : groupPanels) {
                    pnlGroup.setOpen(false);
                }
            }
        });

        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlButtons.add(btnSelectAll);
        pnlButtons.add(btnExpandAll);
        pnlButtons.add(btnDeselectAll);
        pnlButtons.add(btnCollapseAll);

        JLabel lblMatchType = new JLabel("Match Type:  ");
        lblMatchType.setHorizontalAlignment(SwingConstants.RIGHT);

        optInt = new JRadioButton("Intersection");
        optUnion = new JRadioButton("Union");
        ButtonGroup grpMT = new ButtonGroup();
        grpMT.add(optInt);
        grpMT.add(optUnion);
        optInt.setSelected(true);

        optInt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMatchType(MatchType.INTERSECTION);
            }
        });
        optUnion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMatchType(MatchType.UNION);
            }
        });

        pnlMatchType = new JPanel(new GridLayout(2, 2));
        pnlMatchType.add(lblMatchType);
        pnlMatchType.add(optInt);
        pnlMatchType.add(new JPanel());
        pnlMatchType.add(optUnion);

        // Sort Type

        JLabel lblSortType = new JLabel("Sort Type:  ");
        lblSortType.setHorizontalAlignment(SwingConstants.RIGHT);

        JRadioButton optSortDefault = new JRadioButton("Category");
        JRadioButton optSortAlpha = new JRadioButton("Alphabetical");
        JRadioButton optSortSubselected = new JRadioButton("Subselected");
        ButtonGroup grpSort = new ButtonGroup();
        grpSort.add(optSortDefault);
        grpSort.add(optSortAlpha);
        grpSort.add(optSortSubselected);
        optSortDefault.setSelected(true);
        chkUseGroups = new JCheckBox("Use Groups");
        chkUseGroups.setSelected(useGroups);

        optSortDefault.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setScalePanelComparator(new CategoryComparator());
            }
        });
        optSortAlpha.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setScalePanelComparator(new AlphabeticalComparator());
            }
        });
        optSortSubselected.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setScalePanelComparator(new SubselectedComparator());
            }
        });
        chkUseGroups.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUseGroups(chkUseGroups.isSelected());
            }
        });

        pnlSortType = new JPanel(new GridLayout(4, 2));
        pnlSortType.add(lblSortType);
        pnlSortType.add(optSortDefault);
        pnlSortType.add(new JPanel());
        pnlSortType.add(optSortAlpha);
        pnlSortType.add(new JPanel());
        pnlSortType.add(optSortSubselected);
        pnlSortType.add(new JPanel());
        pnlSortType.add(chkUseGroups);

        pnlOptionsCollapsible = new JPanel();
        BoxLayout boxlayout = new BoxLayout(pnlOptionsCollapsible, BoxLayout.Y_AXIS);
        pnlOptionsCollapsible.setLayout(boxlayout);

        pnlOptionsCollapsible.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 5, 5, 5),
            BorderFactory.createEtchedBorder()));
        GuiUtil.addBorderedComponent(pnlOptionsCollapsible, pnlButtons,
            BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlOptionsCollapsible.add(pnlMatchType);
        pnlOptionsCollapsible.add(pnlSortType);

        JPanel pnlOptionsTop = new JPanel(new BorderLayout());
        pnlOptionsTop.add(btnActiveButton, BorderLayout.WEST);
        pnlOptionsTop.add(btnShowOptions, BorderLayout.EAST);

        pnlOptionsAll = new JPanel(new BorderLayout());
        GuiUtil.addBorderedComponent(pnlOptionsAll, pnlOptionsTop,
            BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderLayout.NORTH);

        btnShowOptions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                optionsExpanded = !optionsExpanded;
                updateOptionsExpanded();
            }
        });
        updateOptionsExpanded();
    }

    protected void updateOptionsExpanded() {
        if(optionsExpanded) {
            btnShowOptions.setIcon(SdPlusImageModel.OPTIONS_COLLAPSE);
            pnlOptionsAll.add(pnlOptionsCollapsible, BorderLayout.CENTER);
        } else {
            btnShowOptions.setIcon(SdPlusImageModel.OPTIONS_EXPAND);
            pnlOptionsAll.remove(pnlOptionsCollapsible);
        }
        pnlOptionsAll.updateUI();
    }

    //////////////////////////
    // Accessors & Mutators //
    //////////////////////////

    // Accessors

    // UI Settings

    public Font getSearchFont() {
        return searchFont;
    }
    public boolean isCoalesceEvents() {
        return coalesceEvents;
    }
    public Color getScaleAreaBackground() {
        return scaleAreaColor;
    }
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
    public ImageIcon getBaseIcon() {
        return baseIcon;
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
    public ImageIcon getEnumIcon() {
        return enumIcon;
    }
    public boolean isEnumCoalesceEvents() {
        return enumCoalesceEvents;
    }
    public boolean isEnumShowValueCounts() {
        return enumShowValueCounts;
    }
    public ImageIcon getContIcon() {
        return contIcon;
    }
    public ImageIcon getLongIcon() {
        return longIcon;
    }
    public ImageIcon getDateIcon() {
        return dateIcon;
    }
    public Font getGroupTitleFont() {
        return groupTitleFont;
    }
    public Font getGroupNotesFont() {
        return groupNotesFont;
    }
    public int getGroupTitleMargin() {
        return groupTitleMargin;
    }
    public int getGroupIndent() {
        return groupIndent;
    }
    public ImageIcon getGroupOpenIcon() {
        return groupOpenIcon;
    }
    public ImageIcon getGroupClosedIcon() {
        return groupClosedIcon;
    }
    public Color getGroupBackground() {
        return groupBackground;
    }

    // Options

    public MatchType getMatchType() {
        return matchType;
    }
    public boolean isShowMatchType() {
        return pnlMatchType.isVisible();
    }
    public boolean isShowSortType() {
        return pnlSortType.isVisible();
    }
    public ScalePanelComparator getScalePanelComparator() {
        return scalePanelComparator;
    }
    public boolean isUseGroups() {
        return useGroups;
    }

    // Highlight

    public String getHighlightedPanelKey() {
        if(highlightedPanel == null) {
            return null;
        }
        return highlightedPanel.getScalePanelModel().getKey();
    }

    // Other

    public String[] getScaleKeys() {        // For convenience
        return model.getScaleKeys();
    }
    public ScalePanel getScalePanel(String key) {
        for(ScalePanel pnlScale : scalePanels) {
            if(pnlScale.getScalePanelModel().getKey().equals(key)) {
                return pnlScale;
            }
        }
        return null;
    }
    public List<ScalePanel> getScalePanelList() {
        return new ArrayList<ScalePanel>(scalePanels);  // Return copy of the list.
    }
    public Map<String, ScalePanel> getScalePanelMap() {
        Map<String, ScalePanel> map = new HashMap<String, ScalePanel>();
        for(ScalePanel pnlScale : scalePanels) {
            map.put(pnlScale.getScalePanelModel().getKey(), pnlScale);
        }
        return map;
    }
    public ScaleSetPanelModel getModel() {
        return model;
    }

    // Mutators

    // UI Settings

    public void setSearchFont(Font font) {
        searchFont = font;
        lblSearch.setFont(searchFont);
    }
    public void setCoalesceEvents(boolean ce) {
        coalesceEvents = ce;
    }
    public void setScaleAreaBackground(Color newColor) {
        scaleAreaColor = newColor;
        pnlScales.setBackground(newColor);
    }
    public void setTitleFont(Font newFont) {
        titleFont = newFont;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setTitleFont(newFont);
        }
    }
    public void setTitleCountsFont(Font newFont) {
        titleCountsFont = newFont;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setTitleCountsFont(newFont);
        }
    }
    public void setNotesFont(Font newFont) {
        notesFont = newFont;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setNotesFont(newFont);
        }
    }
    public void setFilterFont(Font newFont) {
        filterFont = newFont;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setFilterFont(newFont);
        }
    }
    public void setBackgroundColor(Color color) {
        backgroundColor = color;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setBackgroundColor(color);
        }
    }
    public void setBorderColor(Color color) {
        borderColor = color;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setBorderColor(color);
        }
    }
    public void setSubselectedBorderColor(Color color) {
        subselectedBorderColor = color;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setSubselectedBorderColor(color);
        }
    }
    public void setHighlightColor(Color color) {
        highlightColor = color;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setHighlightColor(color);
        }
    }
    public void setTitleMargin(int margin) {
        titleMargin = margin;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setTitleMargin(margin);
        }
    }
    public void setBaseIcon(ImageIcon icon) {
        baseIcon = icon;
        setIcon(ScalePanel.class, baseIcon);
    }
    public void setShowTitleCounts(boolean show) {
        showTitleCounts = show;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setShowTitleCounts(show);
        }
    }
    public void setPopupSectionLabels(boolean labels) {
        popupSectionLabels = labels;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setPopupSectionLabels(labels);
        }
    }
    public void setOuterMargin(int margin) {
        outerMargin = margin;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setOuterMargin(margin);
        }
    }
    public void setInnerSpacing(int spacing) {
        innerSpacing = spacing;
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.setInnerSpacing(spacing);
        }
    }
    public void setEnumIcon(ImageIcon icon) {
        enumIcon = icon;
        setIcon(EnumScaleMultiPanel.class, enumIcon);
        setIcon(EnumScaleSinglePanel.class, enumIcon);
    }
    public void setEnumCoalesceEvents(boolean ce) {
        enumCoalesceEvents = ce;
        for(ScalePanel pnlScale : scalePanels) {
            if(pnlScale instanceof EnumScaleMultiPanel) {
                ((EnumScaleMultiPanel) pnlScale).setCoalesceEvents(ce);
            }
        }
    }
    public void setEnumShowValueCounts(boolean show) {
        enumShowValueCounts = show;
        for(ScalePanel pnlScale : scalePanels) {
            if(pnlScale instanceof EnumScaleBasePanel) {
                ((EnumScaleBasePanel) pnlScale).setShowValueCounts(show);
            }
        }
    }
    public void setContIcon(ImageIcon icon) {
        contIcon = icon;
        setIcon(ContScalePanel.class, contIcon);
    }
    public void setLongIcon(ImageIcon icon) {
        longIcon = icon;
        setIcon(LongScalePanel.class, longIcon);
    }
    public void setDateIcon(ImageIcon icon) {
        dateIcon = icon;
        setIcon(DateScalePanel.class, dateIcon);
    }
    protected void setIcon(Class<? extends ScalePanel> cls, ImageIcon icon) {
        for(ScalePanel pnlScale : scalePanels) {
            if(pnlScale.getClass().equals(cls)) {
                pnlScale.setIcon(icon);
            }
        }
    }
    public void setGroupTitleFont(Font font) {
        groupTitleFont = font;
        for(GroupPanel groupPanel : groupPanels) {
            groupPanel.setTitleFont(font);
        }
    }
    public void setGroupNotesFont(Font font) {
        groupNotesFont = font;
        for(GroupPanel groupPanel : groupPanels) {
            groupPanel.setNotesFont(font);
        }
    }
    public void setGroupTitleMargin(int margin) {
        groupTitleMargin = margin;
        for(GroupPanel groupPanel : groupPanels) {
            groupPanel.setTitleMargin(margin);
        }
    }
    public void setGroupIndent(int indent) {
        groupIndent = indent;
        rebuildUIOnly();
    }
    public void setGroupOpenIcon(ImageIcon icon) {
        groupOpenIcon = icon;
        for(GroupPanel groupPanel : groupPanels) {
            groupPanel.setOpenIcon(icon);
        }
    }
    public void setGroupClosedIcon(ImageIcon icon) {
        groupClosedIcon = icon;
        for(GroupPanel groupPanel : groupPanels) {
            groupPanel.setClosedIcon(icon);
        }
    }
    public void setGroupBackground(Color color) {
        groupBackground = color;
        for(GroupPanel groupPanel : groupPanels) {
            groupPanel.setBackground(color);
        }
    }

    // Options

    public void setMatchType(MatchType mt) {
        matchType = mt;
        if(matchType == MatchType.INTERSECTION) {
            optInt.setSelected(true);
            btnActiveButton.setText(btnSelectAll.getText());
            btnActiveButton.setIcon(btnSelectAll.getIcon());
            btnActiveButton.removeActionListener(btnActiveButton.getActionListeners()[0]);
            btnActiveButton.addActionListener(btnSelectAll.getActionListeners()[0]);
            for(ScalePanel pnl : scalePanels) {
                pnl.setDefaultStateAllSelected(true);
                pnl.getScalePanelModel().setDefaultAcceptedValue(true);
            }
        } else {
            optUnion.setSelected(true);
            btnActiveButton.setText(btnDeselectAll.getText());
            btnActiveButton.setIcon(btnDeselectAll.getIcon());
            btnActiveButton.removeActionListener(btnActiveButton.getActionListeners()[0]);
            btnActiveButton.addActionListener(btnDeselectAll.getActionListeners()[0]);
            for(ScalePanel pnl : scalePanels) {
                pnl.setDefaultStateAllSelected(false);
                pnl.getScalePanelModel().setDefaultAcceptedValue(false);
            }
        }
        matchTypeNotifier.fireStateChanged();
    }
    public void setShowMatchType(boolean show) {
        pnlMatchType.setVisible(show);
    }
    public void setShowSortType(boolean show) {
        pnlSortType.setVisible(show);
    }
    public void setScalePanelComparator(ScalePanelComparator cmp) {
        scalePanelComparator = cmp;
        rebuildUIOnly();
    }
    public void setUseGroups(boolean groups) {
        useGroups = groups;
        chkUseGroups.setSelected(useGroups);
        rebuildUIOnly();
    }
    public void assignGroup(String key, GroupPanelModel groupModel) {
        groupMap.put(key, groupModel);
        // Call rebuildUIOnly yourself after you are done assigning
        // all the groups to avoid repeated calls to rebuildUIOnly.
        // Perhaps a setGroups(Map<String, GroupPanelModel>) method
        // would have been better than an assignGroup method that only
        // establishes one group assignment per call -- then you
        // wouldn't have to call rebuildUIOnly yourself.
    }

    // Highlight

    public void setHighlightedPanelKey(String key) {
        if(key == null) {
            if(highlightedPanel != null) {
                highlightedPanel.setHighlighted(false);
                highlightedPanel = null;
            }
        } else {
            for(ScalePanel pnlScale : scalePanels) {
                if(pnlScale.getScalePanelModel().getKey().equals(key)) {
                    pnlScale.setHighlighted(true);
                }
            }
        }
    }
    // Internal method used to enforce that only one scale panel is
    // highlighted at a time, to keep track of the highlighted panel,
    // and make sure the panel is completely visible in the display.
    // This panel assumes that the supplied panel has already been
    // highlighted.
    protected void saveHighlightedNode(ScalePanel node) {
        if(highlightedPanel != null && highlightedPanel != node) {
            highlightedPanel.setHighlighted(false);
        }
        highlightedPanel = node;

        // Shift rectangle to make visible to far left of scale panel
        // area in case there is a non-zero group indentation.
        Rectangle origRect = node.getBounds();
        Rectangle leftRect = new Rectangle(0, origRect.y,
            origRect.width + origRect.x, origRect.height);
        pnlScales.scrollRectToVisible(leftRect);

        // The code above is repeated below on purpose!  In ScalePanel's
        // TitlePanelMouseListener every effort is made to set the
        // scale as the highlighted node AFTER any open or close operation
        // has happened (and thus the appropriate subpanels are made
        // visible or invisible).  BUT, even with updateUI being called
        // after the setOpen (and thus setVisible's) method, the bounds
        // of the scale panel are not updated.  Theoretically, after the
        // setOpen and updateUI methods are called, the bounds should be
        // set so that when this method is called, the getBounds method
        // returns the bounds of the newly expanded or collapsed panel.
        // However, when getBounds is called above, the bounds have not
        // been updated.  Amazingly, they do however get updated when the
        // scrollRectToVisible method is called on the scale set panel.
        // Thus the second block below just does what the above block does
        // except the getBounds method returns the proper bounds this time.
        // So instead of taking the time to figure out why this is, I am
        // going to repeat the code merely due to time constraints and leave
        // it as an exercise to the developer if they decide they want to
        // clean this up.

        origRect = node.getBounds();
        leftRect = new Rectangle(0, origRect.y,
            origRect.width + origRect.x, origRect.height);
        pnlScales.scrollRectToVisible(leftRect);
    }

    // Other

    // Change the model backing the scale set.
    public void setModel(ScaleSetPanelModel m) {

        // Save the model that backs this scale set panel.
        model = m;

        // Ask to be notified when the model changes.
        model.addScaleSetPanelModelListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                rebuildAll();
            }
        });

        // Build the scale panels according to the model.
        rebuildAll();
    }

    ////////////
    // Search //
    ////////////

    protected void searchForScale()
    {
        //get new results if search string changed
        if(lastSearch == null || !txtSearch.getText().equals(lastSearch)) {
            searchResults = findScalePanelsContaining(txtSearch.getText());
            searchPos = 0;
            lastSearch = txtSearch.getText();
        }

        //cycle through all test results
        if(searchResults.size() > 0) {
            searchResults.get(searchPos).setHighlighted(true);
            searchPos++;
            if(searchPos > searchResults.size() - 1) {
                searchPos = 0;
            }
        }
    }

    protected List<ScalePanel> findScalePanelsContaining(String label) {
        List<ScalePanel> scaleList = new ArrayList<ScalePanel>();
        for(ScalePanel pnlScale : scalePanels) {
            if(pnlScale.getTitle().toLowerCase().contains(label.toLowerCase())) {
                scaleList.add(pnlScale);
            }
        }
        return scaleList;
    }

    //////////
    // Misc //
    //////////

    public void selectAll() {
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.selectAll(coalesceEvents);
        }

        // The scale panels won't fire any events if coalesceEvents
        // is true.  That is left for the scale set panel to do here.
        if(coalesceEvents) {
            fireScaleSetChangedEvent(new ScaleSetChangedCoalescedEvent(this));
        }
    }

    public void deselectAll() {
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.deselectAll(coalesceEvents);
        }

        // The scale panels won't fire any events if coalesceEvents
        // is true.  That is left for the scale set panel to do here.
        if(coalesceEvents) {
            fireScaleSetChangedEvent(new ScaleSetChangedCoalescedEvent(this));
        }
    }

    //////////
    // Sort //
    //////////

    protected void sortScalePanels() {

        // Sort the panels.  Panels must know ahead of time
        // if the groups must take precedence over the sort
        // they were made for.
        scalePanelComparator.setUseGroupsMap(useGroups ? groupMap : null);
        Collections.sort(scalePanels, scalePanelComparator);

        // Let the first and last panels know they are the first
        // and last so their borders can be drawn correctly when
        // built.
        for(int p = 0; p < scalePanels.size(); p++) {
            scalePanels.get(p).setTopScalePanel(p == 0);
            scalePanels.get(p).setBottomScalePanel(p == scalePanels.size() - 1);
        }
    }

    ////////////
    // Events //
    ////////////

    public void addMatchTypeListener(ChangeListener listener) {
        matchTypeNotifier.addListener(listener);
    }

    protected ChangeListener highlightedPanelListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            saveHighlightedNode((ScalePanel) e.getSource());
        }
    };

    protected ChangeListener subselectedPanelListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            if(scalePanelComparator instanceof SubselectedComparator) {
                setScalePanelComparator(scalePanelComparator);
            }
        }
    };

    public ScaleSetChangedListener[] getScalePanelChangedListeners() {
        return valueListeners.toArray(new ScaleSetChangedListener[0]);
    }
    public void addScalePanelChangedListener(ScaleSetChangedListener listener) {
        valueListeners.add(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.addScalePanelChangedListener(listener);
        }
    }
    public void removeScalePanelChangedListener(ScaleSetChangedListener listener) {
        valueListeners.remove(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.addScalePanelChangedListener(listener);
        }
    }
    protected void fireScaleSetChangedEvent(ScaleSetChangedCoalescedEvent e) {
        for(ScaleSetChangedListener listener : valueListeners) {
            listener.valueChanged(e);
        }
    }

    public PopupMenuClickedListener[] getPopupMenuClickedListeners() {
        return popupListeners.toArray(new PopupMenuClickedListener[0]);
    }
    public void addPopupMenuClickedListener(PopupMenuClickedListener listener) {
        popupListeners.add(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.addPopupMenuClickedListener(listener);
        }
    }
    public void removePopupMenuClickedListener(PopupMenuClickedListener listener) {
        popupListeners.remove(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.removePopupMenuClickedListener(listener);
        }
    }

    public VisTypeChangedListener[] getVisTypeChangedListeners() {
        return visTypeListeners.toArray(new VisTypeChangedListener[0]);
    }
    public void addVisTypeChangedListener(VisTypeChangedListener listener) {
        visTypeListeners.add(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.addVisTypeChangedListener(listener);
        }
    }
    public void removeVisTypeChangedListener(VisTypeChangedListener listener) {
        visTypeListeners.remove(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.removeVisTypeChangedListener(listener);
        }
    }

    public ColorsChangedListener[] getColorsChangedListeners() {
        return colorsListeners.toArray(new ColorsChangedListener[0]);
    }
    public void addColorsChangedListener(ColorsChangedListener listener) {
        colorsListeners.add(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.addColorsChangedListener(listener);
        }
    }
    public void removeColorsChangedListener(ColorsChangedListener listener) {
        colorsListeners.remove(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.removeColorsChangedListener(listener);
        }
    }

    public ListedChangedListener[] getListedChangedListeners() {
        return listedListeners.toArray(new ListedChangedListener[0]);
    }
    public void addListedChangedListener(ListedChangedListener listener) {
        listedListeners.add(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.addListedChangedListener(listener);
        }
    }
    public void removeListedChangedListener(ListedChangedListener listener) {
        listedListeners.remove(listener);
        for(ScalePanel pnlScale : scalePanels) {
            pnlScale.removeListedChangedListener(listener);
        }
    }

    //////////////
    // toString //
    //////////////

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[#panels=" + scalePanels.size() +
            ", useGroups=" + useGroups +
            ", matchType=" + matchType +
            ", highlightedKey=" + (highlightedPanel == null ? "<none>"
                : highlightedPanel.getScalePanelModel().getKey()) + "]";
    }
}

