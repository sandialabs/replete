package replete.ui.table;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import replete.errors.UnexpectedEnumValueUnicornException;
import replete.event.ChangeNotifier;
import replete.extensions.ui.RXTable;
import replete.text.StringUtil;
import replete.threads.SwingTimerManager;
import replete.ui.GuiUtil;
import replete.ui.SelectionStateCreationMethod;
import replete.ui.SelectionStateSavable;
import replete.ui.cursors.CursorUtil;
import replete.ui.lay.Lay;
import replete.ui.panels.SelectionState;
import replete.ui.windows.escape.EscapeFrame;
import replete.util.ReflectionUtil;

// http://www.quepublishing.com/articles/article.aspx?p=24121&seqNum=3
// http://stackoverflow.com/questions/727177/setting-the-mouse-cursor-for-a-particular-jtable-cell

public class RTable extends RXTable implements SelectionStateSavable {


    ///////////
    // ENUMS //
    ///////////

    public enum TableSelectionStateIdentityMethod implements SelectionStateCreationMethod {
        INDEX,                     // (Default) Just record the selected indicies
        COLUMN_OBJECT_CLASS_NAME,  // Integer as 2nd argument = which col
        COLUMN_OBJECT_HASH_CODE,   // Integer as 2nd argument = which col
        COLUMN_OBJECT_REFERENCE,   // Integer as 2nd argument = which col
        ROW_OBJECT_CLASS_NAME,     // TableModel must be a ObjectBasedTableModel
        ROW_OBJECT_HASH_CODE,      // TableModel must be a ObjectBasedTableModel
        ROW_OBJECT_REFERENCE,      // TableModel must be a ObjectBasedTableModel
        CUSTOM                     // Function<Integer, Object> as 2nd argument = customIdentityFunction
    }


    ////////////
    // FIELDS //
    ////////////

    private RTableRenderer renderer = new UiHintedModelTableCellRenderer();
    private TableCellEditor editor = new UiHintedModelTableCellEditor();

    private String filterText = null;
    private RTableFilterOptions filterOptions = null;

    private boolean editable = true;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTable() {
        init();
    }
    public RTable(TableModel dm) {
        super(dm);
        init();
    }
    public RTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        init();
    }
    public RTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        init();
    }
    public RTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
        init();
    }
    public RTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        init();
    }
    public RTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        init();
    }

    private void init() {
        setDefaultRenderer(Object.class, renderer);
        setDefaultEditor(Object.class, editor);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1) {
                    int row = rowAtPoint(e.getPoint());
                    if(row == -1) {
                        fireEmptyDoubleClickNotifier();
                    } else {
                        fireDoubleClickNotifier();
                    }
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                    fireDeleteKeyNotifier();
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                TableModel model = getModel();
                if(model instanceof UiHintedTableModel) {
                    Point p = e.getPoint();
                    int visualRow = rowAtPoint(p);
                    int col = columnAtPoint(p);
                    if(visualRow != -1 && col != -1) {
                        UiHintedTableModel uiModel = (UiHintedTableModel) model;
                        int modelRow = convertRowIndexToModel(visualRow);
                        Cursor cursor = uiModel.getCursor(modelRow, col);
                        if(cursor != null) {
                            CursorUtil.changeCursor(RTable.this, cursor);
                        } else {
                            CursorUtil.revertCursor(RTable.this);
                        }
                    } else {
                        CursorUtil.revertCursor(RTable.this);
                    }
                }
            }
        });
        setFillsViewportHeight(true);
//        setBackground(Lay.clr("250"));   // TODO Look back into this
    }

    // This method was copied from super class, with some minor changes.
    @Override
    public void setModel(TableModel dataModel) {
        if(dataModel == null) {
            throw new IllegalArgumentException("Cannot set a null TableModel");
        }
        if(this.dataModel != dataModel) {
            TableModel old = this.dataModel;
            if(old != null) {
                old.removeTableModelListener(this);
            }
            this.dataModel = dataModel;
            dataModel.addTableModelListener(this);

            tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));

            firePropertyChange("model", old, dataModel);

            if(getAutoCreateRowSorter()) {
                setRowSorter(createNewRowSorter(dataModel));   // [Changed]
            }
        }
        updateColumnWidths();                                  // [Added]
    }

    @Override
    public void createDefaultColumnsFromModel() {
        super.createDefaultColumnsFromModel();
        updateColumnWidths();
    }

    // Really needs to be called whenever the column model changes,
    // which happens after the table resolves a table model's
    // structural event, but not yet 100% sure how to instrument
    // this.  This could use some more work.
    public void updateColumnWidths() {
        TableModel model = getModel();
        if(model instanceof RTableModel) {
            RTableModel model2 = (RTableModel) model;
            for(int c = 0; c < getColumnCount(); c++) {  // Get column count referring to column model!
                if(c < model2.getColumnCount()) {
                    int[] width = model2.getColumnWidth(c);
                    if(width != null) {
                        setColumnWidths(width, c);
                    }
                }
            }
        }
    }

    @Override
    public boolean editCellAt(int visualRow, int col, EventObject e) {
        TableModel model = getModel();
        if(model instanceof UiHintedTableModel) {
            UiHintedTableModel uiModel = (UiHintedTableModel) model;
            int modelRow = convertRowIndexToModel(visualRow);
            DefaultCellEditor subEditor = uiModel.getEditor(modelRow, col);
            if(subEditor != null) {
                ((UiHintedModelTableCellEditor)editor).setClickCountToStart(subEditor.getClickCountToStart());
            }
        }
        return super.editCellAt(visualRow, col, e);
    }

    public void selectAllInCell(int row, int column) {
        super.changeSelection(row, column, false, false);
        editCellAt(row, column);
        transferFocus();
        Component cmpEditor = ((DefaultCellEditor) getCellEditor()).getComponent();
        JTextField txtEditor = (JTextField) cmpEditor;
        txtEditor.selectAll();
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected ChangeNotifier deleteKeyNotifier = new ChangeNotifier(this);
    public void addDeleteKeyListener(ChangeListener listener) {
        deleteKeyNotifier.addListener(listener);
    }
    protected void fireDeleteKeyNotifier() {
        deleteKeyNotifier.fireStateChanged();
    }

    protected ChangeNotifier emptyClickNotifier = new ChangeNotifier(this);
    public void addEmptyDoubleClickListener(ChangeListener listener) {
        emptyClickNotifier.addListener(listener);
    }
    protected void fireEmptyDoubleClickNotifier() {
        emptyClickNotifier.fireStateChanged();
    }

    protected ChangeNotifier doubleClickNotifier = new ChangeNotifier(this);
    public void addDoubleClickListener(ChangeListener listener) {
        doubleClickNotifier.addListener(listener);
    }
    protected void fireDoubleClickNotifier() {
        doubleClickNotifier.fireStateChanged();
    }


    ///////////////
    // SELECTION //
    ///////////////

    public int getSelectedVisualRow() {
        return super.getSelectedRow();
    }
    public int[] getSelectedVisualRows() {
        return super.getSelectedRows();
    }
    public int getSelectedModelRow() {
        int visualRow = getSelectedVisualRow();
        if(visualRow == -1) {
            return -1;
        }
        int modelRow = convertRowIndexToModel(visualRow);
        return modelRow;
    }
    public int[] getSelectedModelRows() {
        int[] selection = getSelectedVisualRows();
        for (int i = 0; i < selection.length; i++) {
            selection[i] = convertRowIndexToModel(selection[i]);
        }
        return selection;
    }

    @Override
    public SelectionState getSelectionState(Object... args) {
        TableSelectionStateIdentityMethod method =
            getDefaultArg(args, TableSelectionStateIdentityMethod.INDEX);

        // Method-specific arguments
        Integer columnIndex = getDefaultArg(args, Integer.class, 1);
        Function<Integer, Object> customIdentityFunction =
            getDefaultArg(args, Function.class, 1);

        Set<Object> selected = new LinkedHashSet<>();

        for(int row : getSelectedModelRows()) {
            Object key = makeKey(row, method, columnIndex, customIdentityFunction);
            if(key != null) {
                selected.add(key);
            }
        }

        return new SelectionState(
            "selected",  selected,
            "method",    method,
            "colIndex",  columnIndex,
            "ifunction", customIdentityFunction
        );
    }

    @Override
    public void setSelectionState(SelectionState state) {
        Set<Object> selected = state.getGx("selected");
        TableSelectionStateIdentityMethod method = state.getGx("method");
        Integer columnIndex = state.getGx("colIndex");
        Function<Integer, Object> customIdentityFunction = state.getGx("ifunction");

        getSelectionModel().clearSelection();
        TableModel mdl = getModel();
        for(int row = 0; row < mdl.getRowCount(); row++) {
            Object key = makeKey(row, method, columnIndex, customIdentityFunction);
            if(selected.contains(key)) {
                int selViewRow = convertRowIndexToView(row);
                getSelectionModel().addSelectionInterval(selViewRow, selViewRow);
            }
        }
    }

    private Object makeKey(int row, TableSelectionStateIdentityMethod method,
                           // These are the method-specific params -->
                           Integer col, Function<Integer, Object> customIdentityFunction) {

        if(method == TableSelectionStateIdentityMethod.INDEX) {
            return row;

        } else if(method == TableSelectionStateIdentityMethod.COLUMN_OBJECT_CLASS_NAME) {
            TableModel model = getModel();
            Object obj = model.getValueAt(row, col);
            return obj.getClass().getName();

        } else if(method == TableSelectionStateIdentityMethod.COLUMN_OBJECT_HASH_CODE) {
            TableModel model = getModel();
            Object obj = model.getValueAt(row, col);
            return obj.hashCode();

        } else if(method == TableSelectionStateIdentityMethod.COLUMN_OBJECT_REFERENCE) {
            TableModel model = getModel();
            Object obj = model.getValueAt(row, col);
            return obj;

        } else if(method == TableSelectionStateIdentityMethod.ROW_OBJECT_CLASS_NAME) {
            TableModel model = getModel();
            if(model instanceof ObjectBasedTableModel) {
                ObjectBasedTableModel model2 = (ObjectBasedTableModel) model;
                Object obj = model2.getRowObject(row);
                return obj.getClass().getName();
            }
            return null;

        } else if(method == TableSelectionStateIdentityMethod.ROW_OBJECT_HASH_CODE) {
            TableModel model = getModel();
            if(model instanceof ObjectBasedTableModel) {
                ObjectBasedTableModel model2 = (ObjectBasedTableModel) model;
                Object obj = model2.getRowObject(row);
                return obj.hashCode();
            }
            return null;

        } else if(method == TableSelectionStateIdentityMethod.ROW_OBJECT_REFERENCE) {
            TableModel model = getModel();
            if(model instanceof ObjectBasedTableModel) {
                ObjectBasedTableModel model2 = (ObjectBasedTableModel) model;
                Object obj = model2.getRowObject(row);
                return obj;
            }
            return null;

        } else if(method == TableSelectionStateIdentityMethod.CUSTOM) {
            return customIdentityFunction.apply(row);

        } else {
            throw new UnexpectedEnumValueUnicornException(method);
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getFilterText() {
        return filterText;
    }
    public RTableFilterOptions getFilterOptions() {
        return filterOptions;
    }

    // Computed

    public Window getWindow() {     // Get window as a Component (physical object, Swing needs for parents often)
        return GuiUtil.win(this);
    }

    // Mutators

    public RTableRenderer getCellRenderer() {
        return renderer;
    }
    public RTable setFilterText(String filterText) {
        this.filterText = filterText;
        updateFilter();
        return this;
    }
    public RTable setFilterOptions(RTableFilterOptions filterOptions) {
        this.filterOptions = filterOptions;
        updateFilter();
        return this;
    }
    public RTable setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public void removeSelected() {
        removeSelected(this);
    }
    public void setUseStandardDeleteBehavior(boolean use) {
        removeKeyListener(stdDelKey);
        if(use) {
            addKeyListener(stdDelKey);
        }
    }

    private KeyListener stdDelKey = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                JTable tbl = (JTable) e.getSource();
                removeSelected(tbl);
            }
        }
    };

    // TODO: broken with filtering
    private void removeSelected(JTable tbl) {
        if(!editable || !(tbl.getModel() instanceof DefaultTableModel)) {
            return;
        }
        DefaultTableModel mdl = (DefaultTableModel) tbl.getModel();
        int size = mdl.getRowCount();
        if(size != 0 && tbl.getSelectedRow() != -1) {
            int[] indices = tbl.getSelectedRows();
            int low = indices[0];
            for(int i = indices.length - 1; i >= 0; i--) {
                int rmIdx = indices[i];
                mdl.removeRow(rmIdx);
            }
            if(low >= mdl.getRowCount()) {
                low = mdl.getRowCount() - 1;
            }
            tbl.getSelectionModel().setSelectionInterval(low, low);
        }
    }

    private void updateFilter() {
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) getRowSorter();
        if(sorter != null) {
            RTableFilterOptions options = filterOptions;
            if(options == null) {
                options = new RTableFilterOptions();
            }

            if(StringUtil.isBlank(filterText)) {
                sorter.setRowFilter(null);
            } else {
                String text;
                if(options.getMatchMode() == MatchMode.NORMAL) {
                    text = Pattern.quote(filterText);
                } else {
                    text = filterText;
                }

                try {
                    Pattern pattern = Pattern.compile(text, options.isMatchCase() ? 0 : Pattern.CASE_INSENSITIVE);
                    RowFilter<Object, Object> filter = new RegexFilter(pattern, new int[0]);
                    sorter.setRowFilter(filter);
                } catch(Exception e) {
                    // Soft fail
                }
            }
        }
    }

    @Override
    public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
        boolean oldValue = ReflectionUtil.get(this, "autoCreateRowSorter");    // Because autoCreateRowSorter is private
        ReflectionUtil.set(this, "autoCreateRowSorter", autoCreateRowSorter);
        if(autoCreateRowSorter) {
            setRowSorter(createNewRowSorter(getModel()));
        }
        firePropertyChange("autoCreateRowSorter", oldValue, autoCreateRowSorter);
    }

    protected RowSorter<? extends TableModel> createNewRowSorter(TableModel model) {
        return new RTableRowSorter<>(model);
    }


    /////////////////////////////////////////////////////
    // COPIED FROM JDK 1.8 RowFilter To Supply Pattern //
    /////////////////////////////////////////////////////

    private void checkIndices(int[] columns) {
        for (int i = columns.length - 1; i >= 0; i--) {
            if (columns[i] < 0) {
                throw new IllegalArgumentException("Index must be >= 0");
            }
        }
    }
    private abstract class GeneralFilter extends RowFilter<Object,Object> {
        private int[] columns;
        GeneralFilter(int[] columns) {
            checkIndices(columns);
            this.columns = columns;
        }
        @Override
        public boolean include(Entry<? extends Object,? extends Object> value){
            int count = value.getValueCount();
            if (columns.length > 0) {
                for (int i = columns.length - 1; i >= 0; i--) {
                    int index = columns[i];
                    if (index < count) {
                        if (include(value, index)) {
                            return true;
                        }
                    }
                }
            }
            else {
                while (--count >= 0) {
                    if (include(value, count)) {
                        return true;
                    }
                }
            }
            return false;
        }
        protected abstract boolean include(
              Entry<? extends Object,? extends Object> value, int index);
    }
    private class RegexFilter extends GeneralFilter {
        private Matcher matcher;
        RegexFilter(Pattern regex, int[] columns) {
            super(columns);
            if (regex == null) {
                throw new IllegalArgumentException("Pattern must be non-null");
            }
            matcher = regex.matcher("");
        }
        @Override
        protected boolean include(
                Entry<? extends Object,? extends Object> value, int index) {
            matcher.reset(value.getStringValue(index));
            return matcher.find();
        }
    }


    //////////
    // MISC //
    //////////

    public void focus() {
        requestFocusInWindow();
    }
    public void ensureIndexIsVisible(int row) {
        getSelectionModel().setSelectionInterval(row, row);
        scrollRectToVisible(new Rectangle(getCellRect(row, 0, true)));
    }
    public void ensureIndexIsVisible(int row, int row2) {
        getSelectionModel().setSelectionInterval(row, row2);
        scrollRectToVisible(new Rectangle(getCellRect(row, 0, true)));
    }
    public void ensureLastIndexIsVisible(int row, int row2) {
        getSelectionModel().setSelectionInterval(row, row2);
        scrollRectToVisible(new Rectangle(getCellRect(row2, 0, true)));
    }

    public void setColumnWidths(int[][] widths) {
        int length = Math.min(widths.length, getColumnCount());
        for(int c = 0; c < length; c++) {
            setColumnWidths(widths[c], c);
        }
    }
    private void setColumnWidths(int[] ww, int c) {
        if(ww[0] != -1) {
            getColumnModel().getColumn(c).setMinWidth(ww[0]);
        }
        if(ww[1] != -1) {
            getColumnModel().getColumn(c).setPreferredWidth(ww[1]);
        }
        if(ww[2] != -1) {
            getColumnModel().getColumn(c).setMaxWidth(ww[2]);
        } else {
            getColumnModel().getColumn(c).setMaxWidth(100000);
        }

        int cw = getColumnModel().getColumn(c).getWidth();
        if(cw < ww[0]) {
            getColumnModel().getColumn(c).setWidth(ww[0]);
        }
        if(cw > ww[2]) {
            getColumnModel().getColumn(c).setWidth(ww[2]);
        }
    }
    public void addSelectionListener(ListSelectionListener listener) {
        getSelectionModel().addListSelectionListener(listener);
    }

//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setPaint(
//            new GradientPaint(
//                0F, 0F, Lay.clr("250"),
//                getWidth() - 1, getHeight() - 1, Lay.clr("240")
//            )
//        );
////        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER).derive(0.3F));  How to transparently paint over CONTENT of table too
//        g.fillRect(0, getModel().getRowCount() * getRowHeight(), getWidth(), getHeight());
//    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    // We need to have a sorter so the RTablePanel can filter the rows,
    // but we don't want the column headers clickable right now, which
    // can have negative impacts on usability.
    private class RTableRowSorter<T extends TableModel> extends TableRowSorter<T> {
        public RTableRowSorter() {
            super();
        }
        public RTableRowSorter(T model) {
            super(model);
        }
        @Override
        public boolean isSortable(int column) {
            return false;
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        EscapeFrame fra = Lay.fr("Test");
        XModel mdl = new XModel();
        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[] {1, "AAA", "aardvark"});
        rows.add(new Object[] {2, "BBB", "bear"});
        rows.add(new Object[] {3, "CCC", "cat"});
        rows.add(new Object[] {4, "DDD", "dog"});
        rows.add(new Object[] {5, "EEE", "elephant"});
        mdl.setRows(rows);
        RTable tbl = Lay.tbl(mdl);
        ActionListener listener = (ActionListener) e -> {
            List<Object[]> rows2 = new ArrayList<>();
            rows2.add(new Object[] {1, "AAA", "aardvark"});
            rows2.add(new Object[] {2, "BBB", "bear"});
            rows2.add(new Object[] {3, "CCC", "cat"});
            rows2.add(new Object[] {4, "DDD", "dog"});
            rows2.add(new Object[] {5, "EEE", "elephant"});
            ((XModel) tbl.getModel()).setRows(rows2);
        };
        ActionListener listener2 = (ActionListener) e -> {
            List<Object[]> rows2 = new ArrayList<>();
            rows2.add(new Object[] {1, "AAA", "aardvark"});
            rows2.add(new Object[] {2, "BBB", "bear"});
            rows2.add(new Object[] {3, "CCC", "cat"});
            rows2.add(new Object[] {4, "DDD", "dog"});
            rows2.add(new Object[] {5, "EEE", "elephant"});
            XModel mdl2 = new XModel();
            mdl2.setRows(rows2);
            tbl.setModel(mdl2);
        };
        ActionListener listener3 = (ActionListener) e -> {
            List<Object[]> rows2 = new ArrayList<>();
            rows2.add(new Object[] {10, "BBB", "cat"});
            rows2.add(new Object[] {20, "DDD", "aardvark"});
            rows2.add(new Object[] {30, "EEE", "dog"});
            rows2.add(new Object[] {40, "AAA", "elephant"});
            rows2.add(new Object[] {50, "CCC", "bear"});
            rows2.add(new Object[] {11, "BBB", "cat"});
            rows2.add(new Object[] {21, "DDD", "aardvark"});
            rows2.add(new Object[] {31, "EEE", "dog"});
            rows2.add(new Object[] {41, "AAA", "elephant"});
            rows2.add(new Object[] {51, "CCC", "bear"});
            ((XModel) tbl.getModel()).setRows(rows2);
        };
        ActionListener listener4 = (ActionListener) e -> {
            List<Object[]> rows2 = new ArrayList<>();
            rows2.add(new Object[] {60, "EEE", "elephant"});
            rows2.add(new Object[] {70, "AAA", "dog"});
            rows2.add(new Object[] {80, "DDD", "bear"});
            rows2.add(new Object[] {90, "CCC", "cat"});
            rows2.add(new Object[] {100, "BBB", "aardvark"});
            rows2.add(new Object[] {160, "EEE", "elephant"});
            rows2.add(new Object[] {170, "AAA", "dog"});
            rows2.add(new Object[] {180, "DDD", "bear"});
            rows2.add(new Object[] {190, "CCC", "cat"});
            rows2.add(new Object[] {100, "BBB", "aardvark"});
            XModel mdl2 = new XModel();
            mdl2.setRows(rows2);
            tbl.setModel(mdl2);
        };
        ActionListener listenerClear = (ActionListener) e -> {
            tbl.clearSelection();
        };
        Lay.BLtg(fra,
            "C", Lay.tblp(tbl, "N", "expanded"),
            "S", Lay.BL(
                "W", Lay.FL(
                    Lay.btn("UpdateMdl1", listener),
                    Lay.btn("SetMdl1", listener2),
                    Lay.btn("UpdateMdl2", listener3),
                    Lay.btn("SetMdl2", listener4),
                    Lay.btn("&SAVE", (ActionListener) e -> saveState = tbl.getSelectionState()),
                    Lay.btn("&REST", (ActionListener) e -> tbl.setSelectionState(saveState)),
                    Lay.btn("&CLR", listenerClear),
                    "opaque=false"
                ),
                "E", Lay.FL("R",
                    Lay.btn("Close", (ActionListener) e -> fra.close()),
                    "opaque=false"
                ),
                "darkb"
            ),
            "size=[800,600],center,visible"
        );
        fra.addClosingListener(e -> SwingTimerManager.shutdown());
    }
    private static SelectionState saveState;

    protected static class XModel extends DefaultUiHintedTableModel {


        ////////////////
        // OVERRIDDEN //
        ////////////////

        private List<Object[]> rows = new ArrayList<>();


        //////////////
        // MUTATORS //
        //////////////

        public void setRows(List<Object[]> rows) {
            this.rows = rows;
            fireTableDataChanged();
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        protected void init() {
            addColumn("ID", Integer.class, 100, 100, 100);
            addColumn("Name", String.class, 300, 300, 300);
            addColumn("Text", String.class, -1, -1, -1);
        }

        @Override
        public int getRowCount() {
            if(rows == null) {
                return 0;
            }
            return rows.size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            Object[] bean = rows.get(row);
            switch(col) {
                case 0:
                    return bean[0];
                case 1:
                    return bean[1];
                case 2:
                    return bean[2];
            }
            return null;
        }

        @Override
        public int getAlignment(int row, int col) {
            return col == 0 ? SwingConstants.RIGHT : super.getAlignment(row, col);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        @Override
        public Insets getInsets(int row, int col) {
            return new Insets(0, 2, 0, 2);
        }

        @Override
        public int getRowHeight(int row) {
            return 20;
        }
    }
}
