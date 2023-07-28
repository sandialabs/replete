package replete.logging;

public class LogCodeDynamic {


    ////////////
    // FIELDS //
    ////////////

    private String category;
    private String code;
    private String description;
    private boolean important;

    //private String loggerName;    // Does not exist in a dynamic log code (found at runtime)


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public LogCodeDynamic(String category, String code, String description) {
       this(category, code, description, false);
    }
    public LogCodeDynamic(String category, String code, String description, boolean important) {
        this.category    = category;
        this.code        = code;
        this.description = description;
        this.important   = important;
    }


    // No need for accessors/mutators


    ////////////
    // CREATE //
    ////////////

    public LogCode create(Class<?> clazz) {
        return LogCodeManager.create(category, clazz, code, description, important);
    }
}
