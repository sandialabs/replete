package replete.plugins;

import javax.swing.Icon;

// UiGenerator is currently a misleading name for this class.
// If a generator is a "UiGenerator" it doesn't mean that the
// generator generates or coordinates components necessarily
// related to a user interface.  Rather, it means that the
// generator itself is presented to or selectable by a user
// in a user interface.  The 3 essential components needed for
// a user to understand the purpose of an arbitrary piece of
// extensible code is deemed to be:
//   1) Display name (a.k.a. nice name, readable name)
//   2) Description
//   3) Icon (for graphical user interfaces)
// With this information, as user can understand the purpose
// of certain extensible components that are loaded onto the
// platform.  The component itself might just be some low-
// level logic that has nothing further to do with a user
// interface.

// Should not implement ExtensionPoint itself, only subclasses
public abstract class UiGenerator extends Generator implements HumanDescribable {


    //////////////
    // ABSTRACT //
    //////////////

    // Used to display and describe extensible functionality to the user
    // within a user interface.
    public abstract String getName();
    public abstract String getDescription();
    public abstract Icon getIcon();
}
