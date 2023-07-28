package replete.compare;

import java.util.Comparator;
import java.util.List;

import replete.collections.ArrayUtil;
import replete.equality.EqualsUtil;

public class GroupSequenceComparator<T> implements Comparator<T> {


    ////////////
    // FIELDS //
    ////////////

    // Special others group to indicate where objects
    // should be sorted if they do not correspond
    // to any other criteria in the order list.
    public static final Object OTHERS = new Object();

    private Object[] orderedGroupCriteria;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GroupSequenceComparator(Object... orderedGroupCriteria) {
        this.orderedGroupCriteria = orderedGroupCriteria;
    }
    public GroupSequenceComparator(List<Object> orderedGroupCriteriaList) {
        orderedGroupCriteria = orderedGroupCriteriaList.toArray();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int compare(T o1, T o2) {
        if(EqualsUtil.equals(o1, o2)) {
            return 0;
        }
        int othersPos = -2;                                           // -2 unambiguously indicates otherPos not initialized yet
        int i1 = findApplicableIndex(orderedGroupCriteria, o1);
        int i2 = findApplicableIndex(orderedGroupCriteria, o2);
        if(i1 == -1) {
            othersPos = ArrayUtil.indexOf(orderedGroupCriteria, OTHERS);   // Lazy initialization of OTHERS index
            if(othersPos == -1) {
                throw new IllegalArgumentException("o1 not in order array");
            }
            i1 = othersPos;
        }
        if(i2 == -1) {
            if(othersPos == -2) {
                othersPos = ArrayUtil.indexOf(orderedGroupCriteria, OTHERS);   // Lazy initialization of OTHERS index
            }
            if(othersPos == -1) {
                throw new IllegalArgumentException("o2 not in order array");
            }
            i2 = othersPos;
        }
        if(i1 < i2) {
            return -1;
        } else if(i1 > i2) {
            return 1;
        }
        return 0;       // Happens if both are represented by OTHERS or same acceptor
    }

    private static int findApplicableIndex(Object[] orderedCriteria, Object o) {
        for(int i = 0; i < orderedCriteria.length; i++) {
            Object crit = orderedCriteria[i];
            if(crit instanceof ComparePredicate) {
                ComparePredicate acceptor = (ComparePredicate) crit;
                if(acceptor.test(o)) {
                    return i;
                }
            } else if(EqualsUtil.equals(o, crit)) {  // Consider 1.8 Objects.deepEquals()
                return i;
            }
        }
        return -1;
    }
}
