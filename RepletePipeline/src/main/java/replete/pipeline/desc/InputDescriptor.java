package replete.pipeline.desc;

import replete.pipeline.Pipeline;
import replete.pipeline.Stage;

public class InputDescriptor extends PortDescriptor {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final boolean DEFAULT_NULL_ALLOWED = true;
    private static final int CARDINALITY_MINIMUM_MINIMUM = 0;
    private static final int CARDINALITY_MAXIMUM_DEFAULT = 1;
    private static final int CARDINALITY_MAXIMUM_MINIMUM = 1;
    private static final int CARDINALITY_MINIMUM_NOT_REQUIRED = 0;
    private static final int CARDINALITY_MINIMUM_REQUIRED = 1;

    // Other

    private boolean nullAllowed;     // If input is required, can it be null?
    private int cardinalityMinimum;
    private int cardinalityMaximum;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public InputDescriptor(Stage parent, String name, String friendlyName,
                           String description, Class<?> type, boolean required) {
        this(parent, name, friendlyName, description, type, DEFAULT_NULL_ALLOWED,
            required ? CARDINALITY_MINIMUM_REQUIRED : CARDINALITY_MINIMUM_NOT_REQUIRED,
            CARDINALITY_MAXIMUM_DEFAULT
        );
    }
    public InputDescriptor(Stage parent, String name, String friendlyName,
                           String description, Class<?> type, int minCard, int maxCard) {
        this(parent, name, friendlyName, description, type, DEFAULT_NULL_ALLOWED, minCard, maxCard);
    }
    public InputDescriptor(Stage parent, String name, String friendlyName,
                           String description, Class<?> type, boolean nullAllowed, boolean required) {
        this(parent, name, friendlyName, description, type, nullAllowed,
            required ? CARDINALITY_MINIMUM_REQUIRED : CARDINALITY_MINIMUM_NOT_REQUIRED,
            CARDINALITY_MAXIMUM_DEFAULT
        );
    }
    public InputDescriptor(Stage parent, String name, String friendlyName,
                           String description, Class<?> type, boolean nullAllowed,
                           int minCard, int maxCard) {
        super(parent, name, friendlyName, description, type);
        setCardinality(minCard, maxCard);
        this.nullAllowed = nullAllowed;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Accessors

    public boolean isNullAllowed() {
        return nullAllowed;
    }
    public int getCardinalityMinimum() {
        return cardinalityMinimum;
    }
    public int getCardinalityMaximum() {
        return cardinalityMaximum;
    }

    // Accessors (computed)

    public boolean isRequired() {
        return cardinalityMinimum > 0;
    }

    // Mutators (private)

    private void setCardinality(int min, int max) {
        if(min < CARDINALITY_MINIMUM_MINIMUM || max < CARDINALITY_MAXIMUM_MINIMUM || min > max) {
            throw new IllegalArgumentException("Invalid input descriptor cardinality.");
        }
        cardinalityMinimum = min;
        cardinalityMaximum = max;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    public String toStringComplete() {
        return "InputDescriptor [parentId=" + parent.getId() + ", name=" + name + ", friendlyName=" +
            friendlyName + ", description=" + description + ", type=" + type + ", nullAllowed=" + nullAllowed +
            ", cardinalityMinimum=" + cardinalityMinimum + ", cardinalityMaximum=" +
            cardinalityMaximum + ", isRequired()=" + isRequired() + "]";
    }
    @Override
    public String toString() {
        return parent.getName() + Pipeline.PIPELINE_NAME_SEPARATOR + name;
    }
}
