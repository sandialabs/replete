package finio.core.events;

import finio.core.NonTerminal;

public class MapChangedEvent extends MapEvent {


    ///////////
    // FIELD //
    ///////////

    private MapEvent cause;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public MapChangedEvent(NonTerminal M, MapEvent cause) {
        super(M);
        this.cause = cause;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public MapEvent getCause() {
        return cause;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "MapChangedEvent[Map=" + M.toStringObject() + ", Cause=" + cause + "]";
    }
}
