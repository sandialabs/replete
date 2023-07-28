package replete.scripting.rscript.ui;

import replete.ui.tree.NodeBase;

public abstract class NodeASTNode extends NodeBase {

    @Override
    public boolean isCollapsible() {
        return false;
    }

    @Override
    public boolean isBold() {
        return true;
    }

}
