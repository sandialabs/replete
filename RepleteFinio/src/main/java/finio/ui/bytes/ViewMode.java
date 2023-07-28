package finio.ui.bytes;

public enum ViewMode {
    DEC_UNSIGNED("Decimal (Unsigned)"),
    DEC_SIGNED("Decimal (Signed)"),
    OCTAL("Octal"),
    HEX_LOWER("Hex (Lower)"),
    HEX_UPPER("Hex (Upper)"),
    BINARY_DOTS("Binary (Dots)"),
    BINARY_01("Binary (Digits)"),
    ASCII_127("ASCII (0-127)");

    private String label;

    private ViewMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
