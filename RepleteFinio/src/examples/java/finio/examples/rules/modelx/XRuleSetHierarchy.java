package finio.examples.rules.modelx;

public class XRuleSetHierarchy {


    ///////////
    // FIELD //
    ///////////

    public XRuleSet root;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public XRuleSet getRoot() {
        return root;
    }

    // Mutators (Builder)

    public XRuleSetHierarchy setRoot(XRuleSet root) {
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
