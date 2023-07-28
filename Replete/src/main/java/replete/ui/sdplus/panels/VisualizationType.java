package replete.ui.sdplus.panels;

/**
 * The manner in which each scale should be visualized.
 * Each scale can be visualized in only one way right now.
 * However, most likely "Mark On X" and "Mark On Y" are
 * somewhat different conceptually than the others and might
 * need to be refactored out.  On the context menus for
 * example, these two types are shown as check box menu
 * items that can be independently turned on and off.
 *
 * @author Derek Trumbo
 */

public enum VisualizationType {
    NONE,
    MARK_ON_X,
    MARK_ON_Y,
    X_AXIS,
    Y_AXIS,
    COLOR,
    SHAPE
}
