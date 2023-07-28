package replete.ui.csv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CsvModel implements Serializable {
    private String name;
    private boolean includeHeader;
    private List<AbstractCommonCsvColumn> columns;

    public CsvModel(String name) {
        this(name, new ArrayList<>());
    }

    public CsvModel(String name, List<AbstractCommonCsvColumn> columns) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.columns.addAll(columns);
        includeHeader = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIncludeHeader() {
        return includeHeader;
    }

    public void setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    public List<AbstractCommonCsvColumn> getColumns() {
        return columns;
    }

    public void addColumn(AbstractCommonCsvColumn column) {
        columns.add(column);
    }

    public void removeColumn(AbstractCommonCsvColumn column) {
        columns.remove(column);
    }

    public void moveColumnUp(AbstractCommonCsvColumn column) {
        int currentIndex = columns.indexOf(column);
        if(currentIndex == 0) {
            return;
        }
        AbstractCommonCsvColumn removedColumn = columns.remove(currentIndex);
        columns.add(currentIndex - 1, removedColumn);
    }

    public void moveColumnDown(AbstractCommonCsvColumn column) {
        int currentIndex = columns.indexOf(column);
        if(currentIndex == columns.size() - 1) {
            return;
        }
        AbstractCommonCsvColumn removedColumn = columns.remove(currentIndex);
        columns.add(currentIndex + 1, removedColumn);
    }

    @Override
    public String toString() {
        return name;
    }
}
