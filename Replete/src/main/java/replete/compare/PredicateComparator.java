package replete.compare;

import java.util.Comparator;
import java.util.function.Predicate;

public class PredicateComparator<T> implements Comparator<T> {


    ////////////
    // FIELDS //
    ////////////

    private Predicate<T> predicate;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PredicateComparator(Predicate<T> predicate) {
        this.predicate = predicate;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int compare(T o1, T o2) {
        boolean m1 = predicate.test(o1);
        boolean m2 = predicate.test(o2);
        if(m1 == m2) {
            return 0;
        }
        if(m1) {
            return -1;
        }
        return 1;
    }
}
