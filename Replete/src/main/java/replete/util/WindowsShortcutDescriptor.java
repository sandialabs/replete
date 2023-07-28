package replete.util;

import java.io.File;

/**
 * Describes how a Windows shortcut should be created.
 * When a WindowsShortcutDescriptor object is constructed
 * it is immediately valid and can be used to create
 * a shortcut.  The shortcut created has default values.
 * which should be modified for the application's needs.
 *
 * @author Derek Trumbo
 */

public class WindowsShortcutDescriptor {

    public enum ShortcutState {
        NORMAL(1),
        MAXIMIZED(3),
        MINIMIZED(7);

        protected int value;
        private ShortcutState(int v) {
            value = v;
        }
        public int getValue() {
            return value;
        }
    }

    //////////////
    // REQUIRED //
    //////////////

    // The name of the shortcut.  This will need to obey
    // the rules of Windows file naming conventions.
    protected String text = "Shortcut";

    // Default directory is the Desktop.
    protected File destDir = new File(System.getProperty("user.home"), "Desktop");

    // The command to execute.  If the command includes
    // a path that has spaces, use double-quotes to wrap
    // the path.
    // Example: '"C:\Program Files\Program.exe"'
    protected String target = "cmd";

    protected ShortcutState state = ShortcutState.NORMAL;

    //////////////
    // OPTIONAL //
    //////////////

    protected File workingDir = null;
    protected File iconFile = null;

    // List of arguments separated by spaces.  Use
    // double-quotes to combine multiple tokens
    // that belong to the same argument.
    // Example: 'bob sally "billy bob"'
    protected String arguments = null;

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public String getText() {
        return text;
    }

    public void setText(String txt) {
        text = txt;
    }

    public File getDestDir() {
        return destDir;
    }

    public void setDestDir(File dir) {
        destDir = dir;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String tg) {
        target = tg;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(File dir) {
        workingDir = dir;
    }

    public File getIconFile() {
        return iconFile;
    }

    public void setIconFile(File file) {
        iconFile = file;
    }

    public ShortcutState getState() {
        return state;
    }

    public void setState(ShortcutState st) {
        state = st;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String args) {
        arguments = args;
    }

    //////////////
    // toString //
    //////////////

    // Diagnostic toString.
    @Override
    public String toString() {
        String dd = (destDir == null) ? "null" : destDir.getAbsolutePath();
        String wd = (workingDir == null) ? "null" : workingDir.getAbsolutePath();
        String id = (iconFile == null) ? "null" : iconFile.getAbsolutePath();

        return "Shortcut[text=" + text + ",destDir=" + dd + ",target=" + target +
            ",workingDir=" + wd + ",icon=" + id + ",state=" + state.name() +
            ",args=" + arguments + "]";
    }
}
