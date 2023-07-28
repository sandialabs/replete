package replete.ui.uiaction;

import javax.swing.JComponent;

public class HoverEvent {


    ////////////
    // FIELDS //
    ////////////

    private String id;
    private String type;
    private JComponent component;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HoverEvent(String id, String type, JComponent component) {
        this.id = id;
        this.type = type;
        this.component = component;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public JComponent getComponent() {
        return component;
    }
}
