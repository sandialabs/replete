package replete.ui.panels;

import java.util.Map;

import replete.collections.DefaultCreator;
import replete.collections.RLinkedHashMap;
import replete.ui.SelectionStateSavable;

// This method is just a map of String to Object.  The values are
// meant to be (for example):
//
//   1. Selected list, combo, or table indicies (integers)
//   2. Class names/toString's of selected objects (strings)
//   3. Direct references to selected objects (any)
//   4. Quick filtering check box state (booleans)
//   5. Another SelectionState object (to manifest the hierarchy)
//
// The object is generally used by collecting a component's selection
// state, rebuilding/repopulating/refreshing that component and then
// setting the state back onto that component.  Example usage:
//
//     SelectionState state = pnlPerson.getSelectionState();
//     pnlPerson.set(newPerson);
//     pnlPerson.setSelectionState(state);
//
// Once collected from an entire component hierarchy, the object
// could theoretically even be serialized out for later use in
// order to create a "pick up exactly where I left off" feature.

public class SelectionState extends RLinkedHashMap<String, Object> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SelectionState() {
        super();
    }
    public SelectionState(DefaultCreator<Object> creator) {
        super(creator);
    }
    public SelectionState(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }
    public SelectionState(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    public SelectionState(int initialCapacity) {
        super(initialCapacity);
    }
    public SelectionState(Map<? extends String, ? extends Object> m) {
        super(m);
    }
    public SelectionState(Object... init) {
        super(init);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    public <V> V getGx(String key) {    // get but with a generic return type
        return (V) super.get(key);
    }
    public Integer getInt(String key) {
        return (Integer) super.get(key);
    }
    public Integer getInt(String key, int max) {
        int value = getInt(key);
        return value > max ? max : value;
    }
    public Long getLong(String key) {
        return (Long) super.get(key);
    }
    public String getString(String key) {
        return (String) super.get(key);
    }
    public SelectionState getSs(String key) {
        return (SelectionState) super.get(key);
    }

    // Mutators

    // The method name "p" is NOT great, but it is inherited
    // from RLinkedHashMap which hoped to provide a "put"
    // method that you can use with the modified builder pattern
    // (since the classic "put" method returns previous value).
    // We *could* add a new method like "save" or "set" just
    // for SelectionState... but then we're just righting one
    // wrong with another wrong.  Hard to say what the "best"
    // choice here is (perhaps instead of "p" RLinkedHashMap
    // should use something like "put2"?).
    @Override
    public SelectionState p(Object... init) {
        return (SelectionState) super.p(init);
    }
    @Override
    public SelectionState p(String key, Object value) {
        return (SelectionState) super.p(key, value);
    }


    //////////
    // MISC //
    //////////

    // Convenience 'put' for objects that might be SSS.  This
    // is done separately from the 'put' method, which could
    // have been retooled to check every incoming object's type
    // for SelectionStateSavable but that then prevents those
    // components from existing in the map themselves in case
    // other components are listing specific components in their
    // maps to indicate selection state.  This bypasses that
    // design question and makes the developer specifically
    // indicate they want to have getSelectionState called on
    // the object if it of the proper type.  The 'if' that it
    // wraps is handy to clean up client code.
    //
    // Basically this call just saves the developer from having
    // to call "getSelectionState" directly - though the
    // convenience might also lead to less obvious/readable code
    // per usual.
    public SelectionState putSsIf(String key, Object possibleSss) {
        if(possibleSss instanceof SelectionStateSavable) {
            put(key, ((SelectionStateSavable) possibleSss).getSelectionState());
        }
        return this;
    }

    // This looks like another "put" method called in the
    // getSelectionState method.  But it is a convenience
    // method to be used in the *set*SelectionState method
    // to read a value from the state object and place a
    // specific value in the map onto the SSS object.
    public void setSsIf(Object possibleSss, String key) {
        if(possibleSss instanceof SelectionStateSavable) {
            SelectionState state2 = getGx(key);
            if(state2 != null) {
                ((SelectionStateSavable) possibleSss).setSelectionState(state2);
            }
        }
    }

    public SelectionState print() {
        System.out.println(this);
        return this;
    }
}
