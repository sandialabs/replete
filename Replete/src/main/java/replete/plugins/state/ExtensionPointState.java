package replete.plugins.state;

import java.io.Serializable;

public class ExtensionPointState implements Serializable {
    private String id;

    public ExtensionPointState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
