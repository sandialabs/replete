package replete.compare;

import java.util.Comparator;

import replete.equality.EqualsUtil;
import replete.util.ReflectionUtil;

public class MethodIndirectionComparator<T, U> implements Comparator<T> {


    ////////////
    // FIELDS //
    ////////////

    private String methodName;
    private Comparator<U> comp;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MethodIndirectionComparator(String methodName, Comparator<U> comp) {
        this.methodName = methodName;
        this.comp = comp;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int compare(T o1, T o2) {
        if(EqualsUtil.equals(o1, o2)) {    // TODO: Reconsider this logic... not sure it's 100% appropriate for the desired need
            return 0;
        }

        Object f1 = o1 == null ? null : ReflectionUtil.invoke(o1, methodName);
        Object f2 = o2 == null ? null : ReflectionUtil.invoke(o2, methodName);

        return comp.compare((U) f1, (U) f2);
    }
}
