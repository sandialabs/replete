package finio.extractors.jo;

import finio.core.FUtil;
import finio.core.warnings.FieldAccessWarning;

public class ReflectionNonTerminalExpansionDecider implements NonTerminalExpansionDecider {
    public boolean shouldExpandNonTerminal(Object V) {
        return
            !FUtil.isNull(V) &&
            !FUtil.isPrimitive(V) &&
            !FUtil.isJavaArray(V) &&
            !(V instanceof FieldAccessWarning);  // Others?
    }

    @Override
    public String toString() {
        return "Reflection Non-Terminal Expansion Decider";
    }
}
