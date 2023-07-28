package finio.core.events;

import finio.core.NonTerminal;

public class ValueChangedEvent extends KeyEvent {


    ////////////
    // FIELDS //
    ////////////

    private Object Vold;
    private Object Vnew;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ValueChangedEvent(NonTerminal M, Object K, Object Vo, Object Vn) {
        super(M, K);
        Vold = Vo;
        Vnew = Vn;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Object getOldV() {
        return Vold;
    }
    public Object getNewV() {
        return Vnew;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "ValueChangedEvent[Map=" + M.toStringObject() +
            ", Key=" + K + ", Old Value=" + toStringValue(Vold) +
            ", New Value=" + toStringValue(Vnew) + "]";
    }
}
