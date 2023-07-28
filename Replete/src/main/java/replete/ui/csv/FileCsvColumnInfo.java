package replete.ui.csv;

public class FileCsvColumnInfo implements CsvColumnInfo{

    private String name;
    private String description;
    private int index;  // of this column in the original file

    public FileCsvColumnInfo(String name, String description, int index) {
        this.name = name;
        this.description = description;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ");
        sb.append(getName());
        sb.append("; description: ");
        sb.append(getDescription());
        sb.append("; index: ");
        sb.append(getIndex());
        return sb.toString();
    }
}
