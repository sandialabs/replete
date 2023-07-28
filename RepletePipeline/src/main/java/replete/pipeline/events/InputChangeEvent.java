package replete.pipeline.events;

import replete.pipeline.Stage;

public class InputChangeEvent extends StageEvent {
    private String name;

    public InputChangeEvent(Stage source, String name) {
        super(source);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
