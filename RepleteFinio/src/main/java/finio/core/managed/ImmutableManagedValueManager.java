package finio.core.managed;


// This class is just to spark some ideas.  It intentionally has
// two nearly identical methods of specifying a "reference".
// This class could be an AMap, but that might be API overkill.
// Immutable at this point.
public class ImmutableManagedValueManager extends AbstractManagedValueManager {


    ////////////
    // FIELDS //
    ////////////

    private Object V;

    public ImmutableManagedValueManager(Object V) {
        this.V = V;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    @Override
    public Object get() {
        return V;
    }
    @Override
    public boolean canSet() {
        return false;
    }

    @Override
    public String getName() {
        return "Immutable";
    }
}
