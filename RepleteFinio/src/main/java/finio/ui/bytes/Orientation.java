package finio.ui.bytes;

public enum Orientation {
    HORIZONTAL("Horizontal"),
    VERTICAL("Vertical");

    private String label;

    private Orientation(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
