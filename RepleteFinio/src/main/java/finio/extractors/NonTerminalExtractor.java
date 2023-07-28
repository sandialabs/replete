package finio.extractors;

import finio.core.FUtil;
import finio.core.NonTerminal;
import finio.core.NonTerminalCreator;
import finio.core.impl.FMap;

public abstract class NonTerminalExtractor implements NonTerminalCreator {
    protected NonTerminal createBlankNonTerminal() {
        return new FMap();
    }
    @Override
    public NonTerminal extract() {
        NonTerminal M = extractInner();
        if(M != null) {
            int Zall = M.sizeAll();
            FUtil.concat(((FMap) M).getSysMeta(), "extractor", getName(), ", ");
            M.putSysMeta("extraction-tree-size", Zall);
        }
        return M;
    }

    protected abstract NonTerminal extractInner();
    protected abstract String getName();
}
