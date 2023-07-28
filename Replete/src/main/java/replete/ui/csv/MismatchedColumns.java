package replete.ui.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.text.StringUtil;

// Diff Template columns that aren't in report Files and report File columns that aren't in diff template
public class MismatchedColumns {

    Map<DiffReportId, List<AbstractCommonCsvColumn>> templateColsNotInReport = new HashMap<>();
    Map<DiffReportId, List<AbstractCommonCsvColumn>> reportColsNotInTemplate = new HashMap<>();

    public MismatchedColumns() {
        templateColsNotInReport.put(DiffReportId.OLDER, new ArrayList());
        templateColsNotInReport.put(DiffReportId.NEWER, new ArrayList());
        reportColsNotInTemplate.put(DiffReportId.OLDER, new ArrayList());
        reportColsNotInTemplate.put(DiffReportId.NEWER, new ArrayList());
    }

    public void addTemplateColNotInReport(DiffReportId reportIndex, AbstractCommonCsvColumn reportCol) {
        if (reportCol == null) {
            System.out.println("null col sent to addTemplateColNotInReport for " + reportIndex);
        } else {
            templateColsNotInReport.get(reportIndex).add(reportCol);
        }
    }

    public void addReportColNotInTemplate(DiffReportId reportIndex, FileCsvColumn fileCol) {
        if (fileCol == null) {
            System.out.println("null col sent to addReportColNotInTemplate for " + reportIndex);
        } else {
            reportColsNotInTemplate.get(reportIndex).add(fileCol);
        }
    }

    private void addCols(StringBuilder sb, List<AbstractCommonCsvColumn> cols, String str1, String str2) {
        if (!cols.isEmpty()) {
            sb.append(str1);
            sb.append(StringUtil.s(cols.size()));
            sb.append(str2);
                for (AbstractCommonCsvColumn col : cols) {
                    sb.append("\t" + col.getName() + "\n");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        addCols(sb, templateColsNotInReport.get(DiffReportId.OLDER),
            "Template expected column", " not found in older report file: \n");
        addCols(sb, templateColsNotInReport.get(DiffReportId.NEWER),
            "Template expected column", " not found in newer report file: \n");
        addCols(sb, reportColsNotInTemplate.get(DiffReportId.OLDER),
            "Older report had column", " not found in template: \n");
        addCols(sb, reportColsNotInTemplate.get(DiffReportId.NEWER),
            "Newer report had column", " not found in template: \n");
        return sb.toString();
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        MismatchedColumns result = new MismatchedColumns();

        System.out.println("initial report should be empty:  ");
        System.out.println(result.toString());

        result.addReportColNotInTemplate(DiffReportId.OLDER, new FileCsvColumn(DefaultCsvColumnType.DEFAULT,
            new FileCsvColumnInfo("report1col", "", 1)));
        System.out.println("extra report 1 col only:  ");
        System.out.println(result.toString());

        result.addReportColNotInTemplate(DiffReportId.OLDER, new FileCsvColumn(DefaultCsvColumnType.DEFAULT,
            new FileCsvColumnInfo("report1colToo", "", 1)));
        System.out.println("2 extra report 1 cols:  ");
        System.out.println(result.toString());

        result.addReportColNotInTemplate(DiffReportId.NEWER, new FileCsvColumn(DefaultCsvColumnType.DEFAULT,
            new FileCsvColumnInfo("report2col", "", 1)));
        System.out.println("extra report 1 & cols:  ");
        System.out.println(result.toString());

        result.addTemplateColNotInReport(DiffReportId.OLDER, new DiffCsvColumn(DefaultCsvColumnType.DEFAULT,
            new DiffCsvColumnInfo("matched"),
            new FileCsvColumn(DefaultCsvColumnType.DEFAULT, new FileCsvColumnInfo("matched1", "", 1)),
            new FileCsvColumn(DefaultCsvColumnType.DEFAULT, new FileCsvColumnInfo("matched2", "", 2))));
        System.out.println("added template col not in report 1:  ");
        System.out.println(result.toString());

    }
}
