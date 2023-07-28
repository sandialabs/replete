package replete.ui.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class CsvDiffModelTest {

    private static String[] report1headers = {"matched1", "matched2", "apples1", "oranges"};
    private static String[] report2headers = { "apples2", "pineapple", "matched2", "matched1",};
    private static List<AbstractCommonCsvColumn> report1Cols = new ArrayList();
    private static List<AbstractCommonCsvColumn> report2Cols = new ArrayList();
    private static Map<String, AbstractCommonCsvColumn> report1Map = new HashMap();
    private static Map<String, AbstractCommonCsvColumn> report2Map = new HashMap();


    @BeforeClass
    public static void oneTimeSetUp() {
        // list represents order of columns in file; map is for convenience below.
        int index = 0;
        for (String colName : report1headers) {
            FileCsvColumnInfo info = new FileCsvColumnInfo(colName, "", index++);
            FileCsvColumn col = new FileCsvColumn(DefaultCsvColumnType.DEFAULT, info);
            report1Cols.add(col);
            report1Map.put(colName, col);
        }
        index = 0;
        for (String colName : report2headers) {
            FileCsvColumnInfo info = new FileCsvColumnInfo(colName, "", index++);
            FileCsvColumn col = new FileCsvColumn(DefaultCsvColumnType.DEFAULT, info);
            report2Cols.add(col);
            report2Map.put(colName, col);
        }
    }

    private void checkColumn(CsvDiffModel model, DiffCsvColumn col, String expectedName,
                             String expectedReport1Name, String expectedReport2Name) {
        assertEquals(expectedName, col.getName());
        int index = model.getOutputColumns().indexOf(col);

        if (expectedReport1Name==null) {
            assertNull(col.getReport1Column());
            assertEquals(index, model.getReportDiffColumns(1).size());
        } else {
            assertEquals(expectedReport1Name, col.getReport1Column().getName());
            assertEquals(expectedReport1Name, model.getReportDiffColumns(1).get(index).getName());
        }
        if (expectedReport2Name==null) {
            assertNull(col.getReport2Column());
            assertEquals(index, model.getReportDiffColumns(2).size());
        } else {
            assertEquals(expectedReport2Name, col.getReport2Column().getName());
            assertEquals(expectedReport2Name, model.getReportDiffColumns(2).get(index).getName());
        }

    }

    @Test
    public void test() throws Exception {
        CsvDiffModel model = new CsvDiffModel("test", report1Cols, report2Cols);

        // constructor should set up default model correspondence; should have
        // "matched" columns identified, but not those with different names
        List<AbstractCommonCsvColumn> diffCols = model.getOutputColumns();
        assertEquals(2, diffCols.size());
        DiffCsvColumn diffCol = (DiffCsvColumn) diffCols.get(0);
        checkColumn(model, diffCol, "matched1", "matched1", "matched1");
        diffCol = (DiffCsvColumn) diffCols.get(1);
        checkColumn(model, diffCol, "matched2", "matched2", "matched2");

        // manually match the "apples" columns
        model.addReportColumn(report1Map.get("apples1"), 1);
        diffCols = model.getOutputColumns();
        assertEquals(3, diffCols.size());
        diffCol = (DiffCsvColumn) diffCols.get(2);
        checkColumn(model, diffCol, "apples1", "apples1", null);
        model.addReportColumn(report2Map.get("apples2"), 2);
        assertEquals(3, diffCols.size());
        diffCol = (DiffCsvColumn) diffCols.get(2);
        checkColumn(model, diffCol, "apples1", "apples1", "apples2");

        // change things so "pineapple" is matched up with "apples1"
        // should leave "apples2" unmatched at bottom
        model.addReportColumn(report2Map.get("pineapple"), 2);
        diffCols = model.getOutputColumns();
        assertEquals(4, diffCols.size());
        diffCol = (DiffCsvColumn) diffCols.get(3);
        checkColumn(model, diffCol, "pineapple", null, "pineapple");
        model.moveReportColumnUp(report2Map.get("pineapple"), 2);
        diffCols = model.getOutputColumns();
        assertEquals(4, diffCols.size());
        diffCol = (DiffCsvColumn) diffCols.get(2);
        checkColumn(model, diffCol, "apples1", "apples1", "pineapple");
        diffCol = (DiffCsvColumn) diffCols.get(3);
        checkColumn(model, diffCol, "apples2", null, "apples2");

        // move bottom report col down - should have no effect
        model.moveReportColumnDown(report1Map.get("apples1"), 1);
        checkColumn(model, (DiffCsvColumn) model.getOutputColumns().get(2), "apples1", "apples1", "pineapple");
        model.moveReportColumnDown(report2Cols.get(0), 2);
        checkColumn(model, (DiffCsvColumn) model.getOutputColumns().get(3), "apples2", null, "apples2");

        // move top col up - should have no effect
        model.moveReportColumnUp(report1Map.get("matched1"), 1);
        checkColumn(model, (DiffCsvColumn) model.getOutputColumns().get(0), "matched1", "matched1", "matched1");
        model.moveReportColumnUp(report2Map.get("matched1"), 2);
        checkColumn(model, (DiffCsvColumn) model.getOutputColumns().get(0), "matched1", "matched1", "matched1");

        // restore original "apples" match by removing "pineapple"
        model.removeReportColumn(report2Map.get("pineapple"), 2);
        diffCols = model.getOutputColumns();
        assertEquals(3, diffCols.size());

        // template
        List<AbstractCommonCsvColumn> template = model.getOutputColumns();
        CsvDiffModel model2 = new CsvDiffModel("test2", report1Cols, report2Cols);
        // initially the same as above
        diffCols = model2.getOutputColumns();
        assertEquals(2, diffCols.size());
        checkColumn(model2, (DiffCsvColumn) diffCols.get(0), "matched1", "matched1", "matched1");
        checkColumn(model2, (DiffCsvColumn) diffCols.get(1), "matched2", "matched2", "matched2");
        // template should add apples cols
        model2.applyTemplate(template);
        diffCols = model2.getOutputColumns();
        assertEquals(3, diffCols.size());
        checkColumn(model2, (DiffCsvColumn) diffCols.get(0), "matched1", "matched1", "matched1");
        checkColumn(model2, (DiffCsvColumn) diffCols.get(1), "matched2", "matched2", "matched2");
        checkColumn(model2, (DiffCsvColumn) diffCols.get(2), "apples1", "apples1", "apples2");
    }

}
