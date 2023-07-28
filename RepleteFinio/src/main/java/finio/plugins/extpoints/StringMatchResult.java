package finio.plugins.extpoints;

import finio.core.NonTerminal;

public class StringMatchResult {


    ////////////
    // FIELDS //
    ////////////

    private double probability;
    private NonTerminal Mdetails;    // For now, used in place of subclasses.


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public StringMatchResult(double probability, NonTerminal Mdetails) {
        this.probability = probability;
        this.Mdetails = Mdetails;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public double getProbability() {
        return probability;
    }
    public NonTerminal getDetails() {
        return Mdetails;
    }
}
