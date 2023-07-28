package replete.flux;

public enum VisualizerCombineMethod {
    TABS("Tabs"),
    VERTICAL_STACK("Vertically Stacked"),
    HORIZ_STACK("Horizontally Stacked"),
    INTEGRATED("Integrated");

    private String label;
    private VisualizerCombineMethod(String label) {
        this.label = label;
    }
    @Override
    public String toString() {
        return label;
    }
}
