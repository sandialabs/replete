package replete.ui;

import replete.ui.panels.SelectionState;

// NOTE: getSelectionState could have easily been called
// createSelectionState just due to how complex a call
// it might actually be as it could crawl an arbitrarily
// large component hierarchy to gather and provide its
// results.  The prefix "get" has that connotation of a
// method that merely returns a direct reference or a
// copy of a primitive or a method that only has to perform
// minor computation on its fields to provdide a massaged
// value.  The "create" prefix has more of the connotation
// that a good deal more work might be happening when
// the method is called.  This is why one of the core
// PersistentController methods is called createSummaryState.

public interface SelectionStateSavable {
    default SelectionState getSelectionState() {
        return getSelectionState(new Object[0]);
    }

    // Components implement this one if they need to define
    // parameters that influence *how* their state is
    // generated.
    default SelectionState getSelectionState(Object... args) {
        return null;
    }

    default void setSelectionState(SelectionState state) {}

    // Convenience method for checking the variable, untyped arguments
    // for a specific first object.
    default boolean needsDefaultArg(Object[] args, Class<?> clazz, int index) {
        return
            args == null         ||
            args.length <= index ||
            args[index] == null  ||
            !(clazz.isAssignableFrom(args[index].getClass()))
        ;
    }

    default <T> T getDefaultArg(Object[] args, T target) {
        return getDefaultArg(args, target, 0);
    }
    default <T> T getDefaultArg(Object[] args, T target, int index) {
        if(needsDefaultArg(args, target.getClass(), index)) {
            return target;
        }
        return (T) args[index];
    }

    default <T> T getDefaultArg(Object[] args, Class<? extends T> clazz) {
        return getDefaultArg(args, clazz, 0);
    }
    default <T> T getDefaultArg(Object[] args, Class<? extends T> clazz, int index) {
        if(needsDefaultArg(args, clazz, index)) {
            return null;
        }
        return (T) args[index];
    }
}
