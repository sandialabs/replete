package finio.core;

public interface DefaultTerminal {

    // This class exists to document what objects in the system
    // might be considered "default" terminals which would be
    // analogous to the "default" non-terminals, FMap, FList, and
    // FListMap.  "Default" terminals and non-terminals represent
    // the most basic information provided by default in the
    // system without any management or complexity.

    // The "default" terminal values are:
    //  - Boolean
    //  - Byte
    //  - Short
    //  - Integer
    //  - Long
    //  - Float
    //  - Double
    //  - Character
    //  - String

    // Potentially arrays of any of the above types.

    // It is not known yet whether objects that are not one of the
    // above nor NonTerminal's, are conceptually classified as
    // "default" terminals (e.g. StringBuffer, JPanel, Car).

}
