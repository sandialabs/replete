
package replete.pstate;

/**
 * Every state writer must have a reference to the
 * application's state object so it can read the
 * fields from it, in addition to any other information
 * required to write the state to the destination.
 *
 * @author Derek Trumbo
 */

public interface StateWriter {
    public boolean write();
}
