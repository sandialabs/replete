package finio.examples.rules.model;

public class RuleSetHierarchy {


    ///////////
    // FIELD //
    ///////////

    public RuleSetNode root;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public RuleSetNode getRoot() {
        return root;
    }

    // Mutators (Builder)

    public RuleSetHierarchy setRoot(RuleSetNode root) {
        this.root = root;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return root.toString(0);
    }
}
