package replete.ui.table;

import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.util.DateUtil;
import replete.util.ElapsedVerbosity;

public class UiTableTest {


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        List<String[]> data = new ArrayList<>();
        data.add(new String[] {"adfs", "asdfas"});
        data.add(new String[] {"xxxxx", "yyyyy"});

        MyModel mdl = new MyModel(data);
        JTable tbl = Lay.tbl(mdl, "size=14");
        Lay.BLtg(Lay.fr("asfsaf"),
            "C", Lay.sp(tbl),
            "size=400,center,visible"
        );
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private static class MyModel extends LiveRefreshUiHintedTableModel {


        ///////////
        // FIELD //
        ///////////

        private List<String[]> data;
        private long baseTime;


        //////////////////
    // CONSTRUCTORS //
    //////////////////

        public MyModel(List<String[]> data) {
            this.data = data;
            baseTime = System.currentTimeMillis();
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        protected void init() {
            addColumn("Column 1", String.class, new int[] {100, 100, 100});
            addColumn("Column 2", String.class, new int[] {-1, -1, -1});
        }
        @Override
        public int getRowCount() {
            if(data == null) {
                return 0;
            }
            return data.size();
        }
        @Override
        public Object getValueAt(int row, int col) {
            if(row == 0 && col == 1) {
                long elapsed = System.currentTimeMillis() - baseTime;
                String lrt = DateUtil.toLongString(baseTime) +
                    " (" + DateUtil.toElapsedString(elapsed, ElapsedVerbosity.MED, true) + " ago)";
                return lrt;
            }
            String[] d = data.get(row);
            return d[col];
        }

        @Override
        public Color getBackgroundColor(int row, int col) {
            if(col == 0) {
                return Color.YELLOW;
            }
            return Color.green;
        }
        @Override
        public Boolean isBold(int row, int col) {
            if(row == 0) {
                return true;
            }
            return null;
        }
        @Override
        public Insets getInsets(int row, int col) {
            return new Insets(5, 5, 5, 5);
        }
        @Override
        public int getRowHeight(int row) {
            if(row == 0) {
                return 0;
            }
            return 45;
        }
        @Override
        public boolean isCellEditable(int row, int col) {
            return row == 1 && col == 1;
        }
        @Override
        public TableCellRenderer getRenderer(int row, int col) {
            if(row == 1 && col == 1) {
                JButtonTableCellRenderer renderer = new JButtonTableCellRenderer();
                renderer.setIcon(CommonConcepts.CANCEL);
                return renderer;
            }
            return null;
        }
        @Override
        public DefaultCellEditor getEditor(int row, int col) {
            if(row == 1 && col == 1) {
                JButtonTableCellEditor editor = new JButtonTableCellEditor(new JButtonTableCellEditorActionListener() {
                    @Override
                    public void actionPerformed(Object value, int row, int col) {
                        System.out.println("CLICKED");
                    }
                });
                editor.getButton().setIcon(ImageLib.get(CommonConcepts.ADD));
                return editor;
            }
            return null;
        }
    }
}
