package replete.pipeline.events;

import replete.pipeline.Stage;

public class StageEvent {
    private Stage source;
    public StageEvent(Stage source) {
        this.source = source;
    }
    public Stage getSource() {
        return source;
    }
}
