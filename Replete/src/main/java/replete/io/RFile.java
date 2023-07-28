package replete.io;

import java.io.File;
import java.net.URI;

/**
 * "Extended File" - A subclass of File that is merely a
 * wrapper around FileUtil for ease of coding.
 *
 * @author Derek Trumbo
 */

public class RFile extends File {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RFile(File copy) {
        super(copy.getPath());
    }
    public RFile(File parent, String child) {
        super(parent, child);
    }
    public RFile(String pathname) {
        super(pathname);
    }
    public RFile(String parent, String child) {
        super(parent, child);
    }
    public RFile(URI uri) {
        super(uri);
    }

    public RFile append(String str) {
        return new RFile(getParentFile(), getName() + str);
    }
    public RFile prepend(String str) {
        return new RFile(getParentFile(), str + getName());
    }
    public RFile insert(String str) {
        return new RFile(getParentFile(),
            getNameWithoutExtension() + str + "." + getExtension());
    }


    /////////////
    // METHODS //
    /////////////

    // Normal File.equals method returns false for the
    // objects new File("x") and new File("./x").  This
    // method compares the absolute paths and returns
    // whether the file objects refer to the same physical
    // file location.
    @Override
    public boolean equals(Object object) {
        if(object == null || !(object instanceof File)) {
            return false;
        }
        return FileUtil.equals(this, (File) object);
    }

    // Deletes a directory without it having to be empty first.
    @Override
    public boolean delete() {

        // Condition required because specification
        // of FileUtil.deleteDirectory says it will
        // return false for File objects that are
        // not directories.
        if(isDirectory()) {
            return FileUtil.deleteDirectory(this);
        }

        return super.delete();
    }

    public void copy(File dstPath) {
        FileUtil.copy(this, dstPath);
    }

    // Change to File object someday.
    public String getUncPath() {
        return FileUtil.getUncPath(this);
    }

    public String getTextContent() {
        return FileUtil.getTextContent(this);
    }

    public String getTextContent(boolean keepNewlines) {
        return FileUtil.getTextContent(this, keepNewlines);
    }

    public boolean writeTextContent(String content) {
        return FileUtil.writeTextContent(this, content);
    }

    public boolean writeTextContent(String content, boolean append) {
        return FileUtil.writeTextContent(this, content, append);
    }

    public boolean writeTextContent(String content, boolean append, boolean suppressNewline) {
        return FileUtil.writeTextContent(this, content, append, suppressNewline);
    }

    public String getExtension() {
        return FileUtil.getExtension(this);
    }

    public String getNameWithoutExtension() {
        return FileUtil.getNameWithoutExtension(this);
    }

    public File switchExtension(String newExt) {
        return FileUtil.switchExtension(this, newExt);
    }

    public File correctCase() {
        return FileUtil.correctCase(this);
    }
}
