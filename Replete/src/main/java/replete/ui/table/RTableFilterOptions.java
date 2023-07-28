package replete.ui.table;

import java.io.Serializable;

public class RTableFilterOptions implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    // Defaults

    public static final boolean   DEFAULT_MATCH_CASE = false;
    public static final MatchMode DEFAULT_MATCH_MODE = MatchMode.NORMAL;

    // Core

    private boolean   matchCase = DEFAULT_MATCH_CASE;
    private MatchMode matchMode = DEFAULT_MATCH_MODE;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isMatchCase() {
        return matchCase;
    }
    public MatchMode getMatchMode() {
        return matchMode;
    }

    // Mutators

    public RTableFilterOptions setMatchCase(boolean matchCase) {
        this.matchCase = matchCase;
        return this;
    }
    public RTableFilterOptions setMatchMode(MatchMode matchMode) {
        this.matchMode = matchMode;
        return this;
    }
}
