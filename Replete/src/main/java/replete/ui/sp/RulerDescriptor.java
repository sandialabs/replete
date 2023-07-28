package replete.ui.sp;

public class RulerDescriptor {


    ////////////
    // FIELDS //
    ////////////

    private String category;
    private LineNumberRange lines;
    private String toolTip;
    private RulerClickListener clickListener;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RulerDescriptor(String toolTip, RulerClickListener clickListener) {
        this.toolTip = toolTip;
        this.clickListener = clickListener;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getCategory() {
        return category;
    }
    public LineNumberRange getLines() {
        return lines;
    }
    public String getToolTip() {
        return toolTip;
    }
    public RulerClickListener getClickListener() {
        return clickListener;
    }

    // Mutators (set by RowHeaderModel so user doesn't have to supply)

    public void setCategory(String category) {
        this.category = category;
    }
    public void setLines(LineNumberRange lines) {
        this.lines = lines;
    }
}
