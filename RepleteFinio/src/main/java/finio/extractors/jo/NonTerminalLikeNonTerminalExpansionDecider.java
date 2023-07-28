package finio.extractors.jo;

import finio.core.FUtil;

public class NonTerminalLikeNonTerminalExpansionDecider implements NonTerminalExpansionDecider {
    public boolean shouldExpandNonTerminal(Object V) {
        return FUtil.isNonTerminalLike(V);
    }

    @Override
    public String toString() {
        return "Non-Terminal Like Non-Terminal Expansion Decider";
    }
}
