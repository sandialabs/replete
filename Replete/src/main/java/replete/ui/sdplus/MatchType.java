package replete.ui.sdplus;

/**
 * Represents what mode the scale set panel is in.  When in intersection
 * mode, a data element must match all the scale panels' filter criteria
 * to be considered selected and when in union mode, a data element must
 * match at least one of scale panels' filter criteria to be considered
 * selected.
 *
 * @author Derek Trumbo
 */

public enum MatchType {
    INTERSECTION,
    UNION
}
