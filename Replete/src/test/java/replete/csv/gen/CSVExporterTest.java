package replete.csv.gen;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import replete.ui.csv.AbstractCommonCsvColumn;
import replete.ui.csv.CsvColumnInfo;
import replete.ui.csv.CsvColumnType;
import replete.ui.csv.CsvExporter;
import replete.ui.csv.DefaultCsvColumnType;

public class CSVExporterTest {
    private List<AbstractCommonCsvColumn> columns = new ArrayList<>();

    @Before
    public void setup() throws Exception {
        columns.add(new UnitTestColumnOne(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_COLUMN_1, 1));
    }

    @Test
    public void testSingleCSVColumn() throws Exception {
        List<List<String>> csvRows = CsvExporter.writeColumnList("My data", columns);
        assertTrue(csvRows.size() == 1);
        assertTrue(csvRows.get(0).size() == 1);
        assertTrue(csvRows.get(0).get(0).equals("Column 1: My data"));
    }

    @Test
    public void testMultiCSVColumns() throws Exception {
        columns.add(new UnitTestColumnOne(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_COLUMN_2, 2));

        List<List<String>> csvRows = CsvExporter.writeColumnList("My data", columns);
        assertTrue(csvRows.size() == 1);
        assertTrue(csvRows.get(0).size() == 2);
        assertTrue(csvRows.get(0).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(0).get(1).equals("Column 2: My data"));
    }

    @Test
    public void testCSVColumnWithNullData() throws Exception {
        columns.add(new UnitTestColumnNull(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_COLUMN_NULL, 2));

        List<List<String>> csvRows = CsvExporter.writeColumnList("My data", columns);
        assertTrue(csvRows.size() == 1);
        assertTrue(csvRows.get(0).size() == 2);
        assertTrue(csvRows.get(0).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(0).get(1).equals(""));
    }

    @Test
    public void testSingleCSVList() throws Exception {
        columns.add(new UnitTestColumnList(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_1, 3));

        List<List<String>> csvRows = CsvExporter.writeColumnList("My data", columns);
        assertTrue(csvRows.size() == 3);
        assertTrue(csvRows.get(0).size() == 2);
        assertTrue(csvRows.get(0).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(0).get(1).equals("Column 3: apple"));

        assertTrue(csvRows.get(1).size() == 2);
        assertTrue(csvRows.get(1).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(1).get(1).equals("Column 3: banana"));

        assertTrue(csvRows.get(2).size() == 2);
        assertTrue(csvRows.get(2).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(2).get(1).equals("Column 3: cat"));
    }

    @Test
    public void testMultiCSVList() throws Exception {
        columns.add(new UnitTestColumnList(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_1, 3));
        columns.add(new UnitTestColumnList(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_2, 4));

        List<List<String>> csvRows = CsvExporter.writeColumnList("My data", columns);
        assertTrue(csvRows.size() == 9);
        assertTrue(csvRows.get(0).size() == 3);
        assertTrue(csvRows.get(0).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(0).get(1).equals("Column 3: apple"));
        assertTrue(csvRows.get(0).get(2).equals("Column 4: apple"));

        assertTrue(csvRows.get(1).size() == 3);
        assertTrue(csvRows.get(1).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(1).get(1).equals("Column 3: apple"));
        assertTrue(csvRows.get(1).get(2).equals("Column 4: banana"));

        assertTrue(csvRows.get(2).size() == 3);
        assertTrue(csvRows.get(2).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(2).get(1).equals("Column 3: apple"));
        assertTrue(csvRows.get(2).get(2).equals("Column 4: cat"));

        assertTrue(csvRows.get(3).size() == 3);
        assertTrue(csvRows.get(3).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(3).get(1).equals("Column 3: banana"));
        assertTrue(csvRows.get(3).get(2).equals("Column 4: apple"));

        assertTrue(csvRows.get(4).size() == 3);
        assertTrue(csvRows.get(4).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(4).get(1).equals("Column 3: banana"));
        assertTrue(csvRows.get(4).get(2).equals("Column 4: banana"));

        assertTrue(csvRows.get(5).size() == 3);
        assertTrue(csvRows.get(5).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(5).get(1).equals("Column 3: banana"));
        assertTrue(csvRows.get(5).get(2).equals("Column 4: cat"));

        assertTrue(csvRows.get(6).size() == 3);
        assertTrue(csvRows.get(6).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(6).get(1).equals("Column 3: cat"));
        assertTrue(csvRows.get(6).get(2).equals("Column 4: apple"));

        assertTrue(csvRows.get(7).size() == 3);
        assertTrue(csvRows.get(7).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(7).get(1).equals("Column 3: cat"));
        assertTrue(csvRows.get(7).get(2).equals("Column 4: banana"));

        assertTrue(csvRows.get(8).size() == 3);
        assertTrue(csvRows.get(8).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(8).get(1).equals("Column 3: cat"));
        assertTrue(csvRows.get(8).get(2).equals("Column 4: cat"));
    }

    @Test
    public void testMultiCSVListMixedWithSingleRows() throws Exception {
        columns.add(new UnitTestColumnList(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_1, 3));
        columns.add(new UnitTestColumnOne(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_2, 4));
        columns.add(new UnitTestColumnList(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_2, 5));
        columns.add(new UnitTestColumnOne(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_3, 6));

        List<List<String>> csvRows = CsvExporter.writeColumnList("My data", columns);
        assertTrue(csvRows.size() == 9);
        assertTrue(csvRows.get(0).size() == 5);
        assertTrue(csvRows.get(0).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(0).get(1).equals("Column 3: apple"));
        assertTrue(csvRows.get(0).get(2).equals("Column 4: My data"));
        assertTrue(csvRows.get(0).get(3).equals("Column 5: apple"));
        assertTrue(csvRows.get(0).get(4).equals("Column 6: My data"));

        assertTrue(csvRows.get(1).size() == 5);
        assertTrue(csvRows.get(1).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(1).get(1).equals("Column 3: apple"));
        assertTrue(csvRows.get(1).get(2).equals("Column 4: My data"));
        assertTrue(csvRows.get(1).get(3).equals("Column 5: banana"));
        assertTrue(csvRows.get(1).get(4).equals("Column 6: My data"));

        assertTrue(csvRows.get(2).size() == 5);
        assertTrue(csvRows.get(2).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(2).get(1).equals("Column 3: apple"));
        assertTrue(csvRows.get(2).get(2).equals("Column 4: My data"));
        assertTrue(csvRows.get(2).get(3).equals("Column 5: cat"));
        assertTrue(csvRows.get(2).get(4).equals("Column 6: My data"));

        assertTrue(csvRows.get(3).size() == 5);
        assertTrue(csvRows.get(3).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(3).get(1).equals("Column 3: banana"));
        assertTrue(csvRows.get(3).get(2).equals("Column 4: My data"));
        assertTrue(csvRows.get(3).get(3).equals("Column 5: apple"));
        assertTrue(csvRows.get(3).get(4).equals("Column 6: My data"));

        assertTrue(csvRows.get(4).size() == 5);
        assertTrue(csvRows.get(4).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(4).get(1).equals("Column 3: banana"));
        assertTrue(csvRows.get(4).get(2).equals("Column 4: My data"));
        assertTrue(csvRows.get(4).get(3).equals("Column 5: banana"));
        assertTrue(csvRows.get(4).get(4).equals("Column 6: My data"));

        assertTrue(csvRows.get(5).size() == 5);
        assertTrue(csvRows.get(5).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(5).get(1).equals("Column 3: banana"));
        assertTrue(csvRows.get(5).get(2).equals("Column 4: My data"));
        assertTrue(csvRows.get(5).get(3).equals("Column 5: cat"));
        assertTrue(csvRows.get(5).get(4).equals("Column 6: My data"));

        assertTrue(csvRows.get(6).size() == 5);
        assertTrue(csvRows.get(6).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(6).get(1).equals("Column 3: cat"));
        assertTrue(csvRows.get(6).get(2).equals("Column 4: My data"));
        assertTrue(csvRows.get(6).get(3).equals("Column 5: apple"));
        assertTrue(csvRows.get(6).get(4).equals("Column 6: My data"));

        assertTrue(csvRows.get(7).size() == 5);
        assertTrue(csvRows.get(7).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(7).get(1).equals("Column 3: cat"));
        assertTrue(csvRows.get(7).get(2).equals("Column 4: My data"));
        assertTrue(csvRows.get(7).get(3).equals("Column 5: banana"));
        assertTrue(csvRows.get(7).get(4).equals("Column 6: My data"));

        assertTrue(csvRows.get(8).size() == 5);
        assertTrue(csvRows.get(8).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(8).get(1).equals("Column 3: cat"));
        assertTrue(csvRows.get(8).get(2).equals("Column 4: My data"));
        assertTrue(csvRows.get(8).get(3).equals("Column 5: cat"));
        assertTrue(csvRows.get(8).get(4).equals("Column 6: My data"));
    }

    @Test
    public void testMultiCSVListWithEmptyGroups() throws Exception {
        columns.add(new UnitTestColumnEmptyList(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_2, 2));
        columns.add(new UnitTestColumnOne(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_1, 3));
        columns.add(new UnitTestColumnEmptyList(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_GROUP_COLUMN_2, 4));

        List<List<String>> csvRows = CsvExporter.writeColumnList("My data", columns);
        assertTrue(csvRows.size() == 1);
        assertTrue(csvRows.get(0).size() == 4);
        assertTrue(csvRows.get(0).get(0).equals("Column 1: My data"));
        assertTrue(csvRows.get(0).get(1).equals(""));
        assertTrue(csvRows.get(0).get(2).equals("Column 3: My data"));
        assertTrue(csvRows.get(0).get(3).equals(""));
    }

    @Test
    public void testErrorColumns() throws Exception {
        UnitTestColumnError errorColumn = new UnitTestColumnError(DefaultCsvColumnType.DEFAULT, TestCsvColumnInfo.UNIT_TEST_COLUMN_ERROR, 2);
        errorColumn.setImportantColumn(true);
        columns.add(errorColumn);

        List<List<String>> csvRows = new ArrayList<>();
        try {
            csvRows = CsvExporter.writeColumnList("My data", columns);
        } catch(Exception e) {
            assertTrue(csvRows.isEmpty());
        }
    }

    @Test
    public void testGetColumnHeaders() throws Exception {
        List<String> columnHeaders = CsvExporter.getColumnHeaders(columns);
        assertTrue(columnHeaders.size() == 1);
        assertTrue(columnHeaders.get(0).equals("Unit Test Column 1"));
    }

    // A dummy column class that generates a single piece of cell data for its field.
    class UnitTestColumnOne extends AbstractCommonCsvColumn {
        private int id;

        public UnitTestColumnOne(CsvColumnType type, CsvColumnInfo name, int id) throws Exception {
            super(type, name);
            this.id = id;
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public Object getCellData(Object source) throws Exception {
            if(source instanceof String) {
                return ("Column " + id + ": " + (String)source);
            }
            return null;
        }
    }

    // A column that generates a list of data for its field (this data will span multiple rows, with one row
    // per list item).
    class UnitTestColumnList extends AbstractCommonCsvColumn {
        private int id;

        public UnitTestColumnList(CsvColumnType type, CsvColumnInfo name, int id) throws Exception {
            super(type, name);
            this.id = id;
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public Object getCellData(Object source) {
            List<String> listData = new ArrayList<>();
            listData.add("Column " + id + ": apple");
            listData.add("Column " + id + ": banana");
            listData.add("Column " + id + ": cat");
            return listData;
        }
    }

    // This column class is for testing what happens if an error is thrown when we try to get cell data.
    class UnitTestColumnError extends AbstractCommonCsvColumn {
        public UnitTestColumnError(CsvColumnType type, CsvColumnInfo name, int id) throws Exception {
            super(type, name);
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public Object getCellData(Object source) throws Exception {
            throw new Exception();
        }
    }

    // This column class tests what happens if the cell data is null.
    class UnitTestColumnNull extends AbstractCommonCsvColumn {
        public UnitTestColumnNull(CsvColumnType type, CsvColumnInfo name, int id) throws Exception {
            super(type, name);
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public Object getCellData(Object source) {
            return null;
        }
    }

    // This class is for testing what happens if the column is supposed to return a list
    // (i.e. a multi-row field) but that list is empty.
    class UnitTestColumnEmptyList extends AbstractCommonCsvColumn {
        public UnitTestColumnEmptyList(CsvColumnType type, CsvColumnInfo name, int id) throws Exception {
            super(type, name);
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public Object getCellData(Object source) {
            List<String> listData = new ArrayList<>();
            return listData;
        }
    }

    private enum TestCsvColumnInfo implements CsvColumnInfo {
        UNIT_TEST_COLUMN_1("Unit Test Column 1", ""),
        UNIT_TEST_COLUMN_2("Unit Test Column 2", ""),
        UNIT_TEST_COLUMN_NULL("Unit Test Column Null", ""),
        UNIT_TEST_COLUMN_ERROR("Unit Test Column Error", ""),
        UNIT_TEST_GROUP_COLUMN_1("Unit Test Group Column 1", ""),
        UNIT_TEST_GROUP_COLUMN_2("Unit Test Group Column 2", ""),
        UNIT_TEST_GROUP_COLUMN_3("Unit Test Group Column 3", "");

        private String name;
        private String description;

        private TestCsvColumnInfo (String name, String description) {
            this.name = name;
            this.description = description;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
