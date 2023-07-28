package replete.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import replete.collections.ArrayUtil;
import replete.equality.EqualsUtil;
import replete.text.StringUtil;

public class CompareUtil {


    ///////////
    // FIELD //
    ///////////

    // Special others group to indicate where objects
    // should be sorted if they do not correspond
    // to any other criteria in the order list.
    public static final Object OTHERS = new Object();


    ///////////////////
    // GROUP COMPARE //
    ///////////////////

    public static int compareByGroup(
                     Object o1, Object o2, Object[] orderedGroupCriteria) {
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


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        List<Record> list = new ArrayList<>();

        list.add(new Record("C",  2, "Extra",   Type.Farmers));
        list.add(new Record("B", 10, "Apples",  Type.Paupers));
        list.add(new Record("A", 20, "Hi",      Type.Nobles));
        list.add(new Record("C",  2, "Extra",   Type.Nobles));
        list.add(new Record("C",  2, "Extra",   Type.Royals));
        list.add(new Record("A", 17, "Hello",   Type.Farmers));
        list.add(new Record("B",  1, "Goodbye", Type.Nobles));
        list.add(new Record("B",  1, "Hi",      Type.Farmers));
        list.add(new Record("C",  2, "Extra",   Type.Paupers));
        list.add(new Record("C",  1, "Apples",  Type.Paupers));

        GroupSequenceComparator gsc1 = new GroupSequenceComparator<Type>(Type.values());

        CompositeComparatorBuilder<Record> builder = new CompositeComparatorBuilder<>();
        builder.addComparator("name", (o1, o2) -> o1.name.compareTo(o2.name));
        builder.addComparator("age",  (o1, o2) -> new Integer(o1.age).compareTo(o2.age));
        builder.addComparator("text", (o1, o2) -> o1.text.compareTo(o2.text));
        //builder.addChildComparator("type", (o1, o2) -> gsc1.compare(o1.type, o2.type));
        builder.addComparator("type", new FieldIndirectionComparator<Record, Type>("type", gsc1));
        ComparePredicate<Record> specialPredicate =
            o -> o.name.equals("B")     &&
                 o.type == Type.Farmers &&
                 o.text.endsWith("i")   ||
                 o.name.equals("C")     &&
                 o.age == 2             &&
                 o.type == Type.Paupers;
        ;

        Object[] orderedGroupCriteria = new Object[] {specialPredicate, GroupSequenceComparator.OTHERS};
        GroupSequenceComparator gsc2 = new GroupSequenceComparator<Record>(orderedGroupCriteria);
        builder.addComparator("special", (o1, o2) -> gsc2.compare(o1, o2));

        String[] aspectOrder = new String[] {"special", "name", "age", "text", "type"};
        Collections.sort(list, builder.build(aspectOrder));

        System.out.printf("%1s  %1s  %3s  %-7s  %s%n", "S", "N", "Age", "Text", "Type");
        System.out.printf("%1s  %1s  %3s  %-7s  %s%n", "=", "=", "===", "====", "====");
        for(Record r : list) {
            System.out.println(r.toString(specialPredicate.test(r)));
        }

        System.out.println("\nOrder: " + StringUtil.join(aspectOrder, " > "));
    }

    private static enum Type {
        Royals,
        Nobles,
        Farmers,
        Paupers
    }

    private static class Record {
        String name;
        int age;
        String text;
        Type type;
        public Record(String name, int age, String text, Type type) {
            this.name = name;
            this.age = age;
            this.text = text;
            this.type = type;
        }

        // Externally derived/computed/applied property told to the object.
        public String toString(boolean special) {
            return String.format("%1s  %1s  %3d  %-7s  %s", (special?"!":""), name, age, text, type);
        }
    }
}
