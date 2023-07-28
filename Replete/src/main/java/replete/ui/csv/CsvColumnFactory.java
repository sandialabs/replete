package replete.ui.csv;

import java.util.List;

public interface CsvColumnFactory {
    public AbstractCommonCsvColumn createColumn(CsvColumnInfo info);

    public List<AbstractCommonCsvColumn> createAllColumns();

    public List<CsvColumnType> getAvailableColumnTypes();
}
