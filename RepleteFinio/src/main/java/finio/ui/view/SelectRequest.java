package finio.ui.view;

public class SelectRequest {


    ////////////
    // FIELDS //
    ////////////

    private SelectionContext context;
    private SelectAction action;
    private Object[] args;


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
    public Object[] getArgs() {
        return args;
    }

    // Mutators (Builder)

    public SelectRequest setContext(SelectionContext context) {
        this.context = context;
        return this;
    }
    public SelectRequest setAction(SelectAction action) {
        this.action = action;
        return this;
    }
    public SelectRequest setArgs(Object... args) {
        this.args = args;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "context = \n" + context + "\naction = " + action;
    }
}
