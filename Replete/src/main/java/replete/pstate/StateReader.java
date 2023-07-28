
package replete.pstate;

/**
 * Every state reader must have a reference to the
 * application's state object so it can populate the
 * fields in it, in addition to any other information
 * required to read the state from the source.
 *
 * @author Derek Trumbo
 */

public interface StateReader {
    public boolean read();
}
