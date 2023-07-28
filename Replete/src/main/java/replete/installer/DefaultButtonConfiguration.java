package replete.installer;

import javax.swing.Icon;
import javax.swing.SwingConstants;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;


/**
 * @author Derek Trumbo
 */

public class DefaultButtonConfiguration implements ButtonConfiguration {

    public boolean isCancelVisible() {
        return true;
    }
    public boolean isBackVisible() {
        return true;
    }
    public boolean isNextVisible() {
        return true;
    }

    public boolean isCancelEnabled() {
        return true;
    }
    public boolean isBackEnabled() {
        return true;
    }
    public boolean isNextEnabled() {
        return true;
    }

    public String getCancelText() {
        return "Cancel";
    }
    public String getBackText() {
        return "Back";
    }
    public String getNextText() {
        return "Next";
    }

    public int getCancelMnemonic() {
        return 'C';
    }
    public int getBackMnemonic() {
        return 'B';
    }
    public int getNextMnemonic() {
        return 'N';
    }

    protected static Icon cancelIcon = ImageLib.get(CommonConcepts.EXIT);
    protected static Icon backIcon = ImageLib.get(CommonConcepts.PREV);
    protected static Icon nextIcon = ImageLib.get(CommonConcepts.NEXT);
    public Icon getCancelIcon() {
        return cancelIcon;
    }
    public Icon getBackIcon() {
        return  backIcon;
    }
    public Icon getNextIcon() {
        return nextIcon;
    }

    public int getCancelTextPosition() {
        return SwingConstants.RIGHT;
    }
    public int getBackTextPosition() {
        return SwingConstants.RIGHT;
    }
    public int getNextTextPosition() {
        return SwingConstants.LEFT;
    }
}
