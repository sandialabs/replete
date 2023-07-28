package finio.core.events;

import finio.core.NonTerminal;

public class MapBatchChangedEvent extends MapEvent {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public MapBatchChangedEvent(NonTerminal M) {
        super(M);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "MapBatchChangedEvent[Map=" + M.toStringObject() + "]";
    }
}
