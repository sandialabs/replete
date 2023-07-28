package replete.installer;

import javax.swing.Icon;

/**
 * @author Derek Trumbo
 */

public interface ButtonConfiguration {
    public boolean isCancelVisible();
    public boolean isBackVisible();
    public boolean isNextVisible();
    public boolean isCancelEnabled();
    public boolean isBackEnabled();
    public boolean isNextEnabled();
    public String getCancelText();
    public String getBackText();
    public String getNextText();
    public int getCancelMnemonic();
    public int getBackMnemonic();
    public int getNextMnemonic();
    public Icon getCancelIcon();
    public Icon getBackIcon();
    public Icon getNextIcon();
    public int getCancelTextPosition();
    public int getBackTextPosition();
    public int getNextTextPosition();
}
