package replete.ui.fc;

import javax.swing.filechooser.FileFilter;

/**
 * An easy way to add filters to a CommonFileChooser.
 *
 * @author Derek Trumbo
 */

public class RFilterBuilder {
    public static final boolean KEEP_ALL_FILTER = true;
    public static final boolean DONT_KEEP_ALL_FILTER = false;

    private RFileChooser chooser;
    private FileFilter acceptAllFilter;

    // Construct a RFilterBuilder with the given chooser and
    // whether to remove or keep the "all" filter.
    public RFilterBuilder(RFileChooser c, boolean keepAllFilter) {
        chooser = c;

        // Use of the RFilterBuilder implies it will have all
        // control over filters - remove all previous filters.
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.resetChoosableFileFilters();

        acceptAllFilter = chooser.getAcceptAllFileFilter();
        if(!keepAllFilter) {
            chooser.removeChoosableFileFilter(acceptAllFilter);
        }
    }


    ////////////
    // APPEND //
    ////////////

    public RFilterBuilder appendAcceptAllFilter() {
        append(acceptAllFilter);
        return this;
    }

    // Add a file filter to the chooser with the given description and extensions.
    public RFilterBuilder append(String desc, String... exts) {
        return append(desc, false, true, exts);
    }

    // Add a file filter to the chooser with the given description,
    // extensions, and case-sensitivity.
    public RFilterBuilder append(String desc, boolean cs, String... exts) {
        return append(desc, cs, true, exts);
    }

    // Add a file filter to the chooser with the given description,
    // extensions, case-sensitivity, and whether to allow directories.
    public RFilterBuilder append(String desc, boolean cs, boolean ad, String... exts) {
        FileFilter filter = new RFileFilter(desc, exts, cs, ad);
        append(filter);
        return this;
    }
    public RFilterBuilder append(FileFilter filter) {   // Add an already-constructed FileFilter.
        chooser.addChoosableFileFilter(filter);
        return this;
    }
    public RFilterBuilder append(RFileFilter filter) {  // Add an already-constructed CommonFileFilter.
        chooser.addChoosableFileFilter(filter);
        return this;
    }


    /////////////
    // PREPEND //
    /////////////

    public RFilterBuilder prepend(String desc, String... exts) {
        return prepend(desc, false, true, exts);
    }
    public RFilterBuilder prepend(String desc, boolean cs, String... exts) {
        return prepend(desc, cs, true, exts);
    }
    public RFilterBuilder prepend(String desc, boolean cs, boolean ad, String... exts) {
        FileFilter filter = new RFileFilter(desc, exts, cs, ad);
        prepend(filter);
        return this;
    }
    public RFilterBuilder prepend(FileFilter filter) {   // Add an already-constructed FileFilter.
        chooser.prependChoosableFileFilter(filter);
        return this;
    }
    public RFilterBuilder prepend(RFileFilter filter) {  // Add an already-constructed CommonFileFilter.
        chooser.prependChoosableFileFilter(filter);
        return this;
    }
}
