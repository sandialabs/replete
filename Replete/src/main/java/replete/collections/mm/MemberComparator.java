package replete.collections.mm;

/**
 * @author Derek Trumbo
 */

public interface MemberComparator<E, F> {
    public int compareTo(MembershipMap<E, F> map, E group, F member1, F member2);
}
