package finio.core.managed;

import java.util.Random;

// This class is just to spark some ideas.  It intentionally has
// two nearly identical methods of specifying a "reference".
// This class could be an AMap, but that might be API overkill.
// Immutable at this point.
public class RandomManagedValueManager extends AbstractManagedValueManager {


    ////////////
    // FIELDS //
    ////////////

    public Random R = new Random();
    public int value;

    public RandomManagedValueManager() {
        value = R.nextInt();
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    @Override
    public Object get() {
        return value;
    }
    @Override
    public boolean canSet() {
        return false;
    }

    @Override
    public String getName() {
        return "Random";
    }
}
