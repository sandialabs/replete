package finio.platform.exts.view.consoleview.ui;

public enum ListenType {


    ////////////
    // VALUES //
    ////////////

    MAP_CHANGED("map-changed", "Map Changed"),
    MAP_CLEARED("map-cleared", "Map Cleared"),
    KEY_ADDED("key-added", "Key Added"),
    VALUE_CHANGED("value-changed", "Value Changed"),
    KEY_REMOVED("key-removed", "Key Removed"),
    KEY_CHANGED("key-changed", "Key Changed");


    ////////////
    // FIELDS //
    ////////////

    private String id;
    private String name;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    private ListenType(String id, String name) {
        this.id = id;
        this.name = name;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
