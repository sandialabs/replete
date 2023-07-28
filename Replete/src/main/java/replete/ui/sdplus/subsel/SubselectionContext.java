package replete.ui.sdplus.subsel;

/**
 * Defines a subset of the scales in the scale set panel
 * model by deciding if each key provided exists in
 * the subset (i.e. context).
 *
 * @author Derek Trumbo
 */

public interface SubselectionContext {
    public boolean existsInContext(String key);
}
