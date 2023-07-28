package replete.text.stp;

import java.util.function.Predicate;

public interface StateCriteria extends Predicate<State> {
    public static final StateCriteria ANY = s -> true;
}
