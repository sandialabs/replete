package finio.appstate;

public class AppStateChangeEvent {


    ////////////
    // FIELDS //
    ////////////

    public String name;
    public Object prev;
    public Object curr;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public AppStateChangeEvent(String name, Object prev, Object curr) {
        this.name = name;
        this.prev = prev;
        this.curr = curr;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getName() {
        return name;
    }
    public Object getPrev() {
        return prev;
    }
    public Object getCurr() {
        return curr;
    }
}
