package finio.core.events;

import finio.core.NonTerminal;

public class KeyChangedEvent extends KeyEvent {


    ///////////
    // FIELD //
    ///////////

    private Object Knew;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public KeyChangedEvent(NonTerminal M, Object Kold, Object Knew) {
        super(M, Kold);
        this.Knew = Knew;
    }


    //////////////
    // ACCESSOR //
    //////////////

    public Object getOldK() {
        return getK();
    }
    public Object getNewK() {
        return Knew;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "KeyChangedEvent[Map=" + M.toStringObject() +
            ", Old Key=" + K + ", New Key=" + Knew + "]";
    }
}
