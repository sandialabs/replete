package replete.ui.form;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import replete.event.ChangeNotifier;
import replete.io.FileUtil;
import replete.text.StringUtil;
import replete.ui.fc.RFileChooser;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;
import replete.ui.validation.ValidationContext;


/**
 * @author Derek Trumbo
 */

public class FileSelectionPanel extends RPanel {


    ///////////
    // ENUMS //
    ///////////

    public enum DialogType {
        OPEN,
        SAVE
    }
    public enum PathType {
        FILE,
        DIR,
        EITHER
    }


    ////////////
    // FIELDS //
    ////////////

    protected RTextField txt;
    protected JButton btn;
    protected boolean suppressEvent;
    protected DialogType dialogType;
    protected boolean pathRequired;
    protected boolean pathsMustExist;
    protected boolean pathsMustBeAbsolute = true;     // The application's working directory usually should not be accidentally accessible in a GUI.
    protected PathType pathType = PathType.EITHER;
    protected Consumer<RFileChooser> filterModifier = null;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FileSelectionPanel() {
        this(null, null, null, null, JFileChooser.FILES_ONLY, false);
    }
    public FileSelectionPanel(Component parent) {
        this(parent, null, null, null, JFileChooser.FILES_ONLY, false);
    }
    public FileSelectionPanel(Component parent, String ttt) {
        this(parent, ttt, null, null, JFileChooser.FILES_ONLY, false);
    }
    public FileSelectionPanel(Component parent, String ttt, Icon icon,
                              DialogType type, int selType) {
        this(parent, ttt, icon, type, selType, false);
    }
    public FileSelectionPanel(final Component parent, String ttt, Icon icon, DialogType type,
                              final int selType, final boolean multi) {
        if(type == null) {
            dialogType = DialogType.OPEN;
        } else {
            dialogType = type;
        }

        txt = Lay.tx("", "selectall");

        if(icon == null) {
            if(dialogType == DialogType.OPEN) {
                icon = ImageLib.get(CommonConcepts.OPEN);
            } else {
                icon = ImageLib.get(CommonConcepts.SAVE);
            }
        }

        if(ttt == null) {
            if(dialogType == DialogType.OPEN) {
                ttt = "Open";
            } else {
                ttt = "Save";
            }
        }

        final String fttt = ttt;

        btn = Lay.btn(icon, 5, (ActionListener) e -> {
            RFileChooser chooser = RFileChooser.getChooser(
                StringUtil.cut(fttt, "..."), multi, selType);
            if(filterModifier != null) {
                filterModifier.accept(chooser);
            }
            File f = getPath();
            if(f != null) {
                if(f.exists()) {
                    if(f.isDirectory()) {
                        chooser.setCurrentDirectory(f);
                    } else {
                        chooser.setSelectedFile(f);
                    }
                } else if(f.getParentFile() != null && f.getParentFile().exists()) {
                    chooser.setCurrentDirectory(f.getParentFile());
                }
            }
            if(dialogType == DialogType.SAVE && chooser.showSave(parent) ||
               dialogType == DialogType.OPEN && chooser.showOpen(parent)) {
                suppressEvent = true;
                setPaths(chooser.getAllSelectedFiles());
                suppressEvent = false;
                fireChangeNotifier();
            }
        });
        btn.setToolTipText(ttt);   // Done outside of Lay since it's a client-provided string

        txt.addChangeListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                validatePaths();
                if(!suppressEvent) {
                    fireChangeNotifier();
                }
            }
        });

        Lay.BLtg(this,
            "C", Lay.p(txt, "eb=5r"),
            "E", btn
        );
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public File getPath() {
        File[] files = parsePaths();
        if(files.length == 0) {
            return null;
        }
        return files[0];
    }
    public File[] getPaths() {
        return parsePaths();
    }
    public boolean isPathsMustExist() {
        return pathsMustExist;
    }
    public boolean isPathRequired() {
        return pathRequired;
    }
    public boolean isPathsMustBeAbsolute() {
        return pathsMustBeAbsolute;
    }
    public PathType getPathType() {
        return pathType;
    }
    public DialogType getDialogType() {
        return dialogType;
    }

    // Mutators

    public FileSelectionPanel setPath(File f) {
        if(f == null) {
            txt.setText("");
        } else {
            txt.setText(f.getAbsolutePath());
        }
        validatePaths();
        return this;
    }
    public FileSelectionPanel setPaths(File[] files) {
        if(files == null || files.length == 0) {
            txt.setText("");
        } else {
            txt.setText(StringUtil.join(FileUtil.getAbsPaths(files), "; "));
        }
        validatePaths();
        return this;
    }
    public FileSelectionPanel setPathsMustExist(boolean pathsMustExist) {
        this.pathsMustExist = pathsMustExist;
        validatePaths();
        return this;
    }
    public FileSelectionPanel setPathRequired(boolean pathRequired) {
        this.pathRequired = pathRequired;
        validatePaths();
        return this;
    }
    public FileSelectionPanel setPathsMustBeAbsolute(boolean pathsMustBeAbsolute) {
        this.pathsMustBeAbsolute = pathsMustBeAbsolute;
        return this;
    }
    public FileSelectionPanel setPathType(PathType pathType) {
        this.pathType = pathType;
        validatePaths();
        return this;
    }
    public FileSelectionPanel setFilterModifier(Consumer<RFileChooser> filterModifier) {
        this.filterModifier = filterModifier;
        return this;
    }


    //////////
    // MISC //
    //////////

    @Override
    public void focus() {
        txt.requestFocusInWindow();
    }

    private void validatePaths() {
        String errorMessage = getValidationMessage();
        txt.setErrorMessage(errorMessage);
    }

    public String getValidationMessage() {
        String errorMessage = null;
        if(dialogType == DialogType.OPEN) {
            if(txt.isBlank()) {
                if(pathRequired) {
                    errorMessage = "A path must be specified.";
                }
            } else {
                File[] files = parsePaths();
                if(files.length == 1) {
                    if(!pathExists(files[0])) {
                        if(pathsMustExist) {
                            errorMessage = "Specified path does not exist.";
                        }
                    } else {
                        if(pathType != PathType.EITHER) {
                            if(files[0].isFile() && pathType == PathType.FILE ||
                                files[0].isDirectory() && pathType == PathType.DIR) {
                                // Good
                            } else {
                                if(pathType == PathType.FILE) {
                                    errorMessage = "Specified path must be a file.";
                                } else {
                                    errorMessage = "Specified path must be a directory.";
                                }
                            }
                        }
                    }
                } else {
                    boolean nonExist = false;
                    boolean wrongType = false;
                    for(File file : files) {
                        if(!pathExists(file)) {
                            if(pathsMustExist) {
                                nonExist = true;
                            }
                        } else {
                            if(pathType != PathType.EITHER) {
                                if(files[0].isFile() && pathType == PathType.FILE ||
                                    files[0].isDirectory() && pathType == PathType.DIR) {
                                    // Good
                                } else {
                                    wrongType = true;
                                }
                            }
                        }
                    }
                    if(nonExist) {
                        errorMessage = "One or more of the specified paths do not exist.";
                    }
                    if(wrongType) {
                        if(pathType == PathType.FILE) {
                            errorMessage = "All specified paths must refer to files.";
                        } else {
                            errorMessage = "All specified paths must refer to directories.";
                        }
                    }
                }
            }
        } else {
            // Save validation?
        }
        return errorMessage;
    }

    private boolean pathExists(File file) {
        if(!file.isAbsolute() && pathsMustBeAbsolute) {
            return false;
        }
        return file.exists();
    }

    public void setErrorMessage(String errorMessage) {
        txt.setErrorMessage(errorMessage);
    }

    private File[] parsePaths() {
        String text = txt.getText().trim();
        if(text.equals("")) {
            return new File[0];
        }
        String[] fileParts = text.split("\\s*;\\s*");
        List<File> files = new ArrayList<>();
        for(int p = 0; p < fileParts.length; p++) {
            if(!fileParts[p].equals("")) {
                files.add(new File(fileParts[p]));
            }
        }
        return files.toArray(new File[0]);
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected transient ChangeNotifier changeNotifier = new ChangeNotifier(this);
    public void addChangeListener(ChangeListener listener) {
        changeNotifier.addListener(listener);
    }
    protected void fireChangeNotifier() {
        changeNotifier.fireStateChanged();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txt.setEnabled(enabled);
        btn.setEnabled(enabled);
    }

    @Override
    public void validateInput(ValidationContext context) {
        String errorMessage = getValidationMessage();
        if(errorMessage != null) {
            context.error("The file or files you have specified are invalid.  " + errorMessage);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        JFrame f = Lay.fr("Test");
        FileSelectionPanel pnl;
        Lay.BLtg(f,
            "N", pnl = new FileSelectionPanel(f, "TOOOL",
                null, DialogType.SAVE, JFileChooser.FILES_ONLY, true
            ),
            "C", Lay.btn("Set", (ActionListener) e -> {
                pnl.setPaths(new File[] {
                    new File("C:\\Users"),
                    new File("C:\\Users\\dtrumbo\\Desktop")
                });
            }),
            "size=500,center,visible"
        );
        pnl.addChangeListener(e -> System.out.println(Arrays.toString(pnl.getPaths())));
    }
}
