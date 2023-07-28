package replete.logging;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;


/**
 * @author Derek Trumbo
 */

public enum LogEntryType {
    INFO("Information",        ImageLib.get(CommonConcepts.INFO),    JOptionPane.INFORMATION_MESSAGE),
    WARNING("Warning",         ImageLib.get(CommonConcepts.WARNING), JOptionPane.WARNING_MESSAGE),
    ERROR("Error",             ImageLib.get(CommonConcepts.ERROR),   JOptionPane.ERROR_MESSAGE),
    FATAL_ERROR("Fatal Error", ImageLib.get(CommonConcepts.ERROR),   JOptionPane.ERROR_MESSAGE);

    protected String name;
    protected ImageIcon icon;
    protected int dlgType;

    LogEntryType(String name, ImageIcon icon, int dlgType) {
        this.name = name;
        this.icon = icon;
        this.dlgType = dlgType;
    }

    public String getName() {
        return name;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public int getDialogType() {
        return dlgType;
    }

    public static LogEntryType fromString(String s) {
        try {
            return LogEntryType.valueOf(s);
        } catch(IllegalArgumentException e) {
            if(s.equals("Info") || s.equals("Information") || s.equals("Informational") || s.equals("Informational Message")) {
                return INFO;
            } else if(s.equals("Warning") || s.equals("Warning Message")) {
                return WARNING;
            } else if(s.equals("Error") || s.equals("Error Message")) {
                return ERROR;
            } else if(s.equals("Fatal Error") || s.equals("Fatal Error Message")) {
                return FATAL_ERROR;
            }

            throw e;
        }
    }
}
