package replete.threads;

public enum UehType {
    SELF_FIELD("S/F"),   // Thread has an internal UEH using an instance variable
    SELF_CUSTOM("S/C"),  // Thread is not returning a UEH using an instance variable nor is it returning its own group (custom getUncaughtExceptionHandler impl)
    GROUP_PARENT("G>P"),       // Thread has no internal UEH so it delegates to the group, which in turn has a parent group to which it will delegate
    CUSTOM_GROUP("G/C"), // Thread is using its group for its UEH but the group is not ThreadGroup - so it *could* have custom handling with an overridden uncaughtException
    DEFAULT("D"),
    NOTHING(""),
    EMPTY("!");

    private String label;

    private UehType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
