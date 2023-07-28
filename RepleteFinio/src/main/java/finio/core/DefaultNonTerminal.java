package finio.core;

public interface DefaultNonTerminal extends NonTerminal {

    // Marks the default in-memory, always mutable non-terminals
    // that are a part of Finio core:
    //  - FMap (95% reviewed)
    //  - FList (50% reviewed)
    //  - FListMap (20% reviewed)

}
