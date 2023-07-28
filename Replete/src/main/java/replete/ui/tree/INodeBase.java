package replete.ui.tree;

import java.awt.Color;

import javax.swing.Icon;

public interface INodeBase {
    public Icon getIcon(boolean expanded);
    public Color getForegroundColor();
    public boolean isBold();
    public boolean isItalic();
    public boolean isCollapsible();
}
