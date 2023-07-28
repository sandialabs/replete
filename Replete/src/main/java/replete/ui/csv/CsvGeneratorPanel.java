package replete.ui.csv;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.tree.TreePath;

import replete.progress.PercentProgressMessage;
import replete.text.StringUtil;
import replete.ui.button.RButton;
import replete.ui.button.RCheckBox;
import replete.ui.csv.nodes.NodeColumn;
import replete.ui.csv.nodes.NodeType;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.list.RList;
import replete.ui.tabbed.RNotifPanel;
import replete.ui.text.RLabel;
import replete.ui.text.RTextField;
import replete.ui.tree.NodeSimpleLabel;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;
import replete.ui.tree.RTreePath;
import replete.ui.windows.Dialogs;
import replete.ui.worker.RWorker;
import replete.xstream.XStreamWrapper;

public class CsvGeneratorPanel extends RNotifPanel {


    ////////////
    // FIELDS //
    ////////////

    private JFrame parent;

    private RButton btnAddCsv;
    private RButton btnEditCsv;
    private RButton btnRemoveCsv;
    private RButton btnExportCsv;
    private RButton btnLoadTemplate;
    private RButton btnSaveTemplate;
    private RButton btnUpCol;
    private RButton btnDownCol;
    private RButton btnAddCol;
    private RButton btnRemoveCol;
    private RCheckBox chkHeaderRow;

    private RLabel lblEditor;

    private RList<CsvModel> lstCsvs;
    private DefaultListModel<CsvModel> mdlCsvs = new DefaultListModel<>();
    private RTree treAvailCols;
    private RTreeNode nRootAvail;
    private RList<AbstractCommonCsvColumn> lstSelCols;
    private DefaultListModel<AbstractCommonCsvColumn> mdlSelCols = new DefaultListModel<>();

    private RTextField txtColName;
    private RCheckBox chkColSort;
    private RCheckBox chkColImpt;

    private List<AbstractCommonCsvColumn> allColumns = new ArrayList<>();
    private CsvExporter exporter;
    private CsvModel selCsv;
    private CsvColumnFactory columnFactory;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public CsvGeneratorPanel(JFrame parent, CsvColumnFactory columnFactory, CsvExporter exporter) {
        this.parent = parent;
        this.exporter = exporter;
        this.columnFactory = columnFactory;
        allColumns.addAll(this.columnFactory.createAllColumns());

        Lay.BLtg(this,
            "W", Lay.BL(
                "N", Lay.lb("CSV List", "italic"),
                "C", Lay.sp(
                    lstCsvs = Lay.lst(mdlCsvs, "seltype=single"),
                    "prefw=200, maxw=200"
                ),
                "E", Lay.BxL("Y",
                    Lay.p(
                        btnAddCsv = Lay.btn(CommonConcepts.ADD, 2, "ttt=Add-CSV..."),
                        "eb=5l,alignx=0.5,maxh=25"
                    ),
                    Lay.p(
                        btnEditCsv = Lay.btn(CommonConcepts.EDIT, 2, "ttt=Edit-CSV...,enabled=false"),
                        "eb=5tl,alignx=0.5,maxh=30"
                    ),
                    Lay.p(
                        btnRemoveCsv = Lay.btn(CommonConcepts.DELETE, 2, "ttt=Remove-CSV,enabled=false"),
                        "eb=5tl,alignx=0.5,maxh=30"
                    )
                ),
                "eb=5tlb"
            ),
            "C", Lay.BL(
                "C", Lay.BL(
                    "C", Lay.BL(
                        "N", Lay.BL(
                            "W", lblEditor = Lay.lb("", "fg=white,eb=5l"),
                            "E", Lay.FL("R",
                                Lay.lb("Template:", "fg=white"),
                                btnLoadTemplate = Lay.btn("&Load", CommonConcepts.OPEN, "enabled=false"),
                                btnSaveTemplate = Lay.btn("&Save", CommonConcepts.SAVE, "enabled=false")
                            ),
                            "bg=100,chtransp"
                        ),
                        "C", Lay.BxL("X",
                            Lay.BL(
                                "N", Lay.lb("Available Columns", "italic"),
                                "C", Lay.sp(
                                    treAvailCols = Lay.tr(),
                                    "prefw=300, maxw=300, prefh=200, maxh=200"
                                ),
                                "S", Lay.FL("L",
                                    chkHeaderRow = Lay.chk("Include Header Row"),
                                    "nogap"
                                )
                            ),
                            Lay.GBL(
                                Lay.BxL("Y",
                                    Lay.p(
                                        btnAddCol = Lay.btn(CommonConcepts.NEXT, 4),
                                        "alignx=0.5"
                                    ),
                                    Box.createVerticalStrut(5),
                                    Lay.p(
                                        btnRemoveCol = Lay.btn(CommonConcepts.PREV, 4),
                                        "alignx=0.5"
                                    ),
                                    "eb=5lr"
                                )
                            ),
                            Lay.BL(
                                "N", Lay.lb("Selected Columns", "italic"),
                                "C", Lay.sp(
                                    lstSelCols = Lay.lst(mdlSelCols, "empty"),
                                    "prefh=200, maxh=200"
                                ),
                                "S", Lay.BxL("Y",
                                    Lay.FL("R",
                                        Lay.lb("Reorder:", "eb=5r"),
                                        Lay.p(btnUpCol = Lay.btn(CommonConcepts.MOVE_UP, 2), "eb=5r"),
                                        btnDownCol = Lay.btn(CommonConcepts.MOVE_DOWN, 2),
                                        "nogap,eb=5tb"
                                    ),
                                    Lay.FL("L", Lay.lb("Column Configuration", "italic"), "nogap"),
                                    Lay.BxL(
                                        Lay.FL("L",
                                            Lay.lb("Name:", "prefw=100, maxw=200"),
                                            txtColName = Lay.tx("", "selectall,prefw=100,maxw=150")
                                        ),
                                        Lay.FL("L",
                                            Lay.lb("Sort?", "prefw=100, maxw=200"),
                                            chkColSort = Lay.chk()
                                        ),
                                        Lay.FL("L",
                                            Lay.lb("Important?", "prefw=100, maxw=200"),
                                            chkColImpt = Lay.chk()
                                        ),
                                        "mb=[1,150]"
                                    ),
                                    "eb=5b, prefw=400"
                                )
                            ),
                            "eb=5lrt"
                        )
                    ),
                    "mb=[1,150]"
                ),
                "S", Lay.FL("R",
                    btnExportCsv = Lay.btn("Generate CSVs", CommonConcepts.PLAY, 2, "enabled=false"),
                    "nogap,eb=5tl,alignx=0.5,maxh=30"
                ),
                "eb=5"
            )
        );

        initListeners();

        chkHeaderRow.setSelected(true);
        lstSelCols.setCellRenderer(new CSVListCellRenderer());
        updateFromCsvSelection();
    }

    private void initListeners() {
        btnAddCsv.addActionListener(e -> {
            CsvGeneratorNameDialog dlg = new CsvGeneratorNameDialog(parent, "Add New CSV", "");
            dlg.setVisible(true);
            if(dlg.getResult() == CsvGeneratorNameDialog.OK) {
                String csvName = StringUtil.ensureEndsWith(dlg.getFileName(), ".csv");
                mdlCsvs.addElement(new CsvModel(csvName));
                lstCsvs.setSelectedIndex(mdlCsvs.size() - 1);
            }
        });

        btnEditCsv.addActionListener(e -> {
            CsvModel model = mdlCsvs.getElementAt(lstCsvs.getSelectedIndex());
            CsvGeneratorNameDialog dlg = new CsvGeneratorNameDialog(parent, "Edit New CSV", model.getName());
            dlg.setVisible(true);
            if(dlg.getResult() == CsvGeneratorNameDialog.OK) {
                String csvName = StringUtil.ensureEndsWith(dlg.getFileName(), ".csv");
                model.setName(csvName);
                lstCsvs.repaint();
            }
        });

        btnRemoveCsv.addActionListener(e -> {
            int sel = lstCsvs.getSelectedIndex();
            mdlCsvs.remove(sel);
            if(sel >= mdlCsvs.size()) {
                sel--;
            }
            lstCsvs.setSelectedIndex(sel);
        });

        btnExportCsv.addActionListener(e -> exportCSVs());

        btnSaveTemplate.addActionListener(e -> {
            RFileChooser chooser = RFileChooser.getChooser("Save Template");
            RFilterBuilder builder = new RFilterBuilder(chooser, false);
            builder.append("XML File (*.xml)", "xml");
            if(chooser.showSave(parent)) {
                try {
                    File toFile = chooser.getSelectedFileResolved();
//for(AbstractCommonCsvColumn column : selCsv.getColumns()) {
//    System.out.println("Save: " + column.getName() + " / " + column.getOverriddenName() + " / " + column.isImportantColumn());
//}
                    XStreamWrapper.writeToFile(selCsv.getColumns(), toFile);
                } catch(Exception ex) {
                    Dialogs.showDetails(
                        parent, "An error occurred writing the template to file.",
                        "Save Template Error", ex
                    );
                }
            }
        });

        btnLoadTemplate.addActionListener(e -> {
            RFileChooser chooser = RFileChooser.getChooser("Load Template");
            RFilterBuilder builder = new RFilterBuilder(chooser, false);
            builder.append("XML File (*.xml)", "xml");
            if(chooser.showOpen(parent)) {
                try {
                    File fromFile = chooser.getSelectedFileResolved();
                    List<AbstractCommonCsvColumn> columns = XStreamWrapper.loadTarget(fromFile);
//                    selCsv.setColumns(columns);
//for(AbstractCommonCsvColumn column : selCsv.getColumns()) {
//    System.out.println("Load: " + column.getName() + " / " + column.getOverriddenName() + " / " + column.isImportantColumn());
//}

                    // First, clear out previously selected columns in the list (easier to start from scratch).
                    for(AbstractCommonCsvColumn column : lstSelCols.getSelectedValuesList()) {
                        removeColumnFromListToTree(column);
                    }

                    // Next, load all the columns from the template into the list, using the tree.
                    boolean foundColumnInTree = false;
                    for(AbstractCommonCsvColumn column : columns) {
                        for(int i = 0; i < nRootAvail.getChildCount(); i++) {
                            if(foundColumnInTree) {
                                break;
                            }
                            RTreeNode nType = nRootAvail.getRChildAt(i);
                            for(int j = 0; j < nType.getChildCount(); j++) {
                                RTreeNode nColumn = nType.getRChildAt(j);
                                NodeColumn uColumn = nColumn.get();
                                AbstractCommonCsvColumn columnObj = uColumn.getColumn();
                                if(columnObj.getName().equals(column.getName())) {
                                    addColumnToList(column);
                                    TreePath path = new TreePath(nColumn.getPath());
                                    removeColumnFromList(new TreePath[]{path});
                                    foundColumnInTree = true;
                                    break;
                                }
                            }
                        }
                        foundColumnInTree = false;
                    }

                    if(!mdlSelCols.isEmpty()) {
                        lstSelCols.selectFirst();
                    }

                } catch(Exception ex) {
                    Dialogs.showDetails(
                        parent, "An error occurred loading the template from file.",
                        "Load Template Error", ex
                    );
                }
            }
        });

        lstCsvs.addSelectionListener(e -> updateFromCsvSelection());
        treAvailCols.addSelectionListener(e -> updateFromAvailColumnSelection());
        lstSelCols.addSelectionListener(e -> updateFromSelColumnSelection());

        btnAddCol.addActionListener(e -> addColumnFromTreeToList(
            treAvailCols.getSelectionModel().getSelectionPaths()));

        btnRemoveCol.addActionListener(e -> {
            for(AbstractCommonCsvColumn thisSelCol : lstSelCols.getSelectedValuesList()) {
                removeColumnFromListToTree(thisSelCol);
            }

            btnUpCol.setEnabled(false);
            btnDownCol.setEnabled(false);
            btnRemoveCol.setEnabled(false);

            updateColumnList(selCsv.getColumns());
            lstSelCols.setSelectedIndex(-1);

            treAvailCols.repaint();
            treAvailCols.expandAll();
        });

        chkHeaderRow.addActionListener(e -> selCsv.setIncludeHeader(chkHeaderRow.isSelected()));

        chkColSort.addActionListener(e -> {
            for(int i = 0; i < mdlSelCols.getSize(); i++) {
                AbstractCommonCsvColumn column = mdlSelCols.getElementAt(i);
                column.setSortedColumn(false);       // Clear out previous sort column
            }
            AbstractCommonCsvColumn selColumn = lstSelCols.getSelectedValue();
            selColumn.setSortedColumn(chkColSort.isSelected());
        });

        chkColImpt.addActionListener(e -> {
            AbstractCommonCsvColumn selColumn = lstSelCols.getSelectedValue();
            selColumn.setImportantColumn(chkColImpt.isSelected());
        });

        btnUpCol.addActionListener(e -> {
            AbstractCommonCsvColumn selColumn = lstSelCols.getSelectedValue();
            selCsv.moveColumnUp(selColumn);
            updateColumnList(selCsv.getColumns());
            lstSelCols.setSelectedIndex(mdlSelCols.indexOf(selColumn));
        });
        btnDownCol.addActionListener(e -> {
            AbstractCommonCsvColumn selColumn = lstSelCols.getSelectedValue();
            selCsv.moveColumnDown(selColumn);
            updateColumnList(selCsv.getColumns());
            lstSelCols.setSelectedIndex(mdlSelCols.indexOf(selColumn));
        });

        txtColName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                updateOverriddenName();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                updateOverriddenName();
            }
        });
    }

    private void updateOverriddenName() {
        AbstractCommonCsvColumn selColumn = lstSelCols.getSelectedValue();
        if(selColumn != null) {
            selColumn.setOverriddenName(txtColName.getText());
            lstSelCols.repaint();
        }
    }

    private void updateFromCsvSelection() {
        boolean sel = lstCsvs.getSelectedIndex() != -1;
        btnEditCsv.setEnabled(sel);
        btnRemoveCsv.setEnabled(sel);
        btnExportCsv.setEnabled(sel);
        btnLoadTemplate.setEnabled(sel);
        btnSaveTemplate.setEnabled(sel);
        btnAddCol.setEnabled(sel);
        chkHeaderRow.setEnabled(sel);

        if(sel) {
            selCsv = lstCsvs.getSelectedValue();
            lblEditor.setText(selCsv.getName());
            updateColumnList(selCsv.getColumns());
            List<AbstractCommonCsvColumn> startingTreeData = new ArrayList<>();
            startingTreeData.addAll(allColumns);
            startingTreeData.removeAll(selCsv.getColumns());
            initTreeModel(startingTreeData);
            treAvailCols.setModel(nRootAvail);
            treAvailCols.expandAll();
        } else {
            lblEditor.setText("");
            treAvailCols.clear();
            mdlSelCols.clear();
        }

        updateFromAvailColumnSelection();
        updateFromSelColumnSelection();
    }

    private void updateFromAvailColumnSelection() {
        TreePath path = treAvailCols.getSelectionPath();
        RTreePath rPath = path == null ? null : new RTreePath(path);
        boolean sel = rPath != null && rPath.getObject() instanceof NodeColumn;
        btnAddCol.setEnabled(sel);
    }

    private void updateFromSelColumnSelection() {
        boolean sel = lstSelCols.getSelectedIndex() != -1;
        btnUpCol.setEnabled(sel);
        btnDownCol.setEnabled(sel);
        btnRemoveCol.setEnabled(sel);
        txtColName.setEnabled(sel);
        chkColSort.setEnabled(sel);
        chkColImpt.setEnabled(sel);

        if(sel) {
            AbstractCommonCsvColumn selColumn = lstSelCols.getSelectedValue();
            txtColName.setText(selColumn.getResolvedName());
            chkColSort.setEnabled(selColumn.isSortable());
            chkColSort.setSelected(selColumn.isSortedColumn());
            chkColImpt.setSelected(selColumn.isImportantColumn());
        } else {
            txtColName.clear();
            chkColSort.setSelected(false);
            chkColImpt.setSelected(false);
        }
    }

    private void addColumnFromTreeToList(TreePath[] paths) {
        for(TreePath path : paths) {
            RTreeNode nSel = (RTreeNode) path.getLastPathComponent();
            Object selObj = nSel.getUserObject();
            if(selObj instanceof NodeColumn) {
                NodeColumn uColumn = nSel.get();
                AbstractCommonCsvColumn selCol = uColumn.getColumn();
                selCsv.addColumn(columnFactory.createColumn(selCol.getInfo()));
                updateColumnList(selCsv.getColumns());
                treAvailCols.getModel().removeNodeFromParent(nSel);
            }
        }
    }

    private void addColumnToList(AbstractCommonCsvColumn column) {
        selCsv.addColumn(column);
        updateColumnList(selCsv.getColumns());
    }

    private void removeColumnFromListToTree(AbstractCommonCsvColumn column) {
        String desc = column.getType().getDescription();
        for(RTreeNode nType : nRootAvail) {
            NodeType uType = nType.get();
            if(uType.getType().getDescription().equals(desc)) {
                selCsv.removeColumn(column);

                // Clear any information that was configured
                // on the column object while it was in the
                // selected list.
                column.setOverriddenName("");
                column.setSortedColumn(false);
                column.setImportantColumn(false);

                RTreeNode nColumn = nType.add(new NodeColumn(column));
                int index = findAlphabeticalIndexOfColumn(column, nType);
                treAvailCols.getModel().insertNodeInto(nColumn, nType, index);
            }
        }
    }

    private void removeColumnFromList(TreePath[] paths) {
        for(TreePath path : paths) {
            RTreeNode nSel = (RTreeNode) path.getLastPathComponent();
            Object selObj = nSel.getUserObject();
            if(selObj instanceof NodeColumn) {
                NodeColumn uColumn = nSel.get();
                treAvailCols.getModel().removeNodeFromParent(nSel);
            }
        }
    }

    private void updateColumnList(List<AbstractCommonCsvColumn> columns) {
        mdlSelCols.clear();
        for(AbstractCommonCsvColumn column : columns) {
            mdlSelCols.addElement(column);
        }
    }
    private void updateSelCsvFromSelColumns() {
        selCsv.getColumns().clear();
        for(AbstractCommonCsvColumn column : lstSelCols.getSelectedValuesList()) {
            selCsv.getColumns().add(column);
        }
    }

    private void initTreeModel(List<AbstractCommonCsvColumn> columns) {
        nRootAvail = new RTreeNode(new NodeSimpleLabel());
        for(CsvColumnType type : columnFactory.getAvailableColumnTypes()) {
            if(!columnTypeExists(type, columns)) {
                continue;
            }
            RTreeNode nType = nRootAvail.add(new NodeType(type));
            for(AbstractCommonCsvColumn column : columns) {
                if(column.getType() == type) {
                    nType.insert(new NodeColumn(column),
                        findAlphabeticalIndexOfColumn(column, nType));
                }
            }
        }
    }

    private boolean columnTypeExists(CsvColumnType type, List<AbstractCommonCsvColumn> columns) {
        for(AbstractCommonCsvColumn column : columns) {
            if(column.getType() == type) {
                return true;
            }
        }
        return false;
    }

    private int findAlphabeticalIndexOfColumn(AbstractCommonCsvColumn newColumn, RTreeNode nType) {
        for(int i = 0; i < nType.getChildCount(); i++) {
            RTreeNode nColumn = nType.getRChildAt(i);
            NodeColumn uColumn = nColumn.get();
            AbstractCommonCsvColumn column = uColumn.getColumn();
            int compare = newColumn.getName().compareTo(column.getName());
            if(compare <= 0) {
                return i;
            }
        }
        return nType.getChildCount() == 0 ? 0 : nType.getChildCount();
    }


    //////////////////////////
    ///   CELL RENDERERS   ///
    //////////////////////////

    private class CSVListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            AbstractCommonCsvColumn column = (AbstractCommonCsvColumn) value;
            lbl.setText(column.getResolvedName());
            lbl.setToolTipText(column.getDescription());
            return lbl;
        }
    }


    ///////////////////////////////////
    ///   EXPORT / PROGRESS LOGIC   ///
    ///////////////////////////////////

    private String preCheck() {
        // Are there any duplicate CSV column names?
        for(CsvModel thisCsvModel : lstCsvs.getSelectedValuesList()) {
            for(AbstractCommonCsvColumn column1 : thisCsvModel.getColumns()) {
                for(AbstractCommonCsvColumn column2 : thisCsvModel.getColumns()) {
                    // Are the overridden names identical, AND they aren't the same column?
                    if(column1.getResolvedName().equals(column2.getResolvedName()) &&
                            !column1.getName().equals(column2.getName())) {
                        return
                            "Two or more columns have the same exported name of \"" + column1.getResolvedName() +
                            "\".  Please provide unique column names before exporting.";
                    }
                }
            }
        }
        return "";
    }

    private void exportCSVs() {
        String preCheckMessage = preCheck();
        if(!StringUtil.isBlank(preCheckMessage)) {
            Dialogs.showError(parent, preCheckMessage, "Pre-Check CSV Export Error");
            return;
        }

        // Provide a warning message if they're sorting.
        boolean isSortingOn = false;
        for(AbstractCommonCsvColumn column : lstSelCols.getSelectedValuesList()) {
            if(column.isSortedColumn()) {
                isSortingOn = true;
                break;
            }
        }
        if(isSortingOn) {
            boolean continueWithSort = Dialogs.showConfirmWarning(parent,
                "Sorting is an extra step that" +
                " can take a long time, depending on the size of your data set, and the complexity" +
                " of the calculation for the column you are sorting on.  Are you sure you want to" +
                " proceed?", "Sorting"
            );
            if(!continueWithSort) {
                return;
            }
        }

        RWorker<List<CsvModel>, Boolean> worker = new RWorker<List<CsvModel>, Boolean>() {
            @Override
            protected List<CsvModel> gather() {
                List<CsvModel> csvModels = new ArrayList<>();
                for(int i = 0; i < mdlCsvs.getSize(); i++) {
                    CsvModel csv = mdlCsvs.getElementAt(i);
                    csvModels.add(csv);
                }
                return csvModels;
            }

            @Override
            protected Boolean background(List<CsvModel> csvModels) throws Exception {
                publishProgress(new PercentProgressMessage("Preparing CSV exporter...", 0));

                boolean allSuccess = true;
                for(int i = 0; i < csvModels.size(); i++) {
                    CsvModel csv = csvModels.get(i);
                    try {
                        if(!exporter.export(csv)) {
                            allSuccess = false;
                        }
                    } catch(Exception e) {
                        Dialogs.showDetails(parent, "An error occurred generating the CSV.", "Error", e);
                    }

                    int progressSoFar = (int) ((double) i / mdlCsvs.getSize());
                    publishProgress(new PercentProgressMessage("Exporting CSVs...", progressSoFar));
                    checkPauseAndStop();
                }
                return allSuccess;
            }

            @Override
            protected void complete() {
                try {
                    Boolean overallResult = getResult();
                    if(!overallResult) {
                        Dialogs.showWarning(parent, "An error occurred generating one of the CSVs.", "Error");
                    } else {
                        String successMsg = exporter.getSuccessMessage();
                        if(!StringUtil.isBlank(successMsg)) {
                            Dialogs.showMessage(parent, successMsg);
                        }
                    }
                } catch (Exception e) {
                    Dialogs.showDetails(parent, "An error occurred generating the CSV.", "Error", e);
                }
            }
        };

        addTaskAndExecuteFg("Exporting CSVs", worker);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        CsvExporter exporter = new CsvExporter() {
            @Override
            public String getSuccessMessage() {
                return null;
            }
            @Override
            public boolean export(CsvModel csv) throws Exception {
                return false;
            }
        };
        JFrame fra = Lay.fr("CSV Panel", "notif");
        Lay.BLtg(fra,
            "C", new CsvGeneratorPanel(fra, new CsvColumnFactory() {

                @Override
                public AbstractCommonCsvColumn createColumn(CsvColumnInfo info) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public List<AbstractCommonCsvColumn> createAllColumns() {
                    List<AbstractCommonCsvColumn> columns = new ArrayList<>();
                    for(int i = 0; i < 5; i++) {
                        final int j = i;
                        CsvColumnInfo info = new CsvColumnInfo() {
                            @Override
                            public String getName() {
                                return "1Column" + j;
                            }
                            @Override
                            public String getDescription() {
                                return "1Description" + j;
                            }
                        };
                        columns.add(new AbstractCommonCsvColumn(getAvailableColumnTypes().get(0), info) {
                            @Override
                            public String getCellData(Object source) {
                                return "data";
                            }
                        });
                    }
                    for(int i = 0; i < 5; i++) {
                        final int j = i;

                        CsvColumnInfo info = new CsvColumnInfo() {
                            @Override
                            public String getName() {
                                return "2Column" + j;
                            }
                            @Override
                            public String getDescription() {
                                return "2Description" + j;
                            }
                        };
                        columns.add( new AbstractCommonCsvColumn(getAvailableColumnTypes().get(0), info) {
                            @Override
                            public String getCellData(Object source) {
                                return "data";
                            }
                        });
                    }
                    for(int i = 0; i < 5; i++) {
                        final int j = i;

                        CsvColumnInfo info = new CsvColumnInfo() {
                            @Override
                            public String getName() {
                                return "3Column" + j;
                            }
                            @Override
                            public String getDescription() {
                                return "3Description" + j;
                            }
                        };
                        columns.add( new AbstractCommonCsvColumn(getAvailableColumnTypes().get(0), info) {
                            @Override
                            public String getCellData(Object source) {
                                return "data";
                            }
                        });
                    }
                    return columns;
                }

                @Override
                public List<CsvColumnType> getAvailableColumnTypes() {
                    CsvColumnType type = new CsvColumnType() {
                        @Override
                        public String getDescription() {
                            return "type";
                        }

                        @Override
                        public ImageModelConcept getParentIcon() {
                            return RepleteImageModel.CSV_TYPE_OTHER;
                        }

                        @Override
                        public ImageModelConcept getChildIcon() {
                            return RepleteImageModel.CSV_TYPE_OTHER;
                        }
                    };
                    List<CsvColumnType> typeList = new ArrayList<>();
                    typeList.add(type);
                    return typeList;
                }

            }, exporter),
            "size=[800,600],center,visible"
        );
    }
}
