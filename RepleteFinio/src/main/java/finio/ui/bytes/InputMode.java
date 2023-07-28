package finio.ui.bytes;

public enum InputMode {
    DECIMAL("Decimal"),
    OCTAL("Octal"),
    HEX("Hex"),
    BINARY("Binary"),
    ASCII("ASCII (0-127)");

    private String label;

    private InputMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
