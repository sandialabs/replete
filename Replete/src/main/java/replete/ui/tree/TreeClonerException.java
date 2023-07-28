package replete.ui.tree;

/**
 * @author Derek Trumbo
 */

public class TreeClonerException extends Exception {
    public TreeClonerException(Throwable cause) {
        super("Could not call clone method.", cause);
    }
}