
package replete.ui.fc;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;

import replete.event.ChangeNotifier;
import replete.io.FileUtil;
import replete.text.StringUtil;
import replete.ui.GuiUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.windows.Dialogs;
import replete.util.OsUtil;
import replete.util.ReflectionUtil;
import replete.util.User;
import replete.xstream.XStreamWrapper;



/**
 * A file chooser with more features than the default
 * Swing file chooser.  Comprises a framework with
 * CommonFileFilter and RFilterBuilder.
 *
 * @author Derek Trumbo
 */

public class RFileChooser extends JFileChooser implements RecentListContext<File> {


    //////////////
    // CHOOSERS //
    //////////////

    // The Multi-Singleton pattern!
    protected static Map<String, RFileChooser> choosers = new HashMap<>();
    public static final String DEFAULT_PROFILE = "[default]";
    public static final String NO_SAVE_PROFILE = "[nosave]";



    ////////////////////////////
    // Other Global Constants //
    ////////////////////////////

    // Might be different on SunOS.
    protected static final String OPEN_FILTER_LABEL = "Files of Type:";
    protected static final String SAVE_FILTER_LABEL = "Save as Type:";


    ////////////
    // FIELDS //
    ////////////

    // Almost identical to the base class's dialogType field, but
    // required to know the type of a file chooser before the
    // base class's showDialog method is called (which can set the
    // base class's dialogType field to CUSTOM_DIALOG).
    protected int commonDialogType;

    // The filter that was selected when the user clicks the approve
    // button (also set when setFileFilter method called).
    protected FileFilter selectedFilter;

    // To prevent developer from always having to write
    // if(result == JFileChooser.APPROVE_OPTION) {....
    protected boolean approved;

    // Whether or not this CommonFileChooser instance has been
    // shown at least once before (in case client code wants
    // to be precise about the initial current directory).
    protected boolean shownBefore;

    // Whether or not to clear the filters each time the
    // chooser is requested.
    protected boolean persistFilters;

    // Suppress custom behavior of checking for overwrite and
    // existing files.
    protected boolean suppressChecks;

    // Override whether the dialog is to be a custom dialog
    // thus allowing a custom dialog to have open/save checks.
    protected boolean overrideCustom;

    // The profile that this dialog object is being used for.
    protected String profile;

    protected ImageIcon customIcon;

    // Recent files
    private List<File> recentList = new ArrayList<>();
    private JPanel pnlRecentContainer;          // For when used in an independent JDialog context.

    // Validator
    private RFileChooserValidator customValidator;

    private Dimension overriddenSize = null;


    ////////////////////////
    // getChooser Methods //
    ////////////////////////

    // Constants merely to be used in the getChooser methods that
    // follow to keep them organized.
    private static final String  DV_TITLE   = null;
    private static final String  DV_PROFILE = null;
    private static final boolean DV_MULTI   = false;
    private static final int     DV_TYPE    = FILES_ONLY;

    public static RFileChooser getChooser() {
        return getChooser(DV_TITLE, DV_PROFILE, DV_MULTI, DV_TYPE);
    }
    public static RFileChooser getChooser(boolean multi) {
        return getChooser(DV_TITLE, DV_PROFILE, multi, DV_TYPE);
    }
    public static RFileChooser getChooser(int selType) {
        return getChooser(DV_TITLE, DV_PROFILE, DV_MULTI, selType);
    }
    public static RFileChooser getChooser(boolean multi, int selType) {
        return getChooser(DV_TITLE, DV_PROFILE, multi, selType);
    }
    public static RFileChooser getChooser(String title) {
        return getChooser(title, DV_PROFILE, DV_MULTI, DV_TYPE);
    }
    public static RFileChooser getChooser(String title, boolean multi) {
        return getChooser(title, DV_PROFILE, multi, DV_TYPE);
    }
    public static RFileChooser getChooser(String title, int selType) {
        return getChooser(title, DV_PROFILE, DV_MULTI, selType);
    }
    public static RFileChooser getChooser(String title, boolean multi, int selType) {
        return getChooser(title, DV_PROFILE, multi, selType);
    }
    public static RFileChooser getChooser(String title, String profile) {
        return getChooser(title, profile, DV_MULTI, DV_TYPE);
    }
    public static RFileChooser getChooser(String title, String profile, boolean multi) {
        return getChooser(title, profile, multi, DV_TYPE);
    }
    public static RFileChooser getChooser(String title, String profile, int selType) {
        return getChooser(title, profile, DV_MULTI, selType);
    }

    // Return a CommonFileChooser with the given title, profile,
    // multi-select enabled, and selection type.
    public static RFileChooser getChooser(String title, String profile,
                                               boolean multi, int selType) {
        if(profile == null) {
            profile = DEFAULT_PROFILE;
        }

        RFileChooser chooser = getChooserInternal(profile);

        // Set the title. This is passed into the constructor
        // so as not to force the programmer to have to set it
        // always, since they are almost always going to want
        // to set the title.
        chooser.setDialogTitle(title);

        // Reset other common properties to default.
        chooser.setMultiSelectionEnabled(multi);
        chooser.setFileSelectionMode(selType);
        chooser.setSelectedFile(new File(""));
        chooser.setIcon((ImageIcon) null);
        // ^So previous dialog's selected files are not shown initially
        // in the file name text box.

        // Unless this particular file chooser has been set up
        // to keep filters from showing to showing, remove
        // all filters to start this chooser out with only the
        // "All Files" filter.
        if(!chooser.persistFilters) {
            chooser.resetChoosableFileFilters();
        }

        chooser.setSize(700, 500);

        // Do not change the recent list here.

        return chooser;
    }

    public void setValidator(RFileChooserValidator validator) {
        customValidator = validator;
    }

    private static RFileChooser getChooserInternal(String profile) {

        // Get the file chooser. The purpose behind storing
        // the file choosers is so that they retain the directory
        // and filter that was visible when the user closed the
        // dialog box.
        RFileChooser chooser = choosers.get(profile);
        if(chooser == null || profile.equals(NO_SAVE_PROFILE)) {
            chooser = new RFileChooser();
            chooser.profile = profile;
            choosers.put(profile, chooser);
        }

        return chooser;
    }

    public static boolean existsChooser(String profile) {
        return choosers.get(profile) != null;
    }

    // Multi-Singleton pattern.
    private RFileChooser() {}


    ////////////////////
    // getChooserAsIs //
    ////////////////////

    // Perform no initialization (this is for parts of the code other than that
    // which is actively using the chooser to get a file from the user).
    public static RFileChooser getChooserAsIs() {
        return getChooserAsIs(null);
    }
    public static RFileChooser getChooserAsIs(String profile) {
        if(profile == null) {
            profile = DEFAULT_PROFILE;
        }
        return getChooserInternal(profile);
    }


    /////////////////////////
    // Show Dialog Methods //
    /////////////////////////

    // To return boolean for convenience

    public boolean showOpen(Component parent) {
        showOpenDialog(parent);
        return isApproved();
    }

    public boolean showSave(Component parent) {
        showSaveDialog(parent);
        return isApproved();
    }

    public boolean show(Component parent, String customApproveButtonText) {
        showDialog(parent, customApproveButtonText);
        return isApproved();
    }

    public boolean show(Component parent, String customApproveButtonText, int dialogType) {
        showDialog(parent, customApproveButtonText, dialogType);
        return isApproved();
    }

    // To suppress checks

    public int showOpenDialogWithoutChecks(Component parent) {
        suppressChecks = true;
        return showOpenDialog(parent);
    }

    public int showSaveDialogWithoutChecks(Component parent) {
        suppressChecks = true;
        return showSaveDialog(parent);
    }

    // To set local dialog type.

    @Override
    public int showOpenDialog(Component parent) {
        commonDialogType = OPEN_DIALOG;
        return super.showOpenDialog(parent);
    }

    @Override
    public int showSaveDialog(Component parent) {
        commonDialogType = SAVE_DIALOG;
        return super.showSaveDialog(parent);
    }

    public int showDialog(Component parent, String customApproveButtonText, int type) {
        commonDialogType = type;
        overrideCustom = true;
        return showDialog(parent, customApproveButtonText);
    }

    // Main overridden method to do interesting work

    @Override
    public int showDialog(Component parent, String customApproveButtonText) {
        if(customApproveButtonText != null && !overrideCustom) {
            commonDialogType = CUSTOM_DIALOG;
        }
        if(commonDialogType == SAVE_DIALOG) {
            setFilterLabel(SAVE_FILTER_LABEL);
        } else {
            setFilterLabel(OPEN_FILTER_LABEL);
        }
        restoreFilter();
        int result = super.showDialog(parent, customApproveButtonText);
        if(result == JFileChooser.ERROR_OPTION) {
            throw new IllegalStateException("File chooser returned error code.");
        }
        closeCleanUp(result);
        return result;
    }

//    public int showDialog(Component parent, String approveButtonText)
//                    throws HeadlessException {
//                    if (dialog != null) {
//                        // Prevent to show second instance of dialog if the previous one still exists
//                        return JFileChooser.ERROR_OPTION;
//                    }
//
//                    if(approveButtonText != null) {
//                        setApproveButtonText(approveButtonText);
//                        setDialogType(CUSTOM_DIALOG);
//                    }
//                    dialog = createDialog(parent);
//                    dialog.addWindowListener(new WindowAdapter() {
//                        @Override
//                        public void windowClosing(WindowEvent e) {
//                            returnValue = CANCEL_OPTION;
//                        }
//                    });
//                    returnValue = ERROR_OPTION;
//                    rescanCurrentDirectory();
//
//                    dialog.show();
//                    firePropertyChange("JFileChooserDialogIsClosingProperty", dialog, null);
//
//                    // Remove all components from dialog. The MetalFileChooserUI.installUI() method (and other LAFs)
//                    // registers AWT listener for dialogs and produces memory leaks. It happens when
//                    // installUI invoked after the showDialog method.
//                    dialog.getContentPane().removeAll();
//                    dialog.dispose();
//                    dialog = null;
//                    return returnValue;
//                }


    @Override
    public void setDialogType(int dialogType) {
        commonDialogType = dialogType;
        super.setDialogType(dialogType);
    }

    protected void setFilterLabel(String newLabel) {
        setFilterLabel(this, newLabel);
    }

    protected boolean setFilterLabel(Container cnt, String newLabel) {
        Component[] cc = cnt.getComponents();
        for(Component c : cc) {
            if(c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if(lbl.getText().equalsIgnoreCase(OPEN_FILTER_LABEL)
                                || lbl.getText().equalsIgnoreCase(SAVE_FILTER_LABEL)) {
                    lbl.setText(newLabel);
                    return true;
                }
            }
            if(c instanceof Container) {
                if(setFilterLabel((Container) c, newLabel)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Temporary measure to allow those clients using the setFileFilter method
    // instead of the RFilterBuilder class to still allow their filter to be the
    // first shown to the user.  This had to be a different method than an
    // overridden setFileFilter because so many other methods in the API call
    // that method, and it would destroy what selectedFilter was pointing to.
    // A better design in the future may be warranted.
    public void setFileFilter(FileFilter filter, boolean setSelectedFilterAlso) {
        super.setFileFilter(filter);
        if(setSelectedFilterAlso) {
            selectedFilter = getFileFilter();
        }
    }

    protected void restoreFilter() {
        FileFilter[] fs = getChoosableFileFilters();
        if(selectedFilter != null) {
            for(FileFilter f : fs) {
                if(f.getDescription().equals(selectedFilter.getDescription())) {
                    super.setFileFilter(f);
                    return;
                }
            }
        }
        if(fs.length > 0) {
            super.setFileFilter(fs[0]);
        }
    }

    protected void closeCleanUp(int result) {
        selectedFilter = getFileFilter();
        approved = (result == APPROVE_OPTION);
        shownBefore = true;
        suppressChecks = false;
        overrideCustom = false;
        if(approved) {
            fireFileSelectedNotifier(profile);
        }
    }

    public void prependChoosableFileFilter(FileFilter filter) {
        insertChoosableFileFilter(filter, 0);
    }

    public void insertChoosableFileFilter(FileFilter filter, int index) {
        Vector<FileFilter> filters = (Vector<FileFilter>) ReflectionUtil.get(this, "filters");
        FileFilter fileFilter =  (FileFilter) ReflectionUtil.get(this, "fileFilter");
        if(filter != null && !filters.contains(filter)) {
            FileFilter[] oldValue = getChoosableFileFilters();
            filters.add(index, filter);
            firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY, oldValue, getChoosableFileFilters());
            if (fileFilter == null && filters.size() == 1) {
                setFileFilter(filter);
            }
        }
    }

    // Convenience method if just a single main filter is desired,
    // to prevent developer from having to create and use the
    // filter builder in their own code.
    public RFileChooser setMainFilter(boolean keepAllFilter, String desc, String... exts) {
        RFilterBuilder builder = new RFilterBuilder(this, keepAllFilter);
        builder.prepend(desc, exts);
        return this;
    }

    public RFileChooser setDefaultFileName(String name) {
        if(!StringUtil.isBlank(name)) {
            String cleaned = FileUtil.cleanForFileName(name);
            cleaned = StringUtil.max(cleaned, 40, false);
            setSelectedFile(new File(getCurrentDirectory(), cleaned));
        }
        return this;
    }

    public RFileChooser setCurrentDirectoryNew(File dir) {  // Not really sure how to name this method...
        super.setCurrentDirectory(dir);                     // Can't just change the return type.
        return this;
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected static Map<String, ChangeNotifier> notifiers = new HashMap<>();
    public static void addFileSelectedListener(ChangeListener listener) {
        addFileSelectedListener(null, listener);
    }
    public static void addFileSelectedListener(String profile, ChangeListener listener) {
        if(profile == null) {
            profile = DEFAULT_PROFILE;
        }
        if(!notifiers.containsKey(profile)) {
            notifiers.put(profile, new ChangeNotifier(profile));
        }
        notifiers.get(profile).addListener(listener);
    }
    protected void fireFileSelectedNotifier(String profile) {
        if(notifiers.containsKey(profile)) {
            notifiers.get(profile).fireStateChanged();
        }
    }


    ///////////////////////////
    // Approval Verification //
    ///////////////////////////

    @Override
    public void approveSelection() {
        if(!suppressChecks) {
            if(commonDialogType == OPEN_DIALOG) {
                if(!openVerifyExists()) {
                    return;
                }
            } else if(commonDialogType == SAVE_DIALOG) {
                if(!saveConfirmOverwrite()) {
                    return;
                }
            }
            if(customValidator != null) {
                File[] files = getAllSelectedFiles();
                ValidationProblem[] problems = customValidator.validate(
                    this, commonDialogType, files);
                if(problems.length != 0) {
                    String message;
                    if(files.length == 1) {
                        message = "There is a problem with the selected file:";
                    } else if(problems.length == 1) {
                        message = "One of the selected files had problems:";
                    } else {
                        message = "Multiple selected files had problems:";
                    }
                    for(ValidationProblem prob : problems) {
                        message += "\n\n" + prob.getFile() +
                            "\n    " + prob.getMessage();
                    }
                    Dialogs.showWarning(this, message, "Warning");
                    return;
                }
            }
        }

        File[] files = getAllSelectedFiles();
        for(File file : files) {
            addRecentLink(file);
        }

        super.approveSelection();
    }

    public File[] getAllSelectedFiles() {
        File[] files;
        if(isMultiSelectionEnabled()) {
            files = getSelectedFilesWithExtensions();
        } else {
            files = new File[] { getSelectedFileWithExtension() };
        }
        return files;
    }

    protected boolean openVerifyExists() {

        File[] files = getAllSelectedFiles();

        // Assume there will be no problems and the user will
        // continue with the save.
        boolean doOpen = true;

        for(File file : files) {
            if(!file.exists()) {
                String message;

                // Choose message and ask question.
                if(isMultiSelectionEnabled()) {
                    message = "One or more of the selected files do not exist.";
                } else {
                    message = "The selected file does not exist.";
                }

                JOptionPane.showMessageDialog(this, message, "Warning",
                                              JOptionPane.WARNING_MESSAGE);

                doOpen = false;

                // Just show one message.
                break;
            }
        }

        return doOpen;
    }

    protected boolean saveConfirmOverwrite() {

        File[] files = getAllSelectedFiles();

        // Assume there will be no problems and the user will
        // continue with the save.
        boolean doSave = true;

        for(File file : files) {
            if(file.isFile() && file.exists()) {
                String message;

                // Choose message and ask question.
                if(isMultiSelectionEnabled()) {
                    message = "One or more of the selected files exist.  Overwrite?";
                } else {
                    message = "The selected file already exists.  Overwrite?";
                }

                int result = JOptionPane.showConfirmDialog(this, message, "Warning",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                doSave = (result == JOptionPane.YES_OPTION);

                // Just ask one question.
                break;
            }
        }

        return doSave;
    }


    ///////////////////
    // Miscellaneous //
    ///////////////////

    public boolean wasShownBefore() {
        return shownBefore;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isPersistFilters() {
        return persistFilters;
    }

    public void setPersistFilters(boolean newPersist) {
        persistFilters = newPersist;
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        overriddenSize = d;
    }
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        overriddenSize = new Dimension(width, height);
    }
    public RFileChooser setChooserSize(int width, int height) {  // New name so can have
        super.setSize(width, height);                            // return value
        overriddenSize = new Dimension(width, height);
        return this;
    }

    public RFileChooser setIcon(ImageIcon newCustomIcon) {
        customIcon = newCustomIcon;
        return this;
    }
    public RFileChooser setIcon(ImageModelConcept concept) {
        customIcon = ImageLib.get(concept);
        return this;
    }

    // Make a best-effort to return the file that the user
    // selected with the appropriate extension on the end.
    // This only works with CommonFileFilter filters.
    public File getSelectedFileWithExtension() {
        return getSelectedFile();
    }
    @Override
    public File getSelectedFile() {
        File selectedFile = super.getSelectedFile();

        if(selectedFile == null) {
            return selectedFile;
        }

        FileFilter chosenFilter = getFileFilter();

        // No information to add an extension.
        if(chosenFilter == getAcceptAllFileFilter()) {
            return selectedFile;

        // Wouldn't know what extensions were allowed unless it's a
         // CommonFileFilter which keeps track of its allowed
         // extensions.
        } else if(!(chosenFilter instanceof RFileFilter)) {
            return selectedFile;
        }

        RFileFilter commonFilter = (RFileFilter) chosenFilter;

        // If the user did not include the extension on the file
         // name when selecting the file, add the extension now.
         if(!commonFilter.accept(selectedFile)) {
             selectedFile = new File(selectedFile.getAbsolutePath() +
                 commonFilter.getExtensionForAppend());
        }

        return selectedFile;
    }

    // Make a best-effort to return the files that the user
    // selected with the appropriate extensions on the end.
    // This only works with CommonFileFilter filters.
    public File[] getSelectedFilesWithExtensions() {
        return getSelectedFiles();
    }
    @Override
    public File[] getSelectedFiles() {
        File[] selectedFiles = super.getSelectedFiles();

        if(selectedFiles.length == 0) {
            return selectedFiles;
        }

        FileFilter chosenFilter = getFileFilter();

        // No information to add an extension.
        if(chosenFilter == getAcceptAllFileFilter()) {
            return selectedFiles;

        // Wouldn't know what extensions were allowed unless it's a
         // CommonFileFilter which keeps track of its allowed
         // extensions.
        } else if(!(chosenFilter instanceof RFileFilter)) {
            return selectedFiles;
        }

        RFileFilter commonFilter = (RFileFilter) chosenFilter;

        File[] newFiles = new File[selectedFiles.length];
        for(int f = 0; f < selectedFiles.length; f++) {
            File selectedFile = selectedFiles[f];

            // If the user did not include the extension on the file
             // name when selecting the file, add the extension now.
             if(!commonFilter.accept(selectedFile)) {
                 newFiles[f] = new File(selectedFile.getAbsolutePath() +
                      commonFilter.getExtensionForAppend());
            } else {
                newFiles[f] = selectedFile;
            }
        }

        return newFiles;
    }

    protected final String WINDOWS_MAPPED_DRIVE_PATTERN = "^[a-zA-Z]:\\\\.*$";

    // This method will resolve the Windows drive mapping letters
    // to UNC paths.  The reason that this method is not named
    // 'getSelectedFile' and doesn't override the parent class's
    // version is that this process can add a nontrivial
    // performance hit, and there are a handful of parts of the
    // Swing API that call that method on the file chooser
    // independent of our code.
    public File getSelectedFileResolved() {
        return resolveSingleFile(getSelectedFile());
    }

    // Put into own method in case someday we want to make a
    // getSelectedFileWithExtensionResolved method (or a
    // method that takes two booleans, one whether to
    // add extension, and one whether to resolve.
    protected File resolveSingleFile(File file) {

        if(file == null) {
            return null;
        }

        if(OsUtil.isWindows()) {
            String absPath = file.getAbsolutePath();

            if(absPath.matches(WINDOWS_MAPPED_DRIVE_PATTERN)) {
                return new File(FileUtil.getUncPathname(absPath));
            }
        }

        return file;
    }

    // This method will resolve the Windows drive mapping letters
    // to UNC paths.  The reason that this method is not named
    // 'getSelectedFiles' and doesn't override the parent class's
    // version is that this process can add a nontrivial
    // performance hit, and there are a handful of parts of the
    // Swing API that call that method on the file chooser
    // independent of our code.
    // All the selected files are assumed to all be in the same
    // directory.
    public File[] getSelectedFilesResolved() {
        File[] selFiles = getSelectedFiles();     // Cannot return null

        if(selFiles.length == 0) {
            return selFiles;
        }

        if(OsUtil.isWindows()) {
            String absPath = selFiles[0].getAbsolutePath();

            if(absPath.matches(WINDOWS_MAPPED_DRIVE_PATTERN)) {
                File[] newFiles = new File[selFiles.length];
                int f = 0;
                String groupUncPath = FileUtil.getUncPath(selFiles[0]);
                for(File selFile : selFiles) {
                    newFiles[f++] = new File(groupUncPath + selFile.getName());
                }
                return newFiles;
            }
        }

        return selFiles;
    }


    ///////////////
    // Mnemonics //
    ///////////////

    // These button captions will automatically have mnemonics
    // set on the first character.  Exact button text is
    // controlled by L&F, but this work in the vast majority
    // of cases.

    public static List<String> basicCaptions = new ArrayList<>();

    static {
        basicCaptions.add("Open");
        basicCaptions.add("Save");
        basicCaptions.add("Cancel");
        basicCaptions.add("New Folder");
    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        fixLabel();

        JDialog dialog = super.createDialog(parent);
        GuiUtil.addMnemonics(dialog, basicCaptions);
        whiteCombos(dialog);
        if(customIcon != null) {
            dialog.setIconImage(customIcon.getImage());
        }
        pnlRecentContainer = (JPanel) dialog.getContentPane();
        updateRecentPanel();
        if(overriddenSize != null) {
            dialog.setSize(overriddenSize);
        }
        Window window = GuiUtil.win(parent);
        dialog.setLocationRelativeTo(window);
        return dialog;
    }

    // Fixes lower case 'n' in "Folder name:" label
    public void fixLabel() {
        GuiUtil.traverse(this, c -> {
          if(c instanceof JLabel) {
              JLabel lbl = (JLabel) c;
              if(lbl.getText().equals("Folder name:")) {
                  lbl.setText("Folder Name:");
              }
          }
      });
    }

    public static void whiteCombos(JDialog dialog) {
        whiteCombos(dialog.getContentPane());
    }
    public static void whiteCombos(Container c) {
        Component[] cmps = c.getComponents();
        for(Component cmp : cmps) {
            if(cmp instanceof JComboBox) {
                cmp.setBackground(Color.white);

            // Recursively searching containers for more buttons.
            } else if(cmp instanceof Container) {
                whiteCombos((Container) cmp);
            }
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isLinkClickable(File file) {
        boolean allowed = false;
        for(FileFilter filter : getChoosableFileFilters()) {
            allowed = allowed || filter.accept(file);
        }
        return allowed;
    }
    @Override
    public void linkClicked(File object) {
        BasicFileChooserUI ui = (BasicFileChooserUI) getUI();
        setSelectedFile(object);
        ui.getApproveSelectionAction().actionPerformed(null);
    }
    @Override
    public String getLinkNamePlural() {
        return "Files";
    }
    @Override
    public List<File> getRecentList() {
        return recentList;
    }
    @Override
    public void setRecentList(List<File> list) {
        recentList = list;
        updateRecentPanel();
    }
    @Override
    public void addRecentLink(File file) {
        recentList.remove(file);
        recentList.add(0, file);
        updateRecentPanel();
    }
    private void updateRecentPanel() {
        if(pnlRecentContainer != null) {
            RecentListHelper.update(this, pnlRecentContainer);
        }
    }
    public void setRecentContainerPanel(JPanel pnlRecentContainer) {
        this.pnlRecentContainer = pnlRecentContainer;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws IOException {
        RFileChooser chooser2 = RFileChooser.getChooser("Load View from File")
            .setMainFilter(false, "aaa", "aaa")
        ;
        chooser2.setSize(700, 500);
//        RFilterBuilder builder = new RFilterBuilder(chooser2, true);
//        builder.prepend("AAA", "view");
//        builder.append("BBB", "view2");
//        builder.prepend("CCC", "view3");
        if(chooser2.showOpen(null)) {

        }
        if(true) {
            return;
        }

        File prefs = createPrefsFile();
        List<File> recentFiles = readPrefs(prefs);

        RFileChooser chooser = RFileChooser.getChooser(true);
        chooser.setSize(700, 500);
        chooser.setIcon(CommonConcepts.OPEN);
        chooser.setRecentList(recentFiles);

//        RFilterBuilder builder = new RFilterBuilder(chooser, false);
//        builder.append("Text Files", "txt");
//        chooser.showSave(null);
//        chooser.getRecentFiles().clear();
//        chooser.setValidator(new RFileChooserValidator() {
//            public ValidationProblem[] validate(RFileChooser chooser, int dialogType, File[] files) {
//                List<ValidationProblem> probs = new ArrayList<ValidationProblem>();
//                for(File file : files) {
//                    String prefix = "test2";
//                    if(!file.getName().startsWith(prefix)) {
//                        probs.add(new ValidationProblem(
//                            file, "The file name does not start with '" +
//                                prefix + "'."));
//                    }
//                }
//                return probs.toArray(new ValidationProblem[0]);
//            }
//        });

        chooser.showOpen(null);
        System.out.println(Arrays.toString(chooser.getAllSelectedFiles()));
        writePrefs(prefs, chooser);
    }
    private static File createPrefsFile() {
        File replete = User.getHome(".replete");
        replete.mkdirs();
        return new File(replete, "rfilechooser.xml");
    }
    private static List<File> readPrefs(File prefs) {
        List<File> recentFiles = null;
        try {
            Object[] recentLists = XStreamWrapper.loadTarget(prefs);
            recentFiles = (List<File>) recentLists[0];
        } catch(Exception e) {
        }
        if(recentFiles == null) {
            recentFiles = new ArrayList<>();
        }
        if(recentFiles.isEmpty()) {
            recentFiles.add(new File("C:\\Users\\dtrumbo\\Desktop"));
            recentFiles.add(new File("C:\\Users\\dtrumbo\\Desktop\\asdlfasasdfsafd\\asfdsadfasd\\asdfdsaf\\asdldfaslkfdjsalfjaslfksalfkajlksjfla;j"));
        }
        return recentFiles;
    }
    private static void writePrefs(File prefs, RFileChooser chooser) throws IOException {
        Object[] recentLists = new Object[] {
            chooser.getRecentList()
        };
        XStreamWrapper.writeToFile(recentLists, prefs);
    }
}
