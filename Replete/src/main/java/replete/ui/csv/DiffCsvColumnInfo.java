package replete.ui.csv;

public class DiffCsvColumnInfo implements CsvColumnInfo{

    private String name;
    private String description; // not really used...

    public DiffCsvColumnInfo(String name) {
        this.name = name;
        description = "";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ");
        sb.append(getName());
        sb.append("; description: ");
        sb.append(getDescription());
        return sb.toString();
    }
}
