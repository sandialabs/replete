package replete.cli.options;

import java.io.File;

import replete.cli.errors.InvalidPathException;

/**
 * An option that expects a string value that will be
 * validated to be a path that matches some criteria.
 *
 * @author Derek Trumbo
 */

public class PathOption extends Option<File> {


    ////////////
    // FIELDS //
    ////////////

    private boolean mustExist;
    private boolean parentMustExist;
    private boolean mustBeDirectory;
    private boolean mustBeFile;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PathOption(char shortForm) {
        this("" + shortForm, null, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public PathOption(String longForm) {
        this(null, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public PathOption(char shortForm, String longForm) {
        this("" + shortForm, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public PathOption(char shortForm, boolean required) {
        this("" + shortForm, null, required, DEFAULT_ALLOW_MULTI);
    }
    public PathOption(String longForm, boolean required) {
        this(null, longForm, required, DEFAULT_ALLOW_MULTI);
    }
    public PathOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        this("" + shortForm, longForm, required, allowMulti);
    }
    public PathOption(String shortForm, String longForm, boolean required, boolean allowMulti) {
        super(shortForm, longForm, true, required, allowMulti);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isMustExist() {
        return mustExist;
    }
    public boolean isParentMustExist() {
        return parentMustExist;
    }
    public boolean isMustBeDirectory() {
        return mustBeDirectory;
    }
    public boolean isMustBeFile() {
        return mustBeFile;
    }

    // Mutators (Builder)

    public PathOption setMustExist(boolean mustExist) {
        this.mustExist = mustExist;
        return this;
    }
    public PathOption setParentMustExist(boolean parentMustExist) {
        this.parentMustExist = parentMustExist;
        return this;
    }
    public PathOption setMustBeDirectory(boolean mustBeDirectory) {
        this.mustBeDirectory = mustBeDirectory;
        return this;
    }
    public PathOption setMustBeFile(boolean mustBeFile) {
        this.mustBeFile = mustBeFile;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getValueType() {    // TODO: rethink why this option doesn't return File objects
        return File.class;
    }
    @Override
    protected File parseValue(String arg, ParseContext context) throws InvalidPathException {
        File path = new File(arg);
        if(context.isConvertRelPaths() && context.getArgsFile() != null) {
            path = context.convertPath(path, context.getArgsFile());
        }
        if(!path.exists()) {
            if(mustExist || mustBeDirectory || mustBeFile) {
                throw new InvalidPathException(this, arg, "path must exist");
            }
            if(parentMustExist) {
                File parent = path.getParentFile();
                if(!parent.exists()) {
                    throw new InvalidPathException(this, arg, "parent path must exist");
                }
            }
        } else {
            if(mustBeDirectory && !path.isDirectory()) {
                throw new InvalidPathException(this, arg, "path must be a directory");
            }
            if(mustBeFile && !path.isFile()) {
                throw new InvalidPathException(this, arg, "path must be a file");
            }
        }
        return path;
    }
}
