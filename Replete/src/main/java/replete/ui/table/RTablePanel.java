package replete.ui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import replete.event.ChangeNotifier;
import replete.text.StringUtil;
import replete.ui.GuiUtil;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.panels.SelectionState;
import replete.ui.sp.RScrollPane;
import replete.ui.text.validating.ValidatingTextField;
import replete.ui.text.validating.Validator;
import replete.ui.windows.Dialogs;

public class RTablePanel extends RPanel {


    ////////////
    // FIELDS //
    ////////////

    private static final Color ARROW_FILL_LIGHT   = new Color(240, 240, 240);
    private static final Color ARROW_BORDER_LIGHT = new Color(220, 220, 220);
    private static final Color ARROW_FILL_DARK    = new Color(100, 100, 100);
    private static final Color ARROW_BORDER_DARK  = new Color( 20,  20,  20);

    public static final int     DEFAULT_POSITION = SwingConstants.SOUTH;
    public static final boolean DEFAULT_EXPANDED = false;

    private Color arrowFill   = ARROW_FILL_LIGHT;
    private Color arrowBorder = ARROW_BORDER_LIGHT;

    private JLabel lblTable;
    private JLabel lblFilter;
    private ValidatingTextField txtFilter;
    private RTable tbl;
    private RScrollPane scr;
    private String tableLabel;
    private JLabel lblRowCounts;
    private JPanel pnlOptions;
    private JPanel pnlOptionsReal;
    private JPanel pnlOptionsTray;
    private boolean optionsExpanded;
    private int optionsPosition;
    private String lastFilterText = "";

    private TableModel model;
    private RowSorter rowSorter;

    private Color selColor = Lay.clr("FAFF82");


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTablePanel(RTable tbl) {
        this(tbl, DEFAULT_POSITION, DEFAULT_EXPANDED);
    }
    public RTablePanel(RTable tbl, int optionsPosition) {
        this(tbl, optionsPosition, DEFAULT_EXPANDED);
    }
    public RTablePanel(RTable tbl, boolean optionsExpanded) {
        this(tbl, DEFAULT_POSITION, optionsExpanded);
    }
    public RTablePanel(RTable tbl, int optionsPosition, boolean optionsExpanded) {
        this.tbl = tbl;
        this.optionsPosition = optionsPosition;
        this.optionsExpanded = optionsExpanded;

        JButton btnFilterOptions, btnFilterClear;

        Validator validator = (txt, text) -> {
            if(StringUtil.isBlank(text)) {
                return true;
            }
            RTableFilterOptions options = tbl.getFilterOptions();
            if(options == null) {
                options = new RTableFilterOptions();
            }
            if(options.getMatchMode() == MatchMode.NORMAL) {
                return true;
            }
            try {
                Pattern.compile(text, options.isMatchCase() ? 0 : Pattern.CASE_INSENSITIVE);
                return true;
            } catch(Exception e) {
                return false;
            }
        };

        String POS = optionsPosition == SwingUtilities.SOUTH ? "S" : "N";
        Lay.BLtg(this,
            POS, pnlOptions = Lay.BL("bg=100"),
            "C", scr = Lay.sp(tbl)
        );

        Lay.BLtg(pnlOptionsReal = new OptionsPanel(),
            "W", Lay.GBL(
                Lay.FL("L",
                    lblTable = Lay.lb(
                        "  Table: ",
                        mouseListener,
                        "fg=white"
                    ),
                    lblRowCounts = Lay.lb(
                        "? / ? Rows Shown",
                        RepleteImageModel.DATA_TABLE_ROWS,
                        mouseListener,
                        "fg=white"
                    ),
                    "nogap"
                ),
                mouseListener
            ),
            "E", Lay.FL(
                lblFilter = Lay.lb("Filter:", "fg=white", mouseListener),
                txtFilter = Lay.tx("", validator, "glowing,prefw=110,prefh=20,eb=0"),
                btnFilterClear   = Lay.btn(RepleteImageModel.CLEAR_TEXT, "ttt=Clear"),
                btnFilterOptions = Lay.btn(CommonConcepts.OPTIONS, "ttt=Options..."),
                mouseListener
            ),
            mouseListener,
            "cursor=hand,alltransp"
        );

        Lay.hn(pnlOptionsTray = new TrayPanel(),
            mouseListener,
            "cursor=hand,alltransp,dimh=10"
        );

        if(optionsExpanded) {
            pnlOptions.add(pnlOptionsReal, BorderLayout.CENTER);
        } else {
            pnlOptions.add(pnlOptionsTray, BorderLayout.CENTER);
        }

        model = tbl.getModel();
        rowSorter = tbl.getRowSorter();

        tbl.addSelectionListener(e -> updateRowCount());

        // Start listening to the table's current model and row sorter.
        attachModelListener();
        attachRowSorterListener();

        // If the table's model ever changes, catch it and listen to the
        // new model.  This should not happen on panels where the table's
        // model is NOT being reinstantiated with each refresh, but this
        // code is here just in case it happens, in any circumstance.
        tbl.addPropertyChangeListener(e -> {
            String name = e.getPropertyName();
            if(name.equals("model")) {
                detachModelListener();
                model = tbl.getModel();
                attachModelListener();
            } else if(name.equals("rowSorter")) {
                detachRowSorterListener();
                rowSorter = tbl.getRowSorter();
                attachRowSorterListener();
            }
        });

        // Set the action to perform when new and valid text field input
        // is found.
        txtFilter.addValidUnvalidatableTimeoutListener(e ->
            updateFromFilterText()
        );
        txtFilter.setUnvalidatableDecider(s -> StringUtil.isBlank(s));

        btnFilterOptions.addActionListener(e -> {
            Window parent = GuiUtil.win(RTablePanel.this);
            RTableFilterOptions options = tbl.getFilterOptions();
            if(options == null) {
                options = new RTableFilterOptions();
            }
            RTableFilterOptionsDialog dlg = new RTableFilterOptionsDialog(parent, options);
            dlg.setVisible(true);
            if(dlg.getResult() == RTableFilterOptionsDialog.SET) {
                tbl.setFilterOptions(dlg.getOptions());
                txtFilter.triggerValidation(true);
                txtFilter.focus();
            }
        });

        btnFilterClear.addActionListener(e -> {
            clearFilter();
            txtFilter.focus();
        });
    }

    public void clearFilter() {
        txtFilter.setText("");
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getTableLabel() {
        return tableLabel;
    }
    public JPanel getOptionsPanel() {
        return pnlOptions;
    }
    public JPanel getTrayPanel() {
        return pnlOptionsTray;
    }
    public int getOptionsPosition() {
        return optionsPosition;
    }
    public boolean isOptionsExpanded() {
        return optionsExpanded;
    }

    // Mutators

    public void setOptionsPosition(int optionsPosition) {
        this.optionsPosition = optionsPosition;

        if(optionsPosition == SwingConstants.NORTH) {
            remove(pnlOptions);
            add(pnlOptions, BorderLayout.NORTH);
            updateUI();
        } else {
            remove(pnlOptions);
            add(pnlOptions, BorderLayout.SOUTH);
            updateUI();
        }

        pnlOptionsReal.updateUI();
        pnlOptionsTray.updateUI();
    }

    public void setOptionsExpanded(boolean optionsExpanded) {
        this.optionsExpanded = optionsExpanded;

        pnlOptions.removeAll();
        pnlOptions.add(optionsExpanded ? pnlOptionsReal : pnlOptionsTray);
        pnlOptions.updateUI();
    }

    public void setLabelColor(Color color) {
        lblTable.setForeground(color);
        lblRowCounts.setForeground(color);
        lblFilter.setForeground(color);
    }

    public void setSelectedLabelColor(Color color) {
        selColor = color;
    }

    public void setOptionsBackground(Color color) {
        pnlOptions.setBackground(color);
    }

    public void setLightColorScheme() {
        setOptionsBackground(Lay.clrdlft());
        setSelectedLabelColor(Color.blue);
        setLabelColor(Lay.clr("51"));
        txtFilter.setBorder(Lay.mb("1", Lay.clr("51")));
        arrowFill   = ARROW_FILL_DARK;
        arrowBorder = ARROW_BORDER_DARK;

        pnlOptions.updateUI();
    }


    //////////
    // MISC //
    //////////

    protected void attachModelListener() {
        if(model != null) {
            model.addTableModelListener(tableModelListener);
        }
        updateRowCount();    // Initial update from new model
    }
    protected void attachRowSorterListener() {
        if(rowSorter != null) {
            rowSorter.addRowSorterListener(rowSorterListener);
        }
        updateRowCount();    // Initial update from row sorting state
    }
    protected void detachModelListener() {
        if(model != null) {
            model.removeTableModelListener(tableModelListener);
        }
    }
    protected void detachRowSorterListener() {
        if(rowSorter != null) {
            rowSorter.removeRowSorterListener(rowSorterListener);
        }
    }
    private void updateRowCount() {
        int mCount = model.getRowCount();
        int vCount = tbl.getRowCount();
        String hex = String.format("%02x%02x%02x", selColor.getRed(), selColor.getGreen(), selColor.getBlue());
        String sel =
            (tbl.getSelectedRowCount() == 0 ? "" :
                ", <font color='#" + hex + "'>" + tbl.getSelectedRowCount() +
                " Selected</font>")
        ;
        lblRowCounts.setText("<html>" + vCount + " / " + mCount + " Rows Shown" + sel + "</html>");
    }
    private void updateFromFilterText() {
        lastFilterText = txtFilter.getText();
        tbl.setFilterText(lastFilterText);
        fireFilterTextChangedNotifier();
    }

    public String getLastFilterText() {
        return lastFilterText;
    }
    public boolean isFilterEnabled() {
        return !StringUtil.isBlank(lastFilterText);
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private transient ChangeNotifier filterTextChangedNotifier = new ChangeNotifier(this);
    public void addFilterTextChangedListener(ChangeListener listener) {
        filterTextChangedNotifier.addListener(listener);
    }
    private void fireFilterTextChangedNotifier() {
        filterTextChangedNotifier.fireStateChanged();
    }


    ///////////////
    // LISTENERS //
    ///////////////

    private TableModelListener tableModelListener = new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
            updateRowCount();
        }
    };
    private RowSorterListener rowSorterListener = new RowSorterListener() {
        public void sorterChanged(RowSorterEvent e) {
            updateRowCount();
        }
    };


    ///////////////
    // ACCESSORS //
    ///////////////

    public RTable getTable() {
        return tbl;
    }
    public JScrollPane getScrollPane() {
        return scr;
    }

    public void setTableLabel(String tableLabel) {
        this.tableLabel = tableLabel;
        lblTable.setText("  " + StringUtil.prefixIf(tableLabel) + "Table: ");
    }

    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                setOptionsExpanded(!optionsExpanded);
            } else if(SwingUtilities.isRightMouseButton(e)) {
                setOptionsPosition(
                    optionsPosition == SwingConstants.NORTH ? SwingConstants.SOUTH : SwingConstants.NORTH);
            }
        }
    };


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public SelectionState getSelectionState() {
        return new SelectionState()
            .p("scr", scr.getSelectionState())   // Handles tbl by default
        ;
    }

    @Override
    public void setSelectionState(SelectionState state) {
        scr.setSelectionState(state.getGx("scr"));   // Handles tbl by default
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class OptionsPanel extends RPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(optionsPosition == SwingConstants.NORTH) {
                ll(g, getHeight() - 10);
            } else {
                ul(g, 0);
            }
        }
    }

    private class TrayPanel extends RPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(optionsPosition == SwingConstants.NORTH) {
                ul(g, 0);
                ur(g, 0);
            } else {
                ll(g, 0);
                lr(g, 0);
            }
        }
    }

    private void ul(Graphics g, int dy) {
        int x1 = 1;
        int x2 = 8;
        int y1 = dy + 1;
        int y2 = dy + 8;

        int[] xPath = new int[] {x1, x2, x1};
        int[] yPath = new int[] {y1, y1, y2};

        drawTriangle(g, xPath, yPath);
    }

    private void ur(Graphics g, int dy) {
        int r = getWidth() - 1;   // Right-most pixel
        int x1 = r - 1;
        int x2 = r - 8;
        int y1 = dy + 1;
        int y2 = dy + 8;

        int[] xPath = new int[] {x1, x2, x1};
        int[] yPath = new int[] {y1, y1, y2};

        drawTriangle(g, xPath, yPath);
    }

    private void ll(Graphics g, int dy) {
        int x1 = 1;
        int x2 = 8;
        int y1 = dy + 1;
        int y2 = dy + 8;

        int[] xPath = new int[] {x1, x2, x1};
        int[] yPath = new int[] {y1, y2, y2};

        drawTriangle(g, xPath, yPath);
    }

    private void lr(Graphics g, int dy) {
        int r = getWidth() - 1;   // Right-most pixel
        int x1 = r - 1;
        int x2 = r - 8;
        int y1 = dy + 1;
        int y2 = dy + 8;

        int[] xPath = new int[] {x1, x2, x1};
        int[] yPath = new int[] {y1, y2, y2};

        drawTriangle(g, xPath, yPath);
    }

    private void drawTriangle(Graphics g, int[] xPath, int[] yPath) {
        g.setColor(arrowFill);
        g.fillPolygon(xPath, yPath, 3);

        g.setColor(arrowBorder);
        g.drawPolygon(xPath, yPath, 3);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        System.out.println(Pattern.compile("^.*$", Pattern.CASE_INSENSITIVE).toString());
//        System.out.println(new JTable().getRowSorter());
        JButton btn;
        RTablePanel pnl;
        Lay.BLtg(Lay.fr("RTablePanel Demo"),
            "C", pnl = Lay.tblp("S", new MyTableModel(), "label=Xyz", true),
            "S", Lay.FL(btn = Lay.btn("&Click me")),
            "size=600, center, visible=true, toplevel"
        );
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnl.setOptionsPosition(SwingConstants.NORTH);
                pnl.setOptionsExpanded(!pnl.isOptionsExpanded());
            }
        });
        pnl.addFilterTextChangedListener(e -> {
            System.out.println(pnl.getLastFilterText() + "/" + pnl.isFilterEnabled());
        });
        JTable tbl = pnl.getTable();
//        pnl.setLightColorScheme();
//        pnl.setLabelColor(Color.red);
//        pnl.setOptionsBackground(new JPanel().getBackground());
//        pnl.setSelectedLabelColor(Color.blue);
//        pnl.moveOptionsBottom();
        TableRowSorter<TableModel> sorter = (TableRowSorter) tbl.getRowSorter();
//        sorter.setRowFilter(new RowFilter<TableModel, Integer>() {
//            @Override
//            public boolean include(javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
//                System.out.print("@" + entry.getIdentifier() + "/#" + entry.getValueCount() + ": ");
//                for(int i = 0; i < entry.getValueCount(); i++) {
//                    System.out.print(entry.getValue(i) + ", ");
//                }
//                System.out.println();
//                return entry.getStringValue(0).contains("a");
//            }
//        });
//        System.out.println(sorter.getSortKeys().size());
//        sorter.setSortable(1, false);
//        sorter.toggleSortOrder(0);
//        sorter.toggleSortOrder(0);
//        sorter.getSortKeys().stream().forEach(
//            k -> System.out.println(k.getColumn() + "/" + k.getSortOrder()));
//        sorter.setSortKeys(null);
//        System.out.println("--");
//        sorter.getSortKeys().stream().forEach(
//            k -> System.out.println(k.getColumn() + "/" + k.getSortOrder()));
//        System.out.println("^^");
//        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
//        sorter.setSortKeys(sortKeys);
//        sorter.addRowSorterListener(new RowSorterListener() {
//            public void sorterChanged(RowSorterEvent e) {
//                System.out.println(e.getType());
//                sorter.getSortKeys().stream().forEach(
//                    k -> System.out.println(k.getColumn() + "/" + k.getSortOrder()));
//            }
//        });
//
//        btn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                TableModel mdl = tbl.getModel();
//                for(int r = 0; r < mdl.getRowCount(); r++) {
//                    System.out.println(mdl.getValueAt(r, 0) + "; " + mdl.getValueAt(r, 1));
//                }
//            }
//        });
    }

    private static class MyTableModel extends DefaultUiHintedTableModel {
        private Object[][] data = new Object[][]{
            {"apple",   "orange"   , -12},
            {"cat",     "dog"      ,   1},
            {"hawk",    "always"   , 129},
            {"coyote",  "table"    ,  -4},
            {"wolf",    "marsh"    ,   0},
            {"warner",  "electric" ,   2},
            {"bros",    "otherwise",  22},
            {"zepplin", "overly"   ,   3},
        };

        @Override
        public int getRowCount() {
            if(data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        protected void init() {
            addColumn("Left");
            addColumn("Right");
            addColumn("Number", Integer.class);

            // Java's default, built-in RowSorter (Sorting & Filter) concepts actually
            // do rely on column types matching the data.  Can't be lazy and just list
            // all columns as String's if you're going to install the default RowSorter.
            // String columns work fine for presentation/rendering all right, but
            // sorting actually attempts to cast the getValueAt result to the specified
            // column type.
        }

        @Override
        protected Object getValueAtInner(int row, int col) {
            return data[row][col];
        }

        @Override
        public Color getBackgroundColor(int row, int col) {
            String text = StringUtil.cleanNull(getValueAt(row, col));
            return text.contains("a") ? Lay.clr("255,205,205") : super.getBackgroundColor(row, col);
        }

        @Override
        protected JButton getButton(int row, int col) {
            if(row == 2 && col == 0) {
                JButton btn = Lay.btn("Hawk&Bird", CommonConcepts.CANCEL,
                    (ActionListener) e -> {Dialogs.showMessage(null, "clicked");});
                return btn;
            }
            return super.getButton(row, col);
        }
    }
}
