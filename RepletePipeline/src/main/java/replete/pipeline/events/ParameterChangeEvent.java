package replete.pipeline.events;

import replete.pipeline.Stage;


public class ParameterChangeEvent extends StageEvent {


    ///////////
    // FIELD //
    ///////////

    private String parameterName;
    private Object oldValue;
    private Object newValue;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ParameterChangeEvent(Stage stage, String parameterName, Object oldValue, Object newValue) {
        super(stage);
        this.parameterName = parameterName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getParameterName() {
        return parameterName;
    }
    public Object getOldValue() {
        return oldValue;
    }
    public Object getNewValue() {
        return newValue;
    }
}
