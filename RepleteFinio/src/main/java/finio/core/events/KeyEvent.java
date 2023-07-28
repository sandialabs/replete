package finio.core.events;

import finio.core.NonTerminal;

public class KeyEvent extends MapEvent {


    ///////////
    // FIELD //
    ///////////

    protected Object K;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public KeyEvent(NonTerminal M, Object K) {
        super(M);
        this.K = K;
    }


    //////////////
    // ACCESSOR //
    //////////////

    public Object getK() {
        return K;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "KeyEvent[Map=" + M + ", Key=" + K + "]";
    }

    protected String toStringValue(Object V) {
        if(V instanceof NonTerminal) {
            return ((NonTerminal) V).toStringObject();
        }
        return V.toString();
    }
}
