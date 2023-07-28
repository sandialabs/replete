package replete.collections.mm;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import replete.text.StringUtil;

/**
 * This class makes it easy to keep track of relationships
 * between arbitrary pairs of objects.  At it's most simplest
 * level it is merely a map between one kind of object and
 * another map which is a map of a second kind of object to
 * a count.  The count represents the number of times the
 * relationship between the first type of object and the
 * second kind of object exists.  Here is a textual
 * representation:
 *
 * Groups          Members
 *   A -> { X -> 1, Y -> 1, Z -> 1 }
 *   B -> { Q -> 7 }
 *   C -> { S -> 1, T -> 20, U -> 35, V -> 100 }
 *
 * The main goal is to be able to count relationships between
 * pairs of objects.  To increase understandability of the
 * data structure and why it's implemented with maps, the terms
 * 'group' and 'member' are applied to this class.  Each time
 * a 'member' exists in a 'group', the relationship can be
 * added to this map.  If the group doesn't exist, a new
 * group-member map pair is added to the membership map. If the
 * group already exists, but the member is not in the group,
 * then the member is added to the group's member map, with an
 * initial count of 1.  If the group and the member exists in
 * that group's member map, the count integer tied to that member
 * is incremented (only for that group).
 *
 * The data structure IS the map, so all operations like put and
 * get are available - no details are hidden in that respect -
 * but an extensive API has been added to really make this class
 * usable.  Here is a summary:
 *
 *    addGroup(group) - Add an empty group if it doesn't exist.
 *    addMembership(group, member) - Create a relationship between
 *       a group and a member or increment the number of times the
 *       relationship exists between that group and member.
 *    ---
 *    getGroupCount() - The number of groups in the map.
 *    getMemberCount(group) - The number of members in a group's
 *       member map (the number of members that have had at least
 *       one relationship with the group).
 *    getMemberships(group, member) - The number of times the member
 *       exists in the group or the number of times a relationship has
 *       been registered between the group and the member.
 *    getMemberships(group) - The total number of relationships
 *       that a group has.  This essentially sums the counts for
 *       each member in the group.
 *    getAllMemberships() - The total number of relationships that
 *       have been registered in the map.  This is essentially sums
 *       all the counts for all the members in all the groups.  It
 *       is also essentially equal to the number of times that
 *       addMembership(group, member) has been called, as long as you
 *       are not changing the counts via any other method.
 *    ---
 *    getMembers(group) - Returns the member map for the given group.
 *       This is a map of member objects to counts.
 *    ---
 *    toShortString() - A one-line-per-group string representation of the
 *       entire map.
 *    toShortString(group) - A one-line string representation of the group.
 *    toLongString() - A multi-line string representation of the entire map.
 *    toLongString(group) - A multi-line string representation of the group.
 *    ---
 *    groups() - An iterator over the groups in the map.  This will visit
 *       the groups based on their 'natural ordering', since the membership
 *       map is a TreeMap.
 *    groups(comparator) - An iterator over the groups in the map that will
 *       visit the groups in the order specified by the comparator.
 *    members(group) - An iterator over the members that a single group has.
 *       This will visit the members based on their 'natural ordering',
 *       since each group's member map is also a TreeMap.
 *    members(group, comparator) - An iterator over the members that a single
 *       group has that will visit the members in the order specified by the
 *       comparator.
 *    ---
 *    toReversedMap() - Returns a map that turns the members into groups
 *       and groups into members.  All the counts are maintained.
 *
 *  @author Derek Trumbo
 */

@SuppressWarnings("serial")
public class MembershipMap<E, F> extends TreeMap<E, TreeMap<F, Integer>> {


    ////////////
    // FIELDS //
    ////////////

    private int providingSets = 0;
    private MapCreator groupCreator;
    private MapCreator memberCreator;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MembershipMap() {
        this(null, null);
    }
    public MembershipMap(MapCreator groupCreator, MapCreator memberCreator) {
        if(groupCreator == null) {
            groupCreator = new TreeMapMapCreator();
        }
        if(memberCreator == null) {
            memberCreator = new TreeMapMapCreator();
        }

        this.groupCreator = groupCreator;
        this.memberCreator = memberCreator;
    }


    //////////////
    // ADDITION //
    //////////////

    /**
     * Creates a new group in the map if it does not
     * exist.  A blank member count map will be placed
     * in the membership map for this group.  If the
     * group already exists, nothing happens.  The
     * member count map is returned.
     */
    public Map<F, Integer> addGroup(E group) {

        // Get the member count map for this group.
        TreeMap<F, Integer> memberCountMap = get(group);
        if(memberCountMap == null) {
            memberCountMap = new TreeMap<>();
            put(group, memberCountMap);
        }

        return memberCountMap;
    }

    /**
     * Creates a group-member relationship in this
     * membership map.  If the group-member relationship
     * already exists, increments the number of times
     * that that relationship exists.
     */
    public Map<F, Integer> addMembership(E group, F member) {

        Map<F, Integer> memberCountMap = addGroup(group);

        // Put the member into the member count map or
        // increment the number of times it belongs to
        // the group.
        Integer count = memberCountMap.get(member);
        if(count == null) {
            memberCountMap.put(member, 1);
        } else {
            memberCountMap.put(member, count + 1);
        }

        return memberCountMap;
    }


    /////////////
    // STRINGS //
    /////////////

    /**
     * Creates a one-line per group output string.
     */
    public String toShortString() {
        String ret = "";
        for(E group : keySet()) {
            ret += toShortString(group) + "\n";
        }
        return ret;
    }

    /**
     * Creates a one-line output for a given group
     * in the map.
     */
    public String toShortString(E group) {
        TreeMap<F, Integer> memberCountMap = getMembers(group);
        if(memberCountMap == null) {
            return group + " is not a valid group in this membership map";
        }
        String ret = group + " -> {";
        if(memberCountMap.size() != 0) {
            for(F member : memberCountMap.keySet()) {
                Integer count = memberCountMap.get(member);
                ret += member;
                if(count != 1) {
                    ret += "(" + count + ")";
                }
                ret += ", ";
            }
            ret = ret.substring(0, ret.length() - 2);
        }
        return ret + "}";
    }

    /**
     * Creates a multi-line output for each group
     * in the map.  Each member in the group gets
     * its own line.
     */
    public String toLongString() {
        String ret = "";
        for(E group : keySet()) {
            ret += toLongString(group);
        }
        return ret;
    }

    /**
     * Creates a multi-line output for a given group
     * in the map.  Each member in the group gets
     * its own line.
     */
    public String toLongString(E group) {
        TreeMap<F, Integer> memberCountMap = getMembers(group);
        if(memberCountMap == null) {
            return group + " is not a valid group in this membership map\n";
        }
        String ret = group + " contains:\n";
        if(memberCountMap.size() != 0) {
            int sum = 0;
            for(F member : memberCountMap.keySet()) {
                Integer count = memberCountMap.get(member);
                ret += "   " + member + " (" + count + ")\n";
                sum += count;
            }
            ret += "   *TOTAL (" + sum + ")\n";
        } else {
            ret += "   <<no members>>\n";
        }
        return ret;
    }


    ////////////
    // COUNTS //
    ////////////

    /**
     * Returns the number of groups in the map.
     */
    public int getGroupCount() {
        return size();
    }

    public int getProvidingSets() {
        return providingSets;
    }
    public void incrementProvidingSets() {
        providingSets++;
    }

    /**
     * Returns the number of members that have
     * been added to this group.  Doesn't account
     * for each time each member was added to
     * the group.
     */
    public int getMemberCount(E group) {
        TreeMap<F, Integer> memberCountMap = getMembers(group);
        if(memberCountMap == null) {
            return 0;
        }
        return memberCountMap.size();
    }

    /**
     * Returns the total number of times a member
     * has been added to this group.
     */
    public int getMemberships(E group) {
        int instances = 0;
        Map<F, Integer> memberCountMap = getMembers(group);
        if(memberCountMap != null) {
            for(Integer count : memberCountMap.values()) {
                instances += count;
            }
        }
        return instances;
    }

    public int getMemberships(E group, F member) {
        Map<F, Integer> memberCountMap = getMembers(group);
        if(memberCountMap == null) {
            return 0;
        }
        Integer count = memberCountMap.get(member);
        if(count == null) {
            return 0;
        }
        return count;
    }

    public int getAllMemberships() {
        int instances = 0;
        for(E group : keySet()) {
            instances += getMemberships(group);
        }
        return instances;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public TreeMap<F, Integer> getMembers(E group) {
        return get(group);
    }


    ///////////////
    // ITERATORS //
    ///////////////

    public Iterable<E> groups() {
        return groups(null);
    }

    public Iterable<E> groups(GroupComparator<E, F> comparator) {
        return new GroupIterator(comparator);
    }

    public Iterable<Member<F>> members(E group) {
        return members(group, null);
    }

    public Iterable<Member<F>> members(E group, MemberComparator<E, F> comparator) {
        if(getMembers(group) == null) {
            return null;
        }
        return new MemberIterator(group, comparator);
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    protected class GroupIterator implements Iterable<E>, Iterator<E> {
        private E[] groups;
        private int current;
        @SuppressWarnings("unchecked")
        public GroupIterator(final GroupComparator<E, F> comparator) {
            groups = (E[]) keySet().toArray();
            if(comparator != null) {
                Arrays.sort(groups, new Comparator<E>() {
                    public int compare(E group1, E group2) {
                        return comparator.compareTo(MembershipMap.this, group1, group2);
                    }
                });
            }
            current = 0;
        }
        public Iterator<E> iterator() {
            return this;
        }
        public boolean hasNext() {
            return current != groups.length;
        }
        public E next() {
            if(current >= groups.length) {
                throw new NoSuchElementException();
            }
            return groups[current++];
        }
        public void remove() {
        }
    }

    protected class MemberIterator implements Iterable<Member<F>>, Iterator<Member<F>> {
        private F[] members;
        private Integer[] counts;
        private int current;
        @SuppressWarnings("unchecked")
        public MemberIterator(final E group, final MemberComparator<E, F> comparator) {
            TreeMap<F, Integer> mems = getMembers(group);
            members = (F[]) mems.keySet().toArray();
            if(comparator != null) {
                Arrays.sort(members, new Comparator<F>() {
                    public int compare(F member1, F member2) {
                        return comparator.compareTo(MembershipMap.this, group, member1, member2);
                    }
                });
            }
            counts = new Integer[members.length];
            for(int m = 0; m < members.length; m++) {
                counts[m] = mems.get(members[m]);
            }
            current = 0;
        }
        public Iterator<Member<F>> iterator() {
            return this;
        }
        public boolean hasNext() {
            return current != members.length;
        }
        public Member<F> next() {
            if(current >= members.length) {
                throw new NoSuchElementException();
            }
            Member<F> member = new Member<F>(members[current], counts[current]);
            current++;
            return member;
        }
        public void remove() {
        }
    }


    /////////////
    // REVERSE //
    /////////////

    public MembershipMap<F, E> toReversedMap() {
        MembershipMap<F, E> reversedMap = new MembershipMap<F, E>();

        for(E group : keySet()) {
            TreeMap<F, Integer> memberCountMap = get(group);
            if(memberCountMap != null) {
                for(F member : memberCountMap.keySet()) {
                    Integer count = memberCountMap.get(member);
                    Map<E, Integer> reversedMemberCountMap = reversedMap.addMembership(member, group);
                    reversedMemberCountMap.put(group, count);
                }
            }
        }

        return reversedMap;
    }


    ///////////
    // PRINT //
    ///////////

    public void printTable() {
        int allM = getAllMemberships();

        int cntLen = Integer.MIN_VALUE;
        int lblLen = Integer.MIN_VALUE;
        for(E e : keySet()) {
            String lbl = (e == null ? "(null)" : e.toString());
            lbl = StringUtil.max(lbl, 60);
            if(lbl.length() > lblLen) {
                lblLen = lbl.length();
            }

            int groupCount = getMemberships(e);
            String str = StringUtil.commas(groupCount);
            if(str.length() > cntLen) {
                cntLen = str.length();
            }

            Map<F, Integer> memberCountMap = get(e);
            if(memberCountMap != null) {
                for(F f : memberCountMap.keySet()) {
                    lbl = "  " + (f == null ? "(null)" : f.toString());
                    lbl = StringUtil.max(lbl, 60);
                    if(lbl.length() > lblLen) {
                        lblLen = lbl.length();
                    }

                    Integer count = memberCountMap.get(f);
                    str = StringUtil.commas(count);
                    if(str.length() > cntLen) {
                        cntLen = str.length();
                    }
                }
            }
        }

        lblLen = Math.max("GROUP/MEMBER".length(), lblLen);
        cntLen = Math.max("COUNT".length(), cntLen);
        boolean hasProvidingSets = providingSets != 0;

        System.out.print("All Memberships: " + allM);
        if(providingSets != 0) {
            System.out.println(", Providing Sets: " + providingSets);
        } else {
            System.out.println();
        }
        if(hasProvidingSets) {
            String pattern = "%-" + lblLen + "s  %" + cntLen + "s  %7s  %7s  %7s%n";
            System.out.printf(pattern, "GROUP/MEMBER", "COUNT", "GROUP %", "ALL %", "PS %");
            System.out.printf(pattern, "============", "=====", "=======", "=====", "====");
        } else {
            String pattern = "%-" + lblLen + "s  %" + cntLen + "s  %7s  %7s%n";
            System.out.printf(pattern, "GROUP/MEMBER", "COUNT", "GROUP %", "ALL %");
            System.out.printf(pattern, "============", "=====", "=======", "=====");
        }

        for(E e : keySet()) {
            int groupCount = getMemberships(e);

            double groupAllPct = ((double) groupCount / allM) * 100;
            String groupPattern;
            Object[] groupParams;
            if(hasProvidingSets) {
                groupPattern = "%-" + lblLen + "s  %" + cntLen + "s  %7s  %6.2f%%  %7s%n";
                groupParams = new Object[] {
                    StringUtil.max(e == null ? "(null)" : e.toString(), 60),
                    StringUtil.commas(groupCount),
                    "*",
                    groupAllPct,
                    "*"
                };
            } else {
                groupPattern = "%-" + lblLen + "s  %" + cntLen + "s  %7s  %6.2f%%%n";
                groupParams = new Object[] {
                    e,
                    StringUtil.commas(groupCount),
                    "*",
                    groupAllPct
                };
            }
            System.out.printf(groupPattern, groupParams);

            Map<F, Integer> memberCountMap = get(e);
            if(memberCountMap != null) {
                for(F f : memberCountMap.keySet()) {
                    Integer count = memberCountMap.get(f);
                    double memberGroupPct = ((double) count / groupCount) * 100;
                    double memberAllPct = ((double) count / allM) * 100;

                    String memberPattern;
                    Object[] memberParams;
                    if(hasProvidingSets) {
                        double memberRsPct = ((double) count / providingSets) * 100;
                        memberPattern = "%-" + lblLen + "s  %" + cntLen + "s  %6.2f%%  %6.2f%%  %6.2f%%%n";
                        memberParams = new Object[] {
                            StringUtil.max("  " + (f == null ? "(null)" : f.toString()), 60),
                            StringUtil.commas(count),
                            memberGroupPct,
                            memberAllPct,
                            memberRsPct
                        };
                    } else {
                        memberPattern = "%-" + lblLen + "s  %" + cntLen + "s  %6.2f%%  %6.2f%%%n";
                        memberParams = new Object[] {
                            "  " + (f == null ? "(null)" : f.toString()),
                            StringUtil.commas(count),
                            memberGroupPct,
                            memberAllPct,
                        };
                    }
                    System.out.printf(memberPattern, memberParams);
                }
            }
        }
    }


    //////////
    // COPY //
    //////////

    public MembershipMap<E, F> copy() {
        MembershipMap<E, F> copy = new MembershipMap<>();
        for(E e : keySet()) {
            Map<F, Integer> memberCountMap = get(e);
            if(memberCountMap != null) {
                TreeMap<F, Integer> copyMembers = new TreeMap<>();
                for(F f : memberCountMap.keySet()) {
                    Integer count = memberCountMap.get(f);
                    copyMembers.put(f, count);
                }
                copy.put(e, copyMembers);
            } else {
                copy.put(e, null);
            }
        }
        return copy;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        MembershipMap<A, B> map = new MembershipMap<>();
        map.addMembership(new A("1"), new B("Tiger"));
        map.addMembership(new A("1"), new B("Lion"));
        map.addMembership(new A("2"), new B("Tiger"));
        map.addMembership(new A("1"), new B("Butterfly"));
        map.addMembership(new A("2"), new B("Lion"));
        map.addMembership(new A("1"), new B("Tiger"));
        map.addMembership(new A("1"), new B("Lion"));
        map.addMembership(new A("2"), new B("Tiger"));
        map.addMembership(new A("2"), new B("Lion"));
        map.addMembership(new A("2"), new B("Cat"));
        map.addMembership(new A("1"), new B("Dog"));
        map.addMembership(new A("2"), new B("Cat"));
        map.addMembership(new A("1"), new B("Dog"));
        map.addMembership(new A("3"), new B("Frog"));
        map.addMembership(new A("3"), new B("Frog"));
        map.addMembership(new A("3"), new B("Frog"));
        map.addGroup(new A("4"));
        map.addMembership(new A("5"), new B("Zebra"));
        map.addMembership(new A("5"), new B("Zebra2"));
        map.addMembership(new A("5"), new B("Zebra2"));
        map.addMembership(new A("5"), new B("Zebra2"));
        map.addMembership(new A("5"), new B("Zebra3"));
        map.addMembership(new A("5"), new B("Zebra3"));
        map.addMembership(new A("5"), new B("Zebra3"));
        map.addMembership(new A("5"), new B("Zebra3"));
        map.addMembership(new A("5"), new B("Zebra4"));
        map.addMembership(new A("5"), new B("Zebra4"));
        map.addMembership(new A("5"), new B("Zebra5"));
        map.addMembership(new A("5"), new B("Zebra5"));
        map.addMembership(new A("5"), new B("Zebra5"));
        map.addMembership(new A("5"), new B("Zebra5"));
        map.addMembership(new A("5"), new B("Zebra5"));
        map.put(new A("6"), null);

        System.out.println(map.toLongString() + "----------");
        System.out.println(map.toShortString() + "----------");
        System.out.println("All memberships: " + map.getAllMemberships());

        for(A key : map.keySet()) {
            System.out.println("Group memberships: " + key + ": " + map.getMemberships(key));
            System.out.println(map.toShortString(key));
            System.out.print(map.toLongString(key));
        }
        System.out.println("----------");
        System.out.println("Iterator Test");
        for(A key : map.groups()) {
            System.out.println("group: " + key + ": " + map.getMemberships(key) + " " + map.toShortString(key));
        }
        for(Member<B> val : map.members(new A("5"))) {
            System.out.println("member: " + val.value + " (" + val.count + ")");
        }
        System.out.println("----------");
        System.out.println("Iterator Test w/ Sort");
        GroupComparator<A, B> comparator = new GroupComparator<A, B>() {
            public int compareTo(MembershipMap<A, B> map, A group1, A group2) {
                int mems1 = map.getMemberCount(group1);
                int mems2 = map.getMemberCount(group2);
                return mems2 - mems1;
            }
        };
        for(A key : map.groups(comparator)) {
            System.out.println("group: " + key + ": " + map.getMemberships(key) + " " + map.toShortString(key));
        }
        MemberComparator<A, B> comparator2 = new MemberComparator<A, B>() {
            public int compareTo(MembershipMap<A, B> map, A group, B member1, B member2) {
                int cnt1 = map.getMembers(group).get(member1);
                int cnt2 = map.getMembers(group).get(member2);
                return cnt2 - cnt1;
            }
        };
        for(Member<B> val : map.members(new A("5"), comparator2)) {
            System.out.println("member: " + val.value + " (" + val.count + ")");
        }
        System.out.println("----------");

        MembershipMap<B, A> reversedMap = map.toReversedMap();

        System.out.println(reversedMap.toLongString() + "----------");
        System.out.println(reversedMap.toShortString() + "----------");
        System.out.println("All memberships: " + reversedMap.getAllMemberships());

        for(B key : reversedMap.keySet()) {
            System.out.println("Group memberships: " + key + ": " + reversedMap.getMemberships(key));
            System.out.println(reversedMap.toShortString(key));
            System.out.print(reversedMap.toLongString(key));
        }

        map.incrementProvidingSets();
        map.incrementProvidingSets();
        map.incrementProvidingSets();
        map.incrementProvidingSets();
        map.incrementProvidingSets();
        map.printTable();
    }

    public static class A implements Comparable<A> {
        public String S;
        public A(String s) {S = s;}
        public int compareTo(A o) {
            return S.compareTo(o.S);
        }
        @Override
        public String toString() {
            return "[A:"+S+"]";
        }
    }

    public static class B implements Comparable<B> {
        public String S;
        public B(String s) {S = s;}
        public int compareTo(B o) {
            return S.compareTo(o.S);
        }
        @Override
        public String toString() {
            return "[B:"+S+"]";
        }
    }
}
