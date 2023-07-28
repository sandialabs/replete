package replete.logging;

import java.awt.Insets;
import java.util.List;

import javax.swing.SwingConstants;

import replete.ui.ClassNameSimplifierManager;
import replete.ui.table.DefaultUiHintedTableModel;

public  class LogCodeTableModel extends DefaultUiHintedTableModel {
    private List<LogCode> codeList;

    public LogCodeTableModel(List<LogCode> codeList) {
        this.codeList = codeList;
    }

    @Override
    protected void init() {
        addColumn("Category",    String.class, new int[] {115, 115, 300});
        addColumn("Logger",      String.class, new int[] {300, 300, 500});
        addColumn("Code",        String.class, new int[] { 70,  70,  70});
        addColumn("!!!",         String.class, new int[] { 30,  30,  30});
        addColumn("Description", String.class, new int[] { -1,  -1,  -1});
    }
    @Override
    public int getRowCount() {
        if(codeList == null) {
            return 0;
        }
        return codeList.size();
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogCode code = codeList.get(rowIndex);
        switch(columnIndex) {
            case 0: return code.getCategory();
            case 1: return "<html>" + ClassNameSimplifierManager.getSimplifiedMarkedUp(code.getLoggerName()) + "</html>";
            case 2: return code.getCode();
            case 3: return code.isImportant() ? "Y" : "";
            case 4: return code.getDescription();
        }
        return null;
    }

    @Override
    public int getAlignment(int row, int col) {
        return col == 3 ? SwingConstants.CENTER : super.getAlignment(row, col);
    }

    @Override
    public Insets getInsets(int row, int col) {
        return new Insets(0, 2, 0, 2);
    }
}
