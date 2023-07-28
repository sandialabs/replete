package finio.core.events;

import finio.core.NonTerminal;

public class KeyAddedEvent extends KeyEvent {


    ///////////
    // FIELD //
    ///////////

    private Object V;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public KeyAddedEvent(NonTerminal M, Object K, Object V) {
        super(M, K);
        this.V = V;
    }


    //////////////
    // ACCESSOR //
    //////////////

    public Object getV() {
        return V;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "KeyAddedEvent[Map=" + M.toStringObject() +
            ", Key=" + K + ", Value=" + toStringValue(V) + "]";
    }
}
