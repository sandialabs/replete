package replete.ui.tree;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import replete.extensions.ui.OverridableDefaultTreeCellRenderer;

public class NodeBaseTreeRenderer extends OverridableDefaultTreeCellRenderer {

    private Font origFont;

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row,
            boolean hasFocus1) {

        // Essentially returns 'this', but it's required to call.
        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus1);

        if(origFont == null) {
            origFont = getFont();
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        if(node.getUserObject() instanceof NodeBase) {
            NodeBase uBase = (NodeBase) node.getUserObject();

            // Ask the NodeBase for its icon.
            Icon icon = uBase.getIcon(expanded);
            if(icon != null) {
                setIcon(icon);
            }

            // Ask the NodeBase for its color.
            setForeground(uBase.getForegroundColor());

            // Ask the NodeBase for its text.
            setText(uBase.toString());

            // Ask the NodeBase for its font style.
            int style = Font.PLAIN;
            if(uBase.isItalic()) {
                style += Font.ITALIC;
            } else if(uBase.isBold()) {
                style += Font.BOLD;
            }
            if(style == Font.PLAIN) {
                setFont(origFont);
            } else {
                setFont(origFont.deriveFont(style));
            }
        }

        return this;
    }
}
