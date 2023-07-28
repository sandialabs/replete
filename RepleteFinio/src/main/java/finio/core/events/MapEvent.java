package finio.core.events;

import finio.core.NonTerminal;

public class MapEvent {


    ///////////
    // FIELD //
    ///////////

    protected NonTerminal M;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public MapEvent(NonTerminal M) {
        this.M = M;
    }


    //////////////
    // ACCESSOR //
    //////////////

    public NonTerminal getSource() {
        return M;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "MapEvent[Map=" + M.toStringObject() + "]";
    }
}
