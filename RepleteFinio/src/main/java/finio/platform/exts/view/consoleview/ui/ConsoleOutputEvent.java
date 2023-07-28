package finio.platform.exts.view.consoleview.ui;

public class ConsoleOutputEvent {
    private String output;
    public ConsoleOutputEvent(String output) {
        super();
        this.output = output;
    }
    public String getOutput() {
        return output;
    }
}
