package replete.text.tableprinter;

public class ColumnDescriptor<T> {


    ////////////
    // FIELDS //
    ////////////

    private String name;
    private TableColumnAlignment alignment;
    private CellGenerator<T> cellGenerator;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ColumnDescriptor(String name, TableColumnAlignment alignment, CellGenerator<T> cellGenerator) {
        this.name = name;
        this.alignment = alignment;
        this.cellGenerator = cellGenerator;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getName() {
        return name;
    }
    public TableColumnAlignment getAlignment() {
        return alignment;
    }
    public CellGenerator<T> getCellGenerator() {
        return cellGenerator;
    }
}
