package replete.ui.fc;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter that stores internally which file extensions
 * it allows so that those extensions can be accessed at
 * any time.
 *
 * @author Derek Trumbo
 */

public class RFileFilter extends FileFilter implements java.io.FileFilter {
    private String description;
    private String[] extensions;       // Default = null
    private boolean caseSensitive;     // Default = false
    private boolean allowDirs;         // Default = true

    // Construct a filter with given description (will
    // be an accept all file filter because no extensions
    // provided).
    public RFileFilter(String desc) {
        this(desc, null, false, true);
    }

    // Construct a filter with given description and
    // whether to allow directories (will be an accept
    // all file filter because no extensions provided).
    public RFileFilter(String desc, boolean ad) {
        this(desc, null, false, ad);
    }

    // Construct a filter with given description and extensions.
    public RFileFilter(String desc, String[] exts) {
        this(desc, exts, false, true);
    }

    // Construct a filter with given description, extensions, and
    // case-sensitivity.
    public RFileFilter(String desc, String[] exts, boolean cs) {
        this(desc, exts, cs, true);
    }

    // Construct a filter with given description, extensions,
    // case-sensitivity, and whether to allow directories.
    public RFileFilter(String desc, String[] exts, boolean cs, boolean ad) {
        description = desc;
        extensions = exts;
        caseSensitive = cs;
        allowDirs = ad;
    }

    @Override
    public boolean accept(File f) {

        if(f == null) {
            return false;
        }

        // Whether or not to allow directories.
        if(f.isDirectory()) {
            return allowDirs;
        }

        // Implement the "accept all" file filter if there
        // are no extensions.  This allows a CommonFileFilter
        // be an accept all file filter but have a custom
        // description.
        if(extensions == null || extensions.length == 0) {
            return true;
        }

        String name = f.getName();
        int dot = name.lastIndexOf('.');

        // Filter only handles files with extensions
        if( dot == -1 ) {
            return false;
        }

        String ext = name.substring(dot + 1);

        // See if the extension is in the extension list.
        for(String e : extensions) {
            if(ext.equalsIgnoreCase(e) && !caseSensitive || ext.equals(e)) {
                return true;
            }
        }

        // Did not match, reject.
        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String[] getExtensions() {
        return extensions;
    }

    // All the extensions for a given filter should be interchangeable
    // (jpg, jpeg, etc.), so just use the first.
    // (this is an assumption of course but there are
    // plenty of precedents for this - each filter in a
    // save dialog should have interchangeable extensions).
    public String getExtensionForAppend() {
        if(extensions == null || extensions.length == 0) {
            return "";
        }

        return "." + extensions[0].toLowerCase();
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean isAllowDirs() {
        return allowDirs;
    }
}
