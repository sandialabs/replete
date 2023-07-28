package replete.flux.viz;

public enum VisualizerType {
    JAVA_SWING("Java Swing"),
    JAVA_2D("Java 2D");

    private String label;
    private VisualizerType(String label) {
        this.label = label;
    }
    @Override
    public String toString() {
        return label;
    }
}
