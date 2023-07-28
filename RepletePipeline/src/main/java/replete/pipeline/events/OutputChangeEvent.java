package replete.pipeline.events;

import replete.pipeline.Stage;

public class OutputChangeEvent extends StageEvent {
    private String name;
    public OutputChangeEvent(Stage source, String name) {
        super(source);
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
