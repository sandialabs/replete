package finio.ui.bytes;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.platform.exts.view.treeview.TreeView;
import finio.platform.exts.view.treeview.ui.FTreePanel;
import finio.plugins.FinioPluginManager;
import finio.plugins.platform.FinioPlugin;
import finio.ui.actions.FActionMapBuilder;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.SelectionContext;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;
import replete.collections.Pair;
import replete.event.ChangeNotifier;
import replete.io.FileUtil;
import replete.numbers.NumUtil;
import replete.plugins.PluginManager;
import replete.text.StringUtil;
import replete.ui.combo.RComboBox;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.text.validating.ValidatingTextField;
import replete.ui.text.validating.Validator;
import replete.ui.uiaction.UIActionMenuBar;
import replete.ui.windows.notifications.NotificationFrame;


// TODO
//  1. Softer selected color
//  2. Why doesn't lasso work in all files when multiple on?
//  3. Figure out why FTreePanel not refreshing?
//  4. Get elapsed time T displayed somewhere
//  6. Finish L Ruler
//  7. Change pink
//  8. Allow lasso to work on actual rectangles, not just background
//  9. How make a non-editable tree?
//  10. Have tree show the source
//  11. How make double-click in tree not edit cell but rather open tab instead of on select...
//  12. How to add custom actions to the tree so when you right-click you can select "Open Draw Panel..."
//      not just specific to byte[] but specific to byte[] AND our usage of it in this specific App.
//  13. Fix Scroll Pane
//  14. Paint binary dots
//  15. Clean up visuals around an inner rect
//  16. Toy with inner margins so can potentially decorate with small icons
//  17. Add concept of minimum column width
//     a. Would be nice to some how get col width really small for mimic-ing typical "hex" editors and for getting ASCII
//        letters really close to each other
//     b. Would be nice to be able to display multiple modes in each inner some how.
//     c. Need a "pre-pass" to calculate the max column width across ALL possible inner's.
//  18. Could the CENTER tabbed pane be somehow a generic Finio tabbed pane that is representing a
//      NonTerminal where the value is then visualized by a DrawPanel generic Object visualizer?
//  19. Need to get a detail panel for a single byte to show stuff when selected
//  20. Single click selects a single byte.
//  21. Need to have configurable "column gaps" / "row gaps" like a typical hex editor (usually between 4 or 8 byte divisions)
//  22. Lasso needs to be on top of byte boxes
//  23. Lasso can be started on rulers
//  24. Need to have a detail area for the streams - for calculated encodings / natural languages / data length / endianness, data histograms
//  25. Need options to HINT to the app about a byte stream like telling it that it is little endian / big endian
//  26. Need each draw panel to get its own Orientation/ViewMode settings - right now they'are all getting tied together.
//  27. Edit Configurable patterns that you can ask the software to always be looking for, highlighting in draw panel
//      and showing in detail panel if it finds.  Regex type things would be nice.
//  28. Need abstraction for byte[] data so can start annotating byte ranges and supporting delete, etc in an efficient way.
//  29. Optimizations for large byte streams.
//  30. Figure out why process always hangs

public class ByteStreamsFrame extends NotificationFrame {


    ////////////
    // FIELDS //
    ////////////

    public static final String MAX_NUM = "MAX";
    private List<ByteStream> streams = new ArrayList<>();
    private NonTerminal M = new FMap();
    private RTabbedPane tabs;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ByteStreamsFrame() {
        super("Byte Stream Inspector", FinioImageModel.BYTE_STR_LOGO);

        Pair<WorldPanel, FTreePanel> pair = constructStreamList();

        Lay.BLtg(this,
            "C", Lay.SPL(
                Lay.sp(pair.getValue1()),
                tabs = Lay.TBL("dc"),
                "bg=100"
            ),
            "size=[700,700],center"
        );

        JPanel pnlClose = Lay.FL(
            Lay.btn("&Close", CommonConcepts.CANCEL, "closer")
        );
        setShowStatusBar(true);
        getStatusBar().setRightComponent(pnlClose);

        UIActionMenuBar menuBar = new UIActionMenuBar(new ByteStreamsActionMap(this));
        setJMenuBar(menuBar);

        pair.getValue2().addSelectedValuesListener(e -> {
            SelectionContext[] Cs = pair.getValue2().getSelectionValues();
            if(Cs != null) {
                for(SelectionContext C : Cs) {
                    ByteStream stream = (ByteStream) C.getV();
                    byte[] data = stream.getData();
                    Object key = data.hashCode();
                    int index = tabs.indexOfTabByKey(key);
                    if(index == -1) {
                        System.out.println("Loaded Bytes: " + FileUtil.getReadableSizeString(data.length));
                        ByteStreamConfigPanel pnlConfig = new ByteStreamConfigPanel();
                        ByteStreamDrawPanel pnlDraw = new ByteStreamDrawPanel();
                        pnlDraw.setStream(stream);
                        pnlConfig.addConfigUpdatedListener(evt -> {
                            pnlDraw.setOrientation(pnlConfig.cboOrientation.getSelected());
                            pnlDraw.setViewMode(pnlConfig.cboViewMode.getSelected());
                            try {
                                pnlDraw.setDesiredRows(cvrt(pnlConfig.txtRows));
                            } catch(Exception ex) {
                            }
                            try {
                                pnlDraw.setDesiredColumns(cvrt(pnlConfig.txtCols));
                            } catch(Exception ex) {
                            }
                        });
                        pnlConfig.cboViewMode.setSelectedItem(pnlDraw.getViewMode());
                        pnlConfig.cboOrientation.setSelectedItem(pnlDraw.getOrientation());
                        pnlConfig.txtRows.setText(cvrt(pnlDraw.getDesiredRows()));
                        pnlConfig.txtCols.setText(cvrt(pnlDraw.getDesiredCols()));
                        pnlDraw.addStateUpdatedListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                updateDetails(pnlDraw, pnlConfig);
                            }
                        });
                        updateDetails(pnlDraw, pnlConfig);
                        tabs.addTab(C.getK().toString(),
                            CommonConcepts.BINARY,
                            Lay.BL(
                                "N", pnlConfig,
                                "C", Lay.sp(pnlDraw)
                            ),
                            null, key, true
                        );
                    } else {
                        tabs.setSelectedTab(index);
                    }
                }
            }
        });
    }

    private Pair<WorldPanel, FTreePanel> constructStreamList() {
        PluginManager.initialize(FinioPlugin.class);
        FinioPluginManager.initialize();

        AppContext ac = new AppContext();
        ac.getConfig().setNodeInfoEnabled(false);
        FActionMapBuilder builder = new FActionMapBuilder();
        builder.build(ac);
        WorldContext wc = new WorldContext(ac).setW(M);
        ac.addWorld(wc);
        WorldPanel pnlWorld = wc.getWorldPanel();
//        pnlWorld.setExpandSingleView(true);
        FTreePanel pnlTree = new FTreePanel(ac, wc, null, M, new TreeView());
        pnlTree.setShowWorkingScope(false);
        pnlTree.setRootVisible(false);
        pnlWorld.addViewPanel(pnlTree);
        pnlWorld.setPreferredSize(new Dimension(150, 1));
        return new Pair<>(pnlWorld, pnlTree);
    }

    private void updateDetails(ByteStreamDrawPanel pnlDraw, ByteStreamConfigPanel pnlConfig) {
        pnlConfig.lblDetails.setText(
            "  Details: [RR: " + pnlDraw.getResolvedRows() + ", RC: " +
                pnlDraw.getResolvedCols() + "] " +
                pnlDraw.getColWidth() + "x" + pnlDraw.getRowHeight() + " ");
    }

    private int cvrt(ValidatingTextField txtRows) {
        String text = txtRows.getText();
        text = text.trim().toUpperCase();
//        txtRows.setTextSuppressed(text);

        if(text.isEmpty()) {
            return 0;
        } else if(text.equalsIgnoreCase(MAX_NUM)) {
            return Integer.MAX_VALUE;
        }

        return NumUtil.i(text);
    }

    private String cvrt(int desired) {
        if(desired == Integer.MAX_VALUE) {
            return MAX_NUM;
        } else if(desired <= 0) {
            return "";
        }
        return "" + desired;
    }


    //////////
    // MISC //
    //////////

    public void addStream(byte[] data, File file, URL url, String str) {
        Source source;
        if(file != null) {
            source = new FileSource(file);
        } else if(url != null) {
            source = new UrlSource(url);
        } else if(str != null) {
            source = new StringSource();
        } else {
            source = new ByteSource();
        }
        ByteStream stream = new ByteStream(data, source);
        streams.add(stream);
        M.put("Stream #" + streams.size(), stream);
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class ByteStreamConfigPanel extends RPanel {


        ////////////
        // FIELDS //
        ////////////

        private RComboBox<Orientation> cboOrientation;
        private RComboBox<ViewMode> cboViewMode;
        private ValidatingTextField txtRows;
        private ValidatingTextField txtCols;
        private JLabel lblDetails;


        /////////////////
        // CONSTRUCTOR //
        /////////////////

        public ByteStreamConfigPanel() {
            Validator val = (cmp, text) -> {
                text = text.trim();
                return
                    StringUtil.isBlank(text) ||
                    text.equalsIgnoreCase(MAX_NUM) ||
                    (NumUtil.isInt(text) && NumUtil.i(text) >= 0);
            };

            Lay.BLtg(this,
                "W", Lay.FL("L",
                    cboViewMode    = Lay.cb((Object[]) ViewMode.values()),
                    cboOrientation = Lay.cb((Object[]) Orientation.values()),
                    Lay.lb("Rows: "), txtRows = Lay.tx("", 6, val, "selectall,center,size=14"),
                    Lay.lb("Cols: "), txtCols = Lay.tx("", 6, val, "selectall,center,size=14")
                ),
                "E", lblDetails = Lay.lb()
            );

            ActionListener listener = e -> fireConfigUpdatedNotifier();
            cboOrientation.addActionListener(listener);
            cboViewMode.addActionListener(listener);
            txtRows.addActionListener(listener);
            txtCols.addActionListener(listener);

            ChangeListener chListener = e -> fireConfigUpdatedNotifier();
            txtRows.addValidUnvalidatableTimeoutListener(chListener);
            txtCols.addValidUnvalidatableTimeoutListener(chListener);
        }


        //////////////
        // NOTIFIER //
        //////////////

        private ChangeNotifier configUpdatedNotifier = new ChangeNotifier(this);
        public void addConfigUpdatedListener(ChangeListener listener) {
            configUpdatedNotifier.addListener(listener);
        }
        private void fireConfigUpdatedNotifier() {
            configUpdatedNotifier.fireStateChanged();
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        ByteStreamsFrame frame = new ByteStreamsFrame();
        frame.setVisible(true);
    }
}