package replete.ui.csv;

public class DiffCsvColumn extends AbstractCommonCsvColumn {

    ////////////
    // FIELDS //
    ////////////

    private FileCsvColumn report1Column;
    private FileCsvColumn report2Column;
    private boolean isKey = false;
    private boolean doDiff = false;     // is this column relevant for diffing values?
    private String patternWithTag = "";
    private int numChanged = 0;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DiffCsvColumn(CsvColumnType type, DiffCsvColumnInfo info,
                         FileCsvColumn report1Column, FileCsvColumn report2Column) {
        // it is allowable to create a new DiffCsvColumn with either column = null;
        // indicates that matching column will be added later
        super(type, info);
        this.report1Column = report1Column;
        this.report2Column = report2Column;
    }

    public boolean isKey() {
        return isKey;
    }

    public boolean doDiff() {
        return doDiff;
    }

    public String getPattern() {
        return patternWithTag;
    }

    public FileCsvColumn getReport1Column() {
        return report1Column;
    }

    public FileCsvColumn getReport2Column() {
        return report2Column;
    }

    public FileCsvColumn getReportColumn(int index) {
        if (index == 1) {
            return report1Column;
        }
        if (index==2) {
            return report2Column;
        }
        return null;
    }

    public int getNumChanged() {
        return numChanged;
    }

    // setters

    public void setColumn(int index, FileCsvColumn col) {
        if (index == 1) {
            report1Column = col;
        } else if (index == 2) {
            report2Column = col;
        }
    }

    public void removeColumn(int index, FileCsvColumn col) {
        if (index == 1 && report1Column==col) {
            report1Column = null;
        } else if (index == 2 && report2Column==col) {
            report2Column = null;
        }
    }

    public void setIsKey(boolean isKey) {
        this.isKey = isKey;
    }

    public void setDoDiff(boolean doDiff) {
        this.doDiff = doDiff;
    }

    public void setPattern(String patternWithTag) {
        this.patternWithTag = patternWithTag;
    }

    public void setName(String name) {
        DiffCsvColumnInfo info = (DiffCsvColumnInfo) getInfo();
        info.setName(name);
    }

    // default name is taken from report1 col or report2 col if report1 is null;
    // as report cols are changed, this may need to be adjusted
    public void fixName()  {
        if (report1Column==null && report2Column==null) {
            // probably deleting this col soon...
            setName("");
        } else if (report1Column==null) {
            setName(report2Column.getName());
        } else if (report2Column==null) {
            setName(report1Column.getName());
        }
    }

    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Object getCellData(Object source) throws Exception {
        // This shouldn't currently be called
        // TODO - use this to get the diff between report 1 data and report 2 data?
        return null;
    }

    @Override
    public String toString() {
        return info.getName();
    }
}
