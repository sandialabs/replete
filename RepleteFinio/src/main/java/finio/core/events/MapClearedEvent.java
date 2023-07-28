package finio.core.events;

import finio.core.NonTerminal;

public class MapClearedEvent extends MapEvent {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public MapClearedEvent(NonTerminal M) {
        super(M);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "MapClearedEvent[Map=" + M.toStringObject() + "]";
    }
}
