package replete.ui.sdplus.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import replete.ui.images.concepts.ImageLib;
import replete.ui.sdplus.MatchType;
import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.UiDefaults;
import replete.ui.sdplus.events.ColorsChangedEvent;
import replete.ui.sdplus.events.ColorsChangedListener;
import replete.ui.sdplus.events.ListedChangedEvent;
import replete.ui.sdplus.events.ListedChangedListener;
import replete.ui.sdplus.events.PopupMenuClickedEvent;
import replete.ui.sdplus.events.PopupMenuClickedListener;
import replete.ui.sdplus.events.ScalePanelChangedEvent;
import replete.ui.sdplus.events.ScaleSetChangedListener;
import replete.ui.sdplus.events.ValueChangedEvent;
import replete.ui.sdplus.events.VisTypeChangedEvent;
import replete.ui.sdplus.events.VisTypeChangedListener;
import replete.ui.sdplus.images.SdPlusImageModel;
import replete.ui.sdplus.menu.MenuConfiguration;
import replete.ui.sdplus.panels.DateScalePanel;
import replete.ui.sdplus.panels.DateScalePanelModel;
import replete.ui.sdplus.panels.EnumScaleMultiPanelModel;
import replete.ui.sdplus.panels.GroupPanelModel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;
import replete.ui.sdplus.panels.VisualizationType;
import replete.ui.sdplus.sort.AlphabeticalComparator;
import replete.ui.sdplus.sort.CategoryComparator;
import replete.ui.sdplus.sort.SubselectedComparator;
import replete.ui.sdplus.subsel.SubselectionContext;
import replete.ui.sdplus.subsel.Subselector;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;


/**
 * Demo application for the ScaleSetPanel framework (sdplus).
 *
 * @author Derek Trumbo
 */

public class Demo {

    // Model
    protected static DemoModel demoModel;

    // UI
    protected static ScaleSetPanel pnlSet;
    protected static EscapeFrame winDemo;
    protected static DemoTableModel demoTableModel;
    protected static JTable tblDemoModel;
    protected static JTextArea txtOutput;

    protected static void addOutput(Object o) {
        System.out.println(o.toString());
        txtOutput.append(o.toString() + "\n");
        txtOutput.setCaretPosition(txtOutput.getText().length());
    }

    public static void main(String[] args) {
        demoModel = DemoModelGenerator.generate();
        JComponent contentPane = buildContentPane();

        // Window.
        winDemo = new EscapeFrame("SDPlus: ScaleSetPanel Demo");
        winDemo.add(contentPane, BorderLayout.CENTER);
        winDemo.setSize(1000, 700);
        winDemo.setLocationRelativeTo(null);
        winDemo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        winDemo.setVisible(true);
    }

    protected static JComponent buildContentPane() {

        // Scale set panel.
        pnlSet = new ScaleSetPanel(demoModel);
        pnlSet.setPreferredSize(new Dimension(300, 100));
        pnlSet.addMatchTypeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                String str = "MatchType changed to " + pnlSet.getMatchType().name();
                addOutput(str);
                Dialogs.showMessage(winDemo, str);
                updateDemoTableSelected(null);
            }
        });
        pnlSet.addScalePanelChangedListener(new ScaleSetChangedListener() {
            public void valueChanged(ValueChangedEvent e) {
                addOutput(e);
                String key = null;
                if(e instanceof ScalePanelChangedEvent) {
                    key = ((ScalePanelChangedEvent) e).getKey();
                }
                updateDemoTableSelected(key);
            }
        });
        // Locking down the scale set panel so only one scale set
        // panel can have a given visualization type at a time.
        pnlSet.addVisTypeChangedListener(new VisTypeChangedListener() {
            public void valueChanged(VisTypeChangedEvent e) {
                addOutput(e);
                for(ScalePanel pnlScale : pnlSet.getScalePanelList()) {
                    ScalePanelModel mdl = pnlScale.getScalePanelModel();
                    if(mdl != e.getScalePanelModel() && mdl.getVisualizationType() == e.getNewVisType()) {
                        pnlScale.setVisualizationType(VisualizationType.NONE);
                    }
                }
                updateDemoTableColors();
            }
        });
        pnlSet.addColorsChangedListener(new ColorsChangedListener() {
            public void valueChanged(ColorsChangedEvent e) {
                addOutput(e);
                updateDemoTableColors();
            }
        });
        pnlSet.addListedChangedListener(new ListedChangedListener() {
            public void valueChanged(ListedChangedEvent e) {
                addOutput(e);
                updateDemoTableListed(e.getKey(), e.getNewState());
            }
        });
        pnlSet.addPopupMenuClickedListener(new PopupMenuClickedListener() {
            public void valueChanged(PopupMenuClickedEvent e) {
                addOutput(e);
            }
        });
        setPanelGroups();

        // Option buttons tabbed pane.
        JTabbedPane tabs = buildOptionButtonsPanel();

        // Primary model
        demoTableModel = new DemoTableModel(demoModel);
        tblDemoModel = new JTable(demoTableModel);
        JScrollPane scp = new JScrollPane(tblDemoModel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scp.setPreferredSize(new Dimension(100, 150));
        tblDemoModel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblDemoModel.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
               JTable table, Object value, boolean isSelected, boolean hasFocus,
               int row, int col)
            {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                JLabel lbl = (JLabel) c;
                DemoTableModel mdl = (DemoTableModel) table.getModel();
                lbl.setBackground(mdl.getColor(row));
                return lbl;
            }
        });
        setColumnWidths();

        updateDemoTableSelected(null);

        String keys ="";
        for(DataScale dScale : demoModel.scales) {
            keys += dScale.key + ", ";
        }
        keys = keys.substring(0, keys.length() - 2);
        keys = keys.replaceAll("z", "z(#)").replaceAll("rb", "rb(^)");
        JLabel lbl0 = new JLabel("<html><b>* Scale panel keys for reference: " + keys + "</b></html>");
        lbl0.setOpaque(true);
        lbl0.setBackground(Color.cyan);
        JLabel lbl1 = new JLabel("<html><b>* Primary model backing the scale set panel (blank cell means <i>&lt;no value></i> for that data element's scale):</b></html>");
        lbl1.setOpaque(true);
        lbl1.setBackground(Color.yellow);
        JLabel lbl2 = new JLabel("<html><b>* (#)Separate model backing the 'Special' scale panel is array of 4 values: {'North', 'East', 'South', 'West'}</b></html>");
        JLabel lbl3 = new JLabel("<html><b>* (^)Separate model backing the 'Radio Button' scale panel is array of 5 values: {'Waveforms', 'Scatt...', etc.}</b></html>");
        lbl2.setOpaque(true);
        lbl2.setBackground(Color.yellow);
        lbl3.setOpaque(true);
        lbl3.setBackground(Color.yellow);
        JPanel pnlLabels0 = new JPanel(new BorderLayout());
        pnlLabels0.add(lbl0, BorderLayout.NORTH);
        pnlLabels0.add(lbl1, BorderLayout.SOUTH);
        JPanel pnlModels = new JPanel(new BorderLayout());
        pnlModels.add(pnlLabels0, BorderLayout.NORTH);
        pnlModels.add(scp, BorderLayout.CENTER);
        JPanel pnlLabels1 = new JPanel(new BorderLayout());
        pnlLabels1.add(lbl2, BorderLayout.NORTH);
        pnlLabels1.add(lbl3, BorderLayout.SOUTH);
        pnlModels.add(pnlLabels1, BorderLayout.SOUTH);

        // Left side
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.add(tabs, BorderLayout.CENTER);
        pnlLeft.add(pnlModels, BorderLayout.SOUTH);

        // Split pane.
        JSplitPane pnlSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        pnlSplit.setLeftComponent(pnlLeft);
        pnlSplit.setRightComponent(pnlSet);

        return pnlSplit;
    }

    private static void setPanelGroups() {
        GroupPanelModel nameGroup = new GroupPanelModel("Names", "This group contains scales related to names.");
        GroupPanelModel timeGroup = new GroupPanelModel("Times", "This group contains scales related to times.");
        GroupPanelModel zGroup = new GroupPanelModel("Separate Models", null);
        GroupPanelModel plotGroup = new GroupPanelModel("Plot Options", "Change plot type and characteristics.");

        pnlSet.assignGroup("fn", nameGroup);
        pnlSet.assignGroup("ln", nameGroup);
        pnlSet.assignGroup("bd", timeGroup);
        pnlSet.assignGroup("ts", timeGroup);
        pnlSet.assignGroup("z", zGroup);
        pnlSet.assignGroup("rb", plotGroup);

        pnlSet.rebuildUIOnly();
    }

    protected static void updateDemoTableSelected(String keyScaleChanged) {
        Subselector subsel = new Subselector(demoModel, new SubselectionContext() {
            public boolean existsInContext(String key) {
                return !key.equals("z") && !key.equals("rb");
            }
        });
        int[] selectedElements = subsel.findSubselectedElements(pnlSet.getMatchType(), keyScaleChanged);
        addOutput(subsel.toString(pnlSet.getMatchType()));
        String[] selYN = new String[6];
        for(int x = 0; x < 6; x++) {
            selYN[x] = "   N";
        }
        for(int i : selectedElements) {
            selYN[i] = "Y";
        }
        demoTableModel.setSelYN(selYN);
    }

    protected static void updateDemoTableColors() {
        ScalePanelModel colorModel = null;
        for(ScalePanel pnlScale : pnlSet.getScalePanelList()) {
            ScalePanelModel mdl = pnlScale.getScalePanelModel();
            if(mdl.getVisualizationType() == VisualizationType.COLOR) {
                colorModel = mdl;
            }
        }
        if(colorModel != null && !colorModel.getKey().equals("z") && !colorModel.getKey().equals("rb")) {
            int cnt = demoModel.getDataElementCount(colorModel.getKey());
            Color[] colors = new Color[cnt];
            for(int d = 0; d < cnt; d++) {
                Object val = demoModel.getValue(colorModel.getKey(), d);
                colors[d] = colorModel.getColor(val);
            }
            demoTableModel.setColors(colors);
        } else {
            demoTableModel.setColors(null);
        }
    }

    protected static void updateDemoTableListed(String keyScaleListed, boolean listed) {
        demoTableModel.notListed.put(keyScaleListed, !listed);
        demoTableModel.fireTableStructureChanged();
        setColumnWidths();
    }

    protected static void setColumnWidths() {
        tblDemoModel.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblDemoModel.getColumnModel().getColumn(1).setPreferredWidth(35);
        tblDemoModel.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblDemoModel.getColumnModel().getColumn(5).setPreferredWidth(140);
        tblDemoModel.getColumnModel().getColumn(6).setPreferredWidth(40);
        tblDemoModel.getColumnModel().getColumn(7).setPreferredWidth(50);
        tblDemoModel.getColumnModel().getColumn(8).setPreferredWidth(50);
        tblDemoModel.getColumnModel().getColumn(9).setPreferredWidth(40);
        tblDemoModel.getColumnModel().getColumn(10).setPreferredWidth(50);
        tblDemoModel.getColumnModel().getColumn(11).setPreferredWidth(40);
        tblDemoModel.getColumnModel().getColumn(12).setPreferredWidth(140);
    }

    protected static JTabbedPane buildOptionButtonsPanel() {

        JButton btnRebuild = buildOptionButton("Change model & REBUILD ScaleSetPanel", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(demoModel.scales.size() >= 1) {

                    String kk = demoModel.scales.get(1).key;  // "ln" (last name)

                    // Change the data.
                    demoModel.data.get(0).values.put(kk, "XXYYZZ");

                    // Rebuild the scale panel model for that key.
                    demoModel.modelMap.put(kk, demoModel.buildInitialScalePanelModel(kk));

                    // Update demo table.
                    demoTableModel.fireTableDataChanged();

                    // Change the scales.
                    demoModel.scales.remove(demoModel.scales.size() - 1);

                    // Tell all listeners
                    demoModel.fireScaleSetPanelModelChanged();
                }
            }
        });
        JButton btnGetSelKey = buildOptionButton("Get SELECTED panel key", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                message(pnlSet.getHighlightedPanelKey() == null ? "<none>" :
                    pnlSet.getHighlightedPanelKey());
            }
        });
        JButton btnSetSelKey = buildOptionButton("Set SELECTED panel", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String mt = JOptionPane.showInputDialog(winDemo, "Enter new selected panel key (e.g. fn, ln, bd, height, weight) or 'none':", "");
                if(mt != null) {
                    if(mt.equalsIgnoreCase("none")) {
                        mt = null;
                    }
                    pnlSet.setHighlightedPanelKey(mt);
                }
            }
        });
        JButton btnAuto = buildOptionButton("Open AUTONOMOUS Demo", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AutonomousFrame f = new AutonomousFrame();
                f.setVisible(true);
            }
        });
        btnAuto.setOpaque(true);
        btnAuto.setBackground(Color.cyan);
        JButton btnToggleCoalesce = buildOptionButton("Toggle COALESCE Events", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setCoalesceEvents(!pnlSet.isCoalesceEvents());
                toggledTo("Coalesce events", pnlSet.isCoalesceEvents());
            }
        });
        JButton btnGetMatchType = buildOptionButton("Get MATCH TYPE", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                message(pnlSet.getMatchType().name());
            }
        });
        JButton btnSetMatchTypeInt = buildOptionButton("Set MATCH TYPE to Intersection", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setMatchType(MatchType.INTERSECTION);
            }
        });
        JButton btnSetMatchTypeUnion = buildOptionButton("Set MATCH TYPE to Union", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setMatchType(MatchType.UNION);
            }
        });
        JButton btnToggleMTVis = buildOptionButton("Toggle MATCH TYPE Visibility", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setShowMatchType(!pnlSet.isShowMatchType());
                toggledTo("Match type visibility", pnlSet.isShowMatchType());
            }
        });
        JButton btnToggleSTVis = buildOptionButton("Toggle SORT TYPE Visibility", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setShowSortType(!pnlSet.isShowSortType());
                toggledTo("Sort type visibility", pnlSet.isShowSortType());
            }
        });
        JButton btnSetTitleFont = buildOptionButton("Set Title FONT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font f = getUserFont(pnlSet.getTitleFont());
                if(f != null) {
                    pnlSet.setTitleFont(f);
                }
            }
        });
        JButton btnSetTitleCountsFont = buildOptionButton("Set Title Counts FONT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font f = getUserFont(pnlSet.getTitleCountsFont());
                if(f != null) {
                    pnlSet.setTitleCountsFont(f);
                }
            }
        });
        JButton btnSetNotesFont = buildOptionButton("Set Notes FONT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font f = getUserFont(pnlSet.getNotesFont());
                if(f != null) {
                    pnlSet.setNotesFont(f);
                }
            }
        });
        JButton btnSetFilterFont = buildOptionButton("Set Filter FONT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font f = getUserFont(pnlSet.getFilterFont());
                if(f != null) {
                    pnlSet.setFilterFont(f);
                }
            }
        });
        JButton btnSetSearchFont = buildOptionButton("Set Search FONT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font f = getUserFont(pnlSet.getSearchFont());
                if(f != null) {
                    pnlSet.setSearchFont(f);
                }
            }
        });
        JButton btnSetSortDefault = buildOptionButton("Set Default SORT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setScalePanelComparator(new CategoryComparator());
            }
        });
        JButton btnSetSortAlpha = buildOptionButton("Set Alphabetical SORT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setScalePanelComparator(new AlphabeticalComparator());
            }
        });
        JButton btnSetSortSubs = buildOptionButton("Set Subselected SORT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setScalePanelComparator(new SubselectedComparator());
            }
        });
        JButton btnSetSortHeight = buildOptionButton("Set Height Panel Always First SORT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setScalePanelComparator(new CategoryComparator() {
                    @Override
                    public int compare(ScalePanel p1, ScalePanel p2) {
                        int gCompare = compareGroups(p1, p2);
                        if(gCompare != 0) {
                            return gCompare;
                        }

                        if(p1.getScalePanelModel().getKey().equals("height")) {
                            return -1;
                        }
                        if(p2.getScalePanelModel().getKey().equals("height")) {
                            return 1;
                        }

                        return super.compare(p1, p2);
                    }
                });
            }
        });
        JButton btnSetBackgroundClr = buildOptionButton("Set Background COLOR", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = getColor(pnlSet.getBackgroundColor());
                if(c != null) {
                    pnlSet.setBackgroundColor(c);
                }
            }
        });
        JButton btnSetBorderClr = buildOptionButton("Set Border COLOR", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = getColor(pnlSet.getBorderColor());
                if(c != null) {
                    pnlSet.setBorderColor(c);
                }
            }
        });
        JButton btnSetSubsBorderClr = buildOptionButton("Set Subselected Brdr COLOR", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = getColor(pnlSet.getSubselectedBorderColor());
                if(c != null) {
                    pnlSet.setSubselectedBorderColor(c);
                }
            }
        });
        JButton btnSetHighlightClr = buildOptionButton("Set Highlight COLOR", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = getColor(pnlSet.getHighlightColor());
                if(c != null) {
                    pnlSet.setHighlightColor(c);
                }
            }
        });
        JButton btnSetRandomBorderClr = buildOptionButton("Set Multi Border COLOR", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               for(ScalePanel pnlScale : pnlSet.getScalePanelList()) {
                   Random r = new Random();
                   Color clr = new Color((int)(r.nextFloat() * 256), (int)(r.nextFloat() * 256), (int)(r.nextFloat() * 256));
                   pnlScale.setBorderColor(clr);
               }
            }
        });
        JButton btnSetBaseIcon = buildOptionButton("Set Base ICON", e -> {
            pnlSet.setBaseIcon(UiDefaults.ENUM_ICON);
        });
        JButton btnSetEnumIcon = buildOptionButton("Set Enum ICON", e -> {
            pnlSet.setEnumIcon(ImageLib.get(SdPlusImageModel.SP_ENUM_PLACEHOLDER));
        });
        JButton btnSetContIcon = buildOptionButton("Set Cont ICON", e -> {
            pnlSet.setContIcon(ImageLib.get(SdPlusImageModel.SP_CONT_PLACEHOLDER));
        });
        JButton btnSetLongIcon = buildOptionButton("Set Long ICON", e -> {
            pnlSet.setLongIcon(UiDefaults.ENUM_ICON);
        });
        JButton btnSetDateIcon = buildOptionButton("Set Date ICON", e -> {
            pnlSet.setDateIcon(ImageLib.get(SdPlusImageModel.SP_DATE_PLACEHOLDER));
        });
        JButton btnToggleEnumCounts = buildOptionButton("Toggle Enum Value COUNTS", e -> {
            pnlSet.setEnumShowValueCounts(!pnlSet.isEnumShowValueCounts());
            toggledTo("Enum value counts", pnlSet.isEnumShowValueCounts());
        });
        JButton btnToggleEnumCoalesce = buildOptionButton("Toggle Enum COALESCE Events", e -> {
            pnlSet.setEnumCoalesceEvents(!pnlSet.isEnumCoalesceEvents());
            toggledTo("Enum coalesce events", pnlSet.isEnumCoalesceEvents());
        });
        JButton btnSetOuterMargin = buildOptionButton("Set Outer MARGIN", e -> {
            String val = JOptionPane.showInputDialog(winDemo,
                "Enter Panels' Outer Margin", "" + pnlSet.getOuterMargin());
            if(val != null) {
                int m = Integer.parseInt(val);
                pnlSet.setOuterMargin(m);
            }
        });
        JButton btnSetInnerSpacing = buildOptionButton("Set Inner SPACING", e -> {
            String val = JOptionPane.showInputDialog(winDemo,
                "Enter Panels' Inner Spacing", "" + pnlSet.getInnerSpacing());
            if(val != null) {
                int m = Integer.parseInt(val);
                pnlSet.setInnerSpacing(m);
            }
        });
        JButton btnTogglePopupSectionLabels = buildOptionButton("Toggle POPUP Section Labels", e -> {
            pnlSet.setPopupSectionLabels(!pnlSet.isPopupSectionLabels());
            toggledTo("Popup section labels", pnlSet.isPopupSectionLabels());
        });
        JButton btnSetDateFormat = buildOptionButton("Set Birth Date DATE FORMAT", e -> {
            String fmt = JOptionPane.showInputDialog(winDemo,
                "Enter new birth date date format (e.g. yyyy/MM/dd or M/d/yyyy)\nhttp://java.sun.com/javase/6/docs/api/java/text/SimpleDateFormat.html", "");
            if(fmt != null) {
                SimpleDateFormat newFmt = new SimpleDateFormat(fmt);
                newFmt.setLenient(false);
                DateScalePanel bdPanel = (DateScalePanel) pnlSet.getScalePanel("bd");
                ((DateScalePanelModel) bdPanel.getScalePanelModel()).setDateFormat(newFmt);
                bdPanel.updateDateFormat();
            }
        });
        JButton btnSetTSDateFormat = buildOptionButton("Set Time Stamp DATE FORMAT", e -> {
            String fmt = JOptionPane.showInputDialog(winDemo,
                "Enter new time stamp date format (e.g. yyyy/MM/dd or M/d/yyyy)\nhttp://java.sun.com/javase/6/docs/api/java/text/SimpleDateFormat.html", "");
            if(fmt != null) {
                SimpleDateFormat newFmt = new SimpleDateFormat(fmt);
                newFmt.setLenient(false);
                DateScalePanel bdPanel = (DateScalePanel) pnlSet.getScalePanel("ts");
                ((DateScalePanelModel) bdPanel.getScalePanelModel()).setDateFormat(newFmt);
                bdPanel.updateDateFormat();
            }
        });
        JButton btnSetTitleMargin = buildOptionButton("Set Title MARGIN", e -> {
            String val = JOptionPane.showInputDialog(winDemo, "Enter Title Margin", "" + pnlSet.getTitleMargin());
            if(val != null) {
                int m = Integer.parseInt(val);
                pnlSet.setTitleMargin(m);
            }
        });
        JButton btnSetGroupTitleMargin = buildOptionButton("Set Group Title MARGIN", e -> {
            String val = JOptionPane.showInputDialog(winDemo, "Enter Group Title Margin", "" + pnlSet.getTitleMargin());
            if(val != null) {
                int m = Integer.parseInt(val);
                pnlSet.setGroupTitleMargin(m);
            }
        });
        JButton btnToggleTitleCounts = buildOptionButton("Toggle Title COUNTS", e -> {
            pnlSet.setShowTitleCounts(!pnlSet.isShowTitleCounts());
            toggledTo("Title counts", pnlSet.isShowTitleCounts());
        });
        JButton btnSetPopupGroupVis = buildOptionButton("Set POPUP group vis", e -> {
            String opt = JOptionPane.showInputDialog(winDemo, "Enter group name and true or false (e.g. Selection;false)");
            if(opt != null) {
                String[] parts = opt.split("[^A-Za-z]");
                String groupName = parts[0];
                boolean visible = new Boolean(parts[1]);
                for(ScalePanel pnl : pnlSet.getScalePanelList()) {
                    pnl.getMenuConfiguration().setGroupVisible(groupName, visible);
                }
            }
        });
        JButton btnSetScaleAreaClr = buildOptionButton("Set Scale Area COLOR", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = getColor(pnlSet.getScaleAreaBackground());
                if(c != null) {
                    pnlSet.setScaleAreaBackground(c);
                }
            }
        });
        JButton btnToggleUseGroups = buildOptionButton("Toggle use GROUPS", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlSet.setUseGroups(!pnlSet.isUseGroups());
                toggledTo("Use groups", pnlSet.isUseGroups());
            }
        });
        JButton btnSetGroupBG = buildOptionButton("Set Group Background COLOR", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = getColor(pnlSet.getGroupBackground());
                if(c != null) {
                    pnlSet.setGroupBackground(c);
                }
            }
        });
        JButton btnSetGroupTitleFont = buildOptionButton("Set Group Title FONT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font f = getUserFont(pnlSet.getGroupTitleFont());
                if(f != null) {
                    pnlSet.setGroupTitleFont(f);
                }
            }
        });
        JButton btnToggleSomeMenus = buildOptionButton("Toggle POPUP X, Y menus", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean saveValue = false;
                for(ScalePanel pnl : pnlSet.getScalePanelList()) {
                    boolean newValue = !pnl.getMenuConfiguration().isMenuItemVisible(
                        MenuConfiguration.MNU_GRP_VIS, "X Axis");
                    pnl.getMenuConfiguration().setMenuItemVisible(
                        MenuConfiguration.MNU_GRP_VIS, "X Axis", newValue);
                    pnl.getMenuConfiguration().setMenuItemVisible(
                        MenuConfiguration.MNU_GRP_VIS, "Y Axis", newValue);
                    saveValue = newValue;
                }
                toggledTo("POPUP X, Y menus", saveValue);
            }
        });
        JButton btnSetGroupIndent = buildOptionButton("Set group INDENT", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String val = JOptionPane.showInputDialog(winDemo, "Enter group indent", "" + pnlSet.getGroupIndent());
                if(val != null) {
                    int m = Integer.parseInt(val);
                    pnlSet.setGroupIndent(m);
                }
            }
        });
        JButton btnUpdatePanel = buildOptionButton("Change LN Model & UPDATE", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ScalePanel panel = pnlSet.getScalePanelMap().get("ln");
                ScalePanelModel model = panel.getScalePanelModel();
                model.setName("New Name");
                model.setNote("This is the new note.");
                model.setOpen(true);
                model.setVisualizationType(VisualizationType.COLOR);
                ((EnumScaleMultiPanelModel) model).getSelectedValues().remove("Andersen");
                panel.updateUIFromModel();

                // Changed value events not fired in the above case when
                // simply update is called, so update demo table here.
                updateDemoTableSelected("ln");
            }
        });
        JButton btnRandomizeUI = buildOptionButton("Randomize UI :)", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<ScalePanel> scalePanels = pnlSet.getScalePanelList();
                for(ScalePanel pnlScale : scalePanels) {
                    pnlScale.setTitleFont(pickFont());
                    pnlScale.setTitleCountsFont(pickFont());
                    pnlScale.setNotesFont(pickFont());
                    pnlScale.setFilterFont(pickFont());
                    pnlScale.setBackgroundColor(pickColor());
                    pnlScale.setBorderColor(pickColor());
                    pnlScale.setHighlightColor(pickColor());
                    pnlScale.setTitleMargin(pickInt(16));
                    pnlScale.setShowTitleCounts(pickBool());
                    pnlScale.setPopupSectionLabels(pickBool());
                    pnlScale.setOuterMargin(pickInt(16));
                    pnlScale.setInnerSpacing(pickInt(16));
                }
            }
        });

        int rows = 5;
        int cols = 4;

        initOptionButtons(rows, cols);
        setOptionButton(btnRebuild, 0, 0);
        setOptionButton(btnGetSelKey, 0, 1);
        setOptionButton(btnSetSelKey, 0, 2);
        setOptionButton(btnToggleCoalesce, 0, 3);
        setOptionButton(btnGetMatchType, 1, 0);
        setOptionButton(btnSetMatchTypeInt, 1, 1);
        setOptionButton(btnSetMatchTypeUnion, 1, 2);
        setOptionButton(btnSetSearchFont, 1, 3);
        setOptionButton(btnSetSortDefault, 2, 0);
        setOptionButton(btnSetSortAlpha, 2, 1);
        setOptionButton(btnSetSortSubs, 2, 2);
        setOptionButton(btnSetSortHeight, 2, 3);
        setOptionButton(btnSetScaleAreaClr, 3, 0);
        setOptionButton(btnToggleMTVis, 3, 1);
        setOptionButton(btnToggleSTVis, 3, 2);
        setOptionButton(btnToggleUseGroups, 3, 3);
        setOptionButton(btnSetGroupTitleMargin, 4, 0);
        setOptionButton(btnSetGroupBG, 4, 1);
        setOptionButton(btnSetGroupTitleFont, 4, 2);
        setOptionButton(btnSetGroupIndent, 4, 3);

        JPanel pnlSSPOptions = new JPanel(new GridLayout(rows, cols, 4, 4));
        pnlSSPOptions.setOpaque(false);
        for(int r = 0; r < optionButtons.length; r++) {
            for(int c = 0; c < optionButtons[0].length; c++) {
                Component cmp = optionButtons[r][c];
                if(cmp == null) {
                    JPanel pnlBlank = new JPanel();
                    pnlBlank.setOpaque(false);
                    pnlSSPOptions.add(pnlBlank);
                } else {
                    pnlSSPOptions.add(cmp);
                }
            }
        }

        rows = 6;
        cols = 5;

        initOptionButtons(rows, cols);
        setOptionButton(btnSetTitleFont, 0, 0);
        setOptionButton(btnSetTitleCountsFont, 0, 1);
        setOptionButton(btnSetNotesFont, 0, 2);
        setOptionButton(btnSetFilterFont, 0, 3);
        setOptionButton(btnSetBackgroundClr, 1, 0);
        setOptionButton(btnSetBorderClr, 1, 1);
        setOptionButton(btnSetSubsBorderClr, 1, 2);
        setOptionButton(btnSetHighlightClr, 1, 3);
        setOptionButton(btnSetRandomBorderClr, 1, 4);
        setOptionButton(btnSetBaseIcon, 2, 0);
        setOptionButton(btnSetEnumIcon, 2, 1);
        setOptionButton(btnSetContIcon, 2, 2);
        setOptionButton(btnSetLongIcon, 2, 3);
        setOptionButton(btnSetDateIcon, 2, 4);
        setOptionButton(btnAuto, 0, 4);
        setOptionButton(btnSetTitleMargin, 3, 0);
        setOptionButton(btnToggleTitleCounts, 3, 1);
        setOptionButton(btnSetOuterMargin, 3, 2);
        setOptionButton(btnSetInnerSpacing, 3, 3);
        setOptionButton(btnToggleEnumCounts, 4, 0);
        setOptionButton(btnToggleEnumCoalesce, 4, 1);
        setOptionButton(btnTogglePopupSectionLabels, 4, 2);
        setOptionButton(btnSetDateFormat, 4, 3);
        setOptionButton(btnSetTSDateFormat, 4, 4);
        setOptionButton(btnSetPopupGroupVis, 5, 0);
        setOptionButton(btnToggleSomeMenus, 5, 1);
        setOptionButton(btnUpdatePanel, 5, 2);
        setOptionButton(btnRandomizeUI, 5, 4);

        JPanel pnlSPOptions = new JPanel(new GridLayout(rows, cols, 4, 4));
        pnlSPOptions.setOpaque(false);
        for(int r = 0; r < optionButtons.length; r++) {
            for(int c = 0; c < optionButtons[0].length; c++) {
                Component cmp = optionButtons[r][c];
                if(cmp == null) {
                    JPanel pnlBlank = new JPanel();
                    pnlBlank.setOpaque(false);
                    pnlSPOptions.add(pnlBlank);
                } else {
                    pnlSPOptions.add(cmp);
                }
            }
        }

        JLabel lblOutput = new JLabel("<html><b>This output area displays the events as they occur when you " +
            "interact with the ScaleSetPanel.  The <i>coalesce events</i> properties for both the " +
            "scale set panel and each individual enumerated scale panel (check box only) will determine " +
            "whether a single event is fired when you choose <u>Select All</u> or <u>Deselect All</u> or whether an event " +
            "is fired for each value that was changed.</b></</html>");
        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        JPanel pnlBtnClear = new JPanel();
        JButton btnClear = new JButton("Clear Output");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtOutput.setText("");
            }
        });
        pnlBtnClear.add(btnClear);
        JPanel pnlOutput = new JPanel(new BorderLayout());
        pnlOutput.add(lblOutput, BorderLayout.NORTH);
        pnlOutput.add(new JScrollPane(txtOutput), BorderLayout.CENTER);
        pnlOutput.add(pnlBtnClear, BorderLayout.SOUTH);


        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Scale Set Panel", pnlSSPOptions);
        tabs.addTab("Scale Panels", pnlSPOptions);
        tabs.addTab("Event Output", pnlOutput);

        return tabs;
    }

    protected static final Random RAND = new Random();
    protected static String pickString(String[] str) {
        int randIdx = RAND.nextInt(str.length);
        return str[randIdx];
    }
    protected static Color pickColor() {
        return new Color((int)(RAND.nextFloat() * 256), (int)(RAND.nextFloat() * 256), (int)(RAND.nextFloat() * 256));
    }
    protected static int pickInt(int max) {
        return RAND.nextInt(max);
    }
    protected static boolean pickBool() {
        return RAND.nextBoolean();
    }
    protected static Font pickFont() {
        String[] fontNames = {"Courier New", "Arial", "Monaco", "Times New Roman"};
        return new Font(pickString(fontNames), pickInt(5), pickInt(12) + 9);
    }

    protected static void toggledTo(String what, boolean value) {
        Dialogs.showMessage(winDemo, what + " toggled to " + value, "ScaleSetPanel Demo");
    }

    protected static void message(String what) {
        Dialogs.showMessage(winDemo, what, "ScaleSetPanel Demo");
    }

    // Option button management.
    protected static Component[][] optionButtons;
    protected static int xx = 0;
    protected static void initOptionButtons(int rows, int cols) {
        optionButtons = new Component[rows][cols];
    }
    protected static void setOptionButton(JButton btn, int row, int col) {
        optionButtons[row][col] = btn;
        xx++;

    }
    protected static JButton buildOptionButton(String title, ActionListener listener) {
        JButton btn = new JButton("<html><center>" + title + "</center></html>");
        btn.addActionListener(listener);
        return btn;
    }

    public static Font getUserFont(Font curFont) {

      FontChooser dlg = new FontChooser(winDemo);
      SimpleAttributeSet a = new SimpleAttributeSet();
      StyleConstants.setFontFamily(a, curFont.getFamily());
      StyleConstants.setFontSize(a, curFont.getSize());
      dlg.setAttributes(a);
      dlg.setLocationRelativeTo(null);
      dlg.setVisible(true);

      if(dlg.getOption() == JOptionPane.CANCEL_OPTION) {
          return null;
      }

      SimpleAttributeSet attrs = (SimpleAttributeSet) dlg.getAttributes();

      int style = Font.PLAIN;

      if((Boolean) attrs.getAttribute(StyleConstants.Italic)) {
          style = style | Font.ITALIC;
      }
      if((Boolean) attrs.getAttribute(StyleConstants.Bold)) {
          style = style | Font.BOLD;
      }

      Font f = new Font((String) attrs.getAttribute(StyleConstants.Family), style,
          (Integer) attrs.getAttribute(StyleConstants.Size));

      return f;
    }

    protected static Color getColor(Color initialColor) {
        return JColorChooser.showDialog(winDemo, "Choose Color", initialColor);
    }
}
