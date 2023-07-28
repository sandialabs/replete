package replete.cli.argfile;

import java.io.Serializable;

public class ArgFileConfig implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private String optionName = "argfile";
    private String pathsRelToFileOptionName;
    private String lineCommentToken = "#";           // Can be set to null if line comments are not to be allowed
    //private String tokenGroupingSym = "\"";        // TODO: Not implemented yet


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getOptionName() {
        return optionName;
    }
    public String getPathsRelToFileOptionName() {
        return pathsRelToFileOptionName;
    }
    public String getLineCommentToken() {
        return lineCommentToken;
    }

    // Mutators

    public ArgFileConfig setOptionName(String optionName) {
        this.optionName = optionName;
        return this;
    }
    public ArgFileConfig setPathsRelToFileOptionName(String pathsRelToFileOptionName) {
        this.pathsRelToFileOptionName = pathsRelToFileOptionName;
        return this;
    }
    public ArgFileConfig setLineCommentToken(String lineCommentToken) {
        this.lineCommentToken = lineCommentToken;
        return this;
    }
}
