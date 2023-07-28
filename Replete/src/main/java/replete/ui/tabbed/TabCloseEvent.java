package replete.ui.tabbed;

import java.awt.Component;

public class TabCloseEvent {


    ////////////
    // FIELDS //
    ////////////

    private int index;
    private Object key;
    private Component component;
    // header panel?


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TabCloseEvent(int index, Object key, Component component) {
        this.index = index;
        this.key = key;
        this.component = component;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getIndex() {
        return index;
    }
    public Object getKey() {
        return key;
    }
    public Component getComponent() {
        return component;
    }
}
