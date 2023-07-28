package replete.conf;

public class ConfigurationIssue {

    ///////////
    // ENUMS //
    ///////////

    public enum Level {
        ERROR("Error"),
        WARNING("Warning"),
        INFO("Info"),
        DEBUG("Debug");

        private String description;
        private Level(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }
    }

    public enum Type {
        MALFORMED("Malformed Configuration"),
        DEPRECATED("Deprecated Configuration"),
        VIOLATED_RULE("Violated Validation Rule");

        private String description;
        private Type(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }
    }

    ////////////
    // FIELDS //
    ////////////

    private Level level;
    private Type type;
    private String message;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConfigurationIssue(Level level, Type type, String message) {
        this.level = level;
        this.type = type;
        this.message = message;
    }

    /////////////
    // GETTERS //
    /////////////

    public Level getLevel() {
        return level;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(level.getDescription()).append("\t");
        sb.append(type.getDescription()).append("\t");
        sb.append(message);
        return sb.toString();
    }
}
