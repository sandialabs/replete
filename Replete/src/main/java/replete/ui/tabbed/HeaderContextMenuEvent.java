package replete.ui.tabbed;

import java.awt.Component;

public class HeaderContextMenuEvent {


    ////////////
    // FIELDS //
    ////////////

    private Object key;
    private Component component;
    private int x;
    private int y;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HeaderContextMenuEvent(Object key, Component component, int x, int y) {
        this.key = key;
        this.component = component;
        this.x = x;
        this.y = y;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Object getKey() {
        return key;
    }
    public Component getComponent() {
        return component;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}
