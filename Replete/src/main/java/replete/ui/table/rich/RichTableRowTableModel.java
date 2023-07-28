package replete.ui.table.rich;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.DefaultCellEditor;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import replete.text.StringUtil;
import replete.ui.lay.Lay;
import replete.ui.table.LiveRefreshUiHintedTableModel;

public class RichTableRowTableModel extends LiveRefreshUiHintedTableModel {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static Color LIGHT_RED = Lay.clr("255,205,205");
    private static final int DEFAULT_KEY_COL = 0;
    private static final int DEFAULT_VALUE_COL = 1;

    // Core

    private int keyColumn = DEFAULT_KEY_COL;
    private int valueColumn = DEFAULT_VALUE_COL;
    private int valueAlignment = SwingConstants.RIGHT;
    private RichTableRowList rows = new RichTableRowList();
    private boolean showDescriptionColumn = false;

    public RichTableRowTableModel() {
    }
    public RichTableRowTableModel(boolean swapKeyValueColumns) {
        if(swapKeyValueColumns) {
            keyColumn = DEFAULT_VALUE_COL;
            valueColumn = DEFAULT_KEY_COL;
        }
    }


    //////////
    // MISC //
    //////////

    protected int getPropertyPreferredWidth() {
        return 300;
    }
    protected int getValuePreferredWidth() {
        return 80;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    // Mutators

    public void addTuple(RichTableRow tuple) {
        rows.add(tuple);
        updateDescCol();
        fireTableDataChanged();
    }
    public void setRows(RichTableRowList rows) {
        this.rows = rows;
        clearLiveCells();
        int r = 0;
        for(RichTableRow row : rows) {
            if(row.isLiveRefresh()) {
                registerLiveCell(r, valueColumn);
            }
            r++;
        }
        updateDescCol();
        fireTableDataChanged();
    }
    public void clear() {
        rows.clear();
        clearLiveCells();
        fireTableDataChanged();
    }
    public void setValueAlignment(int valueAlignment) {
        this.valueAlignment = valueAlignment;
    }

    private void updateDescCol() {
        boolean newShowDescCol = checkShowDescCol();
        if(newShowDescCol != showDescriptionColumn) {
            showDescriptionColumn = newShowDescCol;
            fireTableStructureChanged();
        }
    }
    private boolean checkShowDescCol() {
        for(RichTableRow row : rows) {
            if(row.getDescription() != null) {
                return true;
            }
        }
        return false;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getColumnClass(int col) {
        return String.class;
    }
    @Override
    public int getColumnCount() {
        return showDescriptionColumn ? 3 : 2;
    }
    @Override
    public String getColumnName(int col) {
        if(col == keyColumn) {
            return "Property";            // TODO Make these column headers modifiable
        } else if(col == valueColumn) {
            return "Value";
        }
        return "Description";
    }
    @Override
    public int[] getColumnWidth(int col) {
        if(col == keyColumn) {
            if(showDescriptionColumn || keyColumn == 0) {
                int ppw = getPropertyPreferredWidth();
                return new int[] {100, ppw, 500};
            }
            return new int[] {-1, -1, -1};

        } else if(col == valueColumn) {
            if(showDescriptionColumn || valueColumn == 0) {
                int vpw = getValuePreferredWidth();
                return new int[] { 40, vpw, 600};
            }
            return new int[] {-1, -1, -1};
        }

        return new int[] {-1, -1, -1};
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
        RichTableRow rtr = rows.get(row);
        String pre  = rtr.isUseHtml() ? "<html>" : "";  // Technically we could have 3 diff
        String post = rtr.isUseHtml() ? "</html>" : ""; // HTML flags for prop, val, & desc

        if(col == keyColumn) {
            return pre + rtr.getProperty() + post;

        } else if(col == valueColumn) {
            Object value = rtr.getValue();
            if(value instanceof ValueGenerator) {
                value = ((ValueGenerator) value).generate();
            }

            String valueStr;
            if(value instanceof Number) {
                if(rtr.getProperty().toLowerCase().contains("port")) {
                    valueStr = value.toString();
                } else {
                    valueStr = StringUtil.commas(value.toString());
                }

            } else {
                valueStr = value == null ? "" : value.toString();
            }
            return pre + valueStr + post;
        }

        String descStr = rtr.getDescription() == null ? "" : rtr.getDescription().toString();
        return pre + descStr + post;
    }

    @Override
    public int getAlignment(int row, int col) {
        if(col == valueColumn) {
            RichTableRow code = rows.get(row);
            Integer align = code.getAlignment();
            if(align != null) {
                return align;
            }
            return valueAlignment;
        }
        return super.getAlignment(row, col);
    }

    @Override
    public Color getForegroundColor(int row, int col) {
        RichTableRow code = rows.get(row);
        Color fg = code.getForeground();
        if(fg != null) {
            return fg;
        }
        return null;
    }

    @Override
    public Color getBackgroundColor(int row, int col) {
        RichTableRow code = rows.get(row);
        Color bg = code.getBackground();
        if(bg != null) {
            return bg;
        } else if(code.isImportant()) {
            return LIGHT_RED;
        }
        return null;
    }

    @Override
    public Insets getInsets(int row, int col) {
        return new Insets(0, 2, 0, 2);
    }

    @Override
    public Font getFont(int row, int col) {
        if(col == 1) {
            RichTableRow code = rows.get(row);
            return code.getFont();
        }
        return null;
    }

    @Override
    public Boolean isBold(int row, int col) {
        RichTableRow code = rows.get(row);
        return code.isBold() ? true : null;
    }

    @Override
    public TableCellRenderer getRenderer(int row, int col) {
        if(col == valueColumn) {
            RichTableRow code = rows.get(row);
            if(code.getValueRenderer() != null) {
                return code.getValueRenderer();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if(col == valueColumn) {
            RichTableRow code = rows.get(row);
            return code.getValueEditor() != null;
        }
        return super.isCellEditable(row, col);
    }

    @Override
    public DefaultCellEditor getEditor(int row, int col) {
        if(col == valueColumn) {
            RichTableRow code = rows.get(row);
            if(code.getValueEditor() != null) {
                return code.getValueEditor();
            }
        }
        return null;
    }

    @Override
    public Cursor getCursor(int row, int col) {
        if(col == valueColumn) {
            RichTableRow code = rows.get(row);
            if(code.getValueEditor() != null) {
                return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            }
        }
        return null;
    }
}
