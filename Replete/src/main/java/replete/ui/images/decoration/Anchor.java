package replete.ui.images.decoration;

public enum Anchor {
    UPPER_LEFT   ("Upper Left"),
    UPPER_CENTER ("Upper Center"),
    UPPER_RIGHT  ("Upper Right"),
    MIDDLE_LEFT  ("Middle Left"),
    MIDDLE_CENTER("Middle Center"),
    MIDDLE_RIGHT ("Middle Right"),
    LOWER_LEFT   ("Lower Left"),
    LOWER_CENTER ("Lower Center"),
    LOWER_RIGHT  ("Lower Right");

    private String label;

    private Anchor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
