package finio.ui.view;

public class ExpandRequest {


    ////////////
    // FIELDS //
    ////////////

    private SelectionContext context;
    private SelectAction action;
    private int level;
    private Object[] args;
    private boolean all;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public SelectionContext getContext() {
        return context;
    }
    public SelectAction getAction() {
        return action;
    }
    public int getLevel() {
        return level;
    }
    public Object[] getArgs() {
        return args;
    }
    public boolean isAll() {
        return all;
    }

    // Mutators (Builder)

    public ExpandRequest setContext(SelectionContext context) {
        this.context = context;
        return this;
    }
    public ExpandRequest setAction(SelectAction action) {
        this.action = action;
        return this;
    }
    public ExpandRequest setLevel(int level) {
        this.level = level;
        return this;
    }
    public ExpandRequest setArgs(Object... args) {
        this.args = args;
        return this;
    }
    public ExpandRequest setAll(boolean all) {
        this.all = all;
        return this;
    }
}
