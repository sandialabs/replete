package replete.ui.params.hier.test;


// Catch-all rule to be used for root rule nodes, for example.

public class AllRule extends Rule {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean appliesTo(String url) {
        return true;
    }
}
