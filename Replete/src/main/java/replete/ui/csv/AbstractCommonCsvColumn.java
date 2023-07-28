package replete.ui.csv;

import java.io.Serializable;

import javax.swing.ImageIcon;

import replete.text.StringUtil;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageLib;

public abstract class AbstractCommonCsvColumn implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private CsvColumnType type;
    protected CsvColumnInfo info;

    private String overriddenName = "";
    private boolean importantColumn = false;
    private boolean sortedColumn = false;
    protected boolean sortable = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AbstractCommonCsvColumn(CsvColumnType type, CsvColumnInfo info) {
        this.type = type;
        this.info = info;

        // Most columns today result in Strings, so we'll assume that they're sortable.
        sortable = true;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public CsvColumnType getType() {
        return type;
    }
    public CsvColumnInfo getInfo() {
        return info;
    }
    public String getName() {
        if (info == null || info.getName() == null) {
            return "";
        }
        return info.getName();
    }
    public String getDescription() {
        return info.getDescription();
    }
    public String getOverriddenName() {
        return overriddenName;
    }
    public boolean isImportantColumn() {
        return importantColumn;
    }
    public boolean isSortedColumn() {
        return sortedColumn;
    }
    public boolean isSortable() {
        return sortable;
    }

    // Accessors (Computed)

    public String getResolvedName() {
        if(!StringUtil.isBlank(overriddenName)) {
            return overriddenName;
        }
        return info.getName();
    }
    public ImageIcon getIcon() {
        return ImageLib.get(RepleteImageModel.CSV_COL_OTHER);
    }

    // Mutators

    public void setOverriddenName(String overriddenName) {
        this.overriddenName = overriddenName;
    }
    public void setImportantColumn(boolean importantColumn) {
        this.importantColumn = importantColumn;
    }
    public void setSortedColumn(boolean sortedColumn) {
        this.sortedColumn = sortedColumn;
    }


    //////////////
    // ABSTRACT //
    //////////////

    public abstract Object getCellData(Object source) throws Exception;


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(info.getName());
        sb.append(" (");
        sb.append(info.getDescription());
        sb.append(")");
        return sb.toString();
    }
}
