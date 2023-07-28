package replete.collections.mm;

/**
 * @author Derek Trumbo
 */

public interface GroupComparator<E, F> {
    public int compareTo(MembershipMap<E, F> map, E group1, E group2);
}
