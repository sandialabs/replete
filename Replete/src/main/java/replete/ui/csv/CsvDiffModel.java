package replete.ui.csv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Manages a list of columns to use in csv diff, where each column needs:
//  the FileCsvColumn to use for report 1
//  the FileCsvColumn to use for report 2
//  other info about whether to check for value changes, values to exclude from the diff, etc.
public class CsvDiffModel implements Serializable {

    ////////////
    // FIELDS //
    ////////////

    private String name;
    private List<DiffCsvColumn> matchedColumns = new ArrayList();
    private transient List<AbstractCommonCsvColumn> report1Columns = new ArrayList();
    private transient List<AbstractCommonCsvColumn> report2Columns = new ArrayList();

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public CsvDiffModel(String name,
                        List<AbstractCommonCsvColumn> report1Cols,
                        List<AbstractCommonCsvColumn> report2Cols) {
        this.name = name;
        report1Columns.addAll(report1Cols);
        report2Columns.addAll(report2Cols);
        setDefaultModel();
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // accessors

    public String getName() {
        return name;
    }

    public List<AbstractCommonCsvColumn> getOutputColumns() {
        List<AbstractCommonCsvColumn> cols = new ArrayList<>();
        for (DiffCsvColumn col : matchedColumns) {
            cols.add(col);
        }
        return cols;
    }

    public List<String> getDataInOutputOrder(String[] row, int reportIndex) throws Exception {
        return getOutputColumns().stream()
            .map(DiffCsvColumn.class::cast)
            .map(c -> c.getReportColumn(reportIndex).getCellData(row))
            .collect(Collectors.toList())
        ;
    }

    public List<AbstractCommonCsvColumn> getReportDiffColumns(int reportIndex) {
        List<AbstractCommonCsvColumn> result = new ArrayList();
        boolean eol = false;
        for (DiffCsvColumn col : matchedColumns) {
            if (col.getReportColumn(reportIndex) != null) {
                result.add(col.getReportColumn(reportIndex));
            }
        }
        return result;
    }

    public String getOutputName(AbstractCommonCsvColumn col) {
        return getDiffCol((FileCsvColumn)col).getName();
    }

    public boolean isKey(AbstractCommonCsvColumn col) {
        return getDiffCol((FileCsvColumn)col).isKey();
    }

    public boolean getDoValueCheck(AbstractCommonCsvColumn col) {
        return getDiffCol((FileCsvColumn)col).doDiff();
    }

    public String getPattern(AbstractCommonCsvColumn col) {
        return getDiffCol((FileCsvColumn)col).getPattern();
    }

    private DiffCsvColumn getDiffCol(AbstractCommonCsvColumn column1, AbstractCommonCsvColumn column2) {
        if (!(column1 instanceof FileCsvColumn) || !(column2 instanceof FileCsvColumn) ) {
            return null;
        }
        FileCsvColumn fileCol1 = (FileCsvColumn) column1;
        DiffCsvColumn diffCol1 = getDiffCol(fileCol1);
        FileCsvColumn fileCol2 = (FileCsvColumn) column2;
        DiffCsvColumn diffCol2 = getDiffCol(fileCol2);
        if (diffCol1 == diffCol2) {
            return diffCol1;
        } else {
            return null;
        }
    }

    private DiffCsvColumn getDiffCol(FileCsvColumn col) {
        for (DiffCsvColumn diffCol : matchedColumns) {
            if (diffCol.getReport1Column()!=null && diffCol.getReport1Column()==col) {
                return diffCol;
            }
            if (diffCol.getReport2Column()!=null && diffCol.getReport2Column()==col) {
                return diffCol;
            }
        }
        return null;
    }

    private AbstractCommonCsvColumn findColumnByName(List<? extends AbstractCommonCsvColumn> colList, String name) {
        for (AbstractCommonCsvColumn col : colList) {
            if (col.getName().equals(name)) {
                return col;
            }
        }
        return null;
    }

    private int getIndexOfLastNonNullReportCol(int report) {
        for (int index = matchedColumns.size()-1; index>=0; index--) {
            if (matchedColumns.get(index).getReportColumn(report) != null) {
                return index;
            }
        }
        return -1;
    }

    // setters

    public MismatchedColumns applyTemplate(List<AbstractCommonCsvColumn> columns) throws Exception {
        MismatchedColumns result = new MismatchedColumns();
        // We already have a default model at this point
        // Clear the current diff cols, but keep the report cols to compare to template
        if (!columns.isEmpty()) {
            matchedColumns.clear();
        }
        for (AbstractCommonCsvColumn column : columns) {
            if (column instanceof DiffCsvColumn) {
                DiffCsvColumn diffCol = (DiffCsvColumn)column;
                // get the references to the actual report1 and report2 file cols; indices could differ
                AbstractCommonCsvColumn report1Col = findColumnByName(report1Columns, diffCol.getReport1Column().getName());
                if (report1Col == null && diffCol.getReport1Column() != null) {
                    result.addTemplateColNotInReport(DiffReportId.OLDER, diffCol.getReport1Column());
                } else {
                    diffCol.setColumn(1, (FileCsvColumn) report1Col);
                }
                AbstractCommonCsvColumn report2Col = findColumnByName(report2Columns, diffCol.getReport2Column().getName());
                if (report2Col == null && diffCol.getReport2Column() != null) {
                    result.addTemplateColNotInReport(DiffReportId.NEWER, diffCol.getReport2Column());
                } else {
                    diffCol.setColumn(2, (FileCsvColumn) report2Col);
                }
                if (report1Col != null && report2Col != null) {
                    matchedColumns.add(diffCol);
                }
            } else {
                throw new Exception("Can't parse CSV diff template");
            }
        }
        // check for report columns that have no corresponding diff col in template
        for (AbstractCommonCsvColumn col : report1Columns) {
            FileCsvColumn fileCol = (FileCsvColumn) col;
            if (findColumnByName(columns, col.getName()) == null) {
                result.addReportColNotInTemplate(DiffReportId.OLDER, fileCol);
            }
        }
        for (AbstractCommonCsvColumn col : report2Columns) {
            FileCsvColumn fileCol = (FileCsvColumn) col;
            if (findColumnByName(columns, col.getName()) == null) {
                result.addReportColNotInTemplate(DiffReportId.NEWER, fileCol);
            }
        }
        return result;
    }

    private void setDefaultModel() {
        boolean haveKey = false;
        for (AbstractCommonCsvColumn col1 : report1Columns) {
            AbstractCommonCsvColumn col2 = findColumnByName(report2Columns, col1.getName());
            // make the first pair of corresponding columns the default key
            if (col2 != null) {
                DiffCsvColumn outCol = new DiffCsvColumn(col1.getType(),
                    new DiffCsvColumnInfo(col1.getName()),
                    (FileCsvColumn) col1, (FileCsvColumn) col2);
                matchedColumns.add(outCol);
                if (!haveKey) {
                    outCol.setIsKey(true);
                    haveKey = true;
                    outCol.setDoDiff(false);
                }
            }
        }
    }

    public boolean setColumnName(AbstractCommonCsvColumn col, String name) {
        DiffCsvColumn diffCol = getDiffCol((FileCsvColumn) col);
        if (diffCol == null) {
            return false;
        }
        diffCol.setName(name);
        return true;
     }

    public boolean setKeyColumn(AbstractCommonCsvColumn col) {
        DiffCsvColumn diffCol = getDiffCol((FileCsvColumn) col);
        if (diffCol == null) {
            return false;
        }
        setKeyColumn(diffCol);
        return true;
    }

    private void setKeyColumn(DiffCsvColumn keyCol) {
        // only one key; remove any existing setting
        for (DiffCsvColumn col : matchedColumns) {
            col.setIsKey(false);
        }
        keyCol.setIsKey(true);
        // no need to diff values on key column
        keyCol.setDoDiff(false);
    }

    public boolean setDoValueCheck(List<AbstractCommonCsvColumn> cols, boolean checkValues) {
        for (AbstractCommonCsvColumn col : cols) {
            DiffCsvColumn diffCol = getDiffCol((FileCsvColumn) col);
            if (diffCol == null) {
                return false;
            }
            diffCol.setDoDiff(checkValues);
        }
        return true;
    }

    public boolean setPattern(List<AbstractCommonCsvColumn> cols, String patternWithTag) {
        for (AbstractCommonCsvColumn col : cols) {
            DiffCsvColumn diffCol = getDiffCol((FileCsvColumn) col);
            if (diffCol == null) {
                return false;
            }
            diffCol.setPattern(patternWithTag);
            FileCsvColumn fileCol = diffCol.getReport1Column();
            fileCol.setExcludedValues(patternWithTag);
            fileCol = diffCol.getReport2Column();
            fileCol.setExcludedValues(patternWithTag);
        }
        return true;
     }

    public void addReportColumn(AbstractCommonCsvColumn column, int reportIndex) {
        FileCsvColumn fileCol = (FileCsvColumn) column;
        if (matchedColumns.isEmpty()) {
            addUnmatchedDiffCol(fileCol, reportIndex);
        } else {
            DiffCsvColumn lastDiffCol = matchedColumns.get(matchedColumns.size()-1);
            // if last 'matched' diffCol doesn't have a fileCol for this reportIndex,
            // add this report column to that diff col
            // otherwise, create a new diffCol with a null report1 col
            if (lastDiffCol.getReportColumn(reportIndex) == null) {
                lastDiffCol.setColumn(reportIndex, fileCol);
            } else {
                addUnmatchedDiffCol(fileCol, reportIndex);
            }
        }
    }

    public void addUnmatchedDiffCol(FileCsvColumn column, int reportIndex) {
        if (reportIndex==1) {
            addDiffCol(column, null);
        } else if (reportIndex == 2) {
            addDiffCol(null, column);
        }
    }

    public void removeReportColumn(AbstractCommonCsvColumn column, int reportIndex) {
        FileCsvColumn fileCol = (FileCsvColumn) column;
        DiffCsvColumn currentDiffCol = getDiffCol(fileCol);
        currentDiffCol.removeColumn(reportIndex, fileCol);
        // This requires shifting all the appropriate report cols in any diff cols below this one
        int startIndex = matchedColumns.indexOf(currentDiffCol) + 1;
        int stopIndex = matchedColumns.size() - 1;
        for (int ii=startIndex; ii<=stopIndex; ii++) {
            moveReportColumnUp(matchedColumns.get(ii).getReportColumn(reportIndex), reportIndex);
        }
        DiffCsvColumn lastCol = matchedColumns.get(stopIndex);
        if (lastCol.getReportColumn(1)==null && lastCol.getReportColumn(2)==null) {
            matchedColumns.remove(lastCol);
        }
    }

    public void removePairedColumns(AbstractCommonCsvColumn column1, AbstractCommonCsvColumn column2) {
        DiffCsvColumn diffCol = getDiffCol(column1, column2);
        if (diffCol != null) {
            matchedColumns.remove(diffCol);
        }
    }

    public void removeOutputColumn(AbstractCommonCsvColumn column) {
        if (column instanceof DiffCsvColumn) {
            DiffCsvColumn diffCol = (DiffCsvColumn) column;
            diffCol.setIsKey(false);
            diffCol.setDoDiff(false);
            matchedColumns.remove(diffCol);
        }
    }

    public void moveReportColumnUp(AbstractCommonCsvColumn column, int reportIndex) {
        FileCsvColumn movingReportCol = (FileCsvColumn) column;
        DiffCsvColumn currentDiffCol = getDiffCol(movingReportCol);
        int currentIndex = matchedColumns.indexOf(currentDiffCol);
        if (currentIndex > 0) {
            DiffCsvColumn targetDiffCol = matchedColumns.get(currentIndex-1);
            FileCsvColumn displacedReportCol = targetDiffCol.getReportColumn(reportIndex);
            currentDiffCol.setColumn(reportIndex, displacedReportCol);
            targetDiffCol.setColumn(reportIndex, movingReportCol);
            currentDiffCol.fixName();
        }
    }

    public void moveReportColumnDown(AbstractCommonCsvColumn column, int reportIndex) {
        FileCsvColumn movingReportCol = (FileCsvColumn) column;
        DiffCsvColumn currentDiffCol = getDiffCol(movingReportCol);
        int currentIndex = matchedColumns.indexOf(currentDiffCol);
        if (currentIndex < matchedColumns.size()-1 &&
                        currentIndex != getIndexOfLastNonNullReportCol(reportIndex)) {
            DiffCsvColumn targetDiffCol = matchedColumns.get(currentIndex+1);
            FileCsvColumn displacedReportCol = targetDiffCol.getReportColumn(reportIndex);
            currentDiffCol.setColumn(reportIndex, displacedReportCol);
            targetDiffCol.setColumn(reportIndex, movingReportCol);
            targetDiffCol.fixName();
        }
    }

    public void movePairedColumnsUp(AbstractCommonCsvColumn column1, AbstractCommonCsvColumn column2) {
        DiffCsvColumn diffCol = getDiffCol(column1, column2);
        if (diffCol != null) {
            int currentIndex = matchedColumns.indexOf(diffCol);
            if (currentIndex > 0) {
                DiffCsvColumn removedColumn = matchedColumns.remove(currentIndex);
                matchedColumns.add(currentIndex-1, removedColumn);
            }
        }
    }

    public void movePairedColumnsDown(AbstractCommonCsvColumn column1, AbstractCommonCsvColumn column2) {
        DiffCsvColumn diffCol = getDiffCol(column1, column2);
        if (diffCol != null) {
            int currentIndex = matchedColumns.indexOf(diffCol);
            if (currentIndex < matchedColumns.size()-1) {
                DiffCsvColumn removedColumn = matchedColumns.remove(currentIndex);
                matchedColumns.add(currentIndex+1, removedColumn);
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        for (DiffCsvColumn col : matchedColumns) {
            headers.add(col.getName());
        }
        return headers;
    }

    public DiffCsvColumn getKeyColumn() {
        for (DiffCsvColumn col : matchedColumns) {
            if (col.isKey()) {
                return col;
            }
        }
        return null;
    }

    private boolean hasKey() {
        for (DiffCsvColumn col : matchedColumns) {
            if (col.isKey() && col.getReport1Column() != null && col.getReport2Column() != null) {
                return true;
            }
        }
        return false;
    }

    public String preCheck() {
        if (!hasKey()) {
            return "A key column must be specified to determine which newer report rows correspond to older report rows\n";
        }
        return "";
    }

    public boolean isReady() {
        return preCheck().equals("");
    }

    private void addDiffCol(FileCsvColumn col1, FileCsvColumn col2) {
        DiffCsvColumnInfo info = col1==null ? new DiffCsvColumnInfo(col2.getInfo().getName()) :
            new DiffCsvColumnInfo(col1.getInfo().getName());
        CsvColumnType type = col1==null ? col2.getType() : col1.getType();
        matchedColumns.add(new DiffCsvColumn(type, info, col1, col2));
    }
}
