package replete.ui.sdplus.demo;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import replete.ui.sdplus.panels.DateScalePanelModel;


/**
 * Demo table model backed by the scale set panel model.
 *
 * @author Derek Trumbo
 */

public class DemoTableModel extends DefaultTableModel {
    protected DemoModel demoModel;
    protected String[] selYN;
    protected Color[] colors;
    protected Map<String, Boolean> notListed =
        new HashMap<String, Boolean>();

    public void setSelYN(String[] newSelYN) {
        selYN = newSelYN;
        fireTableDataChanged();
    }

    public DemoTableModel(DemoModel mm) {
        demoModel = mm;
        selYN = new String[demoModel.data.size()];
        colors = new Color[demoModel.data.size()];
        for(int d = 0; d < demoModel.data.size(); d++) {
            selYN[d] = "Y";
            colors[d] = Color.white;
        }
    }

    public void setColors(Color[] newColors) {
        colors = newColors;
        fireTableDataChanged();
    }

    public Color getColor(int row) {
        if(colors == null) {
            return Color.white;
        }
        return colors[row];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return demoModel.scales.size() - 2 + 2;
        // -2 to not show special scale in the table
        // +2 for extra CLR column.
    }

    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex == 0) {
            return "CLR";
        } else if(columnIndex == 1) {
            return "SEL?";
        }
        String nm = demoModel.scales.get(columnIndex - 2).name;
        String key = demoModel.getScaleKeys()[columnIndex - 2];
        if(notListed.get(key) != null && notListed.get(key)) {
            nm = "(hid)" + nm;
        }
        return nm;
    }

    @Override
    public int getRowCount() {
        if(demoModel == null) {
            return 0;
        }
        return demoModel.data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return " ";
        } else if(columnIndex == 1) {
            return selYN[rowIndex];
        }
        Object val = demoModel.data.get(rowIndex).values.get(
            demoModel.scales.get(columnIndex - 2).key);
        if(getColumnName(columnIndex).indexOf("Birth Date") != -1) {
            if(val != null) {
                val = DateScalePanelModel.DEFAULT_DATE_FORMAT.format(new Date((Long) val));
            }
        } else if(getColumnName(columnIndex).indexOf("Time Stamp") != -1) {
            if(val != null) {
                val = new SimpleDateFormat("yyyy/M/d HH:mm:ss").format(new Date((Long) val));
            }
        }
        return val;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }
}
