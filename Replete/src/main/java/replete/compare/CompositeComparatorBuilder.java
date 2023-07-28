package replete.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.collections.Pair;

public class CompositeComparatorBuilder<T> {


    ////////////
    // FIELDS //
    ////////////

    private Map<String, Comparator<T>> comparators = new HashMap<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Map<String, Comparator<T>> getComparators() {
        return comparators;
    }

    // Mutators

    public CompositeComparatorBuilder<T> addComparator(String name, Comparator<T> comparator) {
        comparators.put(name, comparator);
        return this;
    }

    public CompositeSequentialComparator build(String... comparatorOrder) {
        return new CompositeSequentialComparator(comparatorOrder);
    }

    public List<Pair<Integer, Integer>> getDifferenceLocations(Collection<T> sortedElements, String... childComparatorOrder) {
        List<Pair<Integer, Integer>> cellDiffs = new ArrayList<>();
        T prevElem = null;
        int e = 0;
        for(T elem : sortedElements) {
            if(prevElem != null) {
                int f = 0;
                for(String name : childComparatorOrder) {
                    Comparator<T> comparator = comparators.get(name);
                    if(comparator == null) {
                        throw new IllegalArgumentException("Comparator '" + name + "' not registered");
                    }
                    int value = comparator.compare(prevElem, elem);
                    if(value != 0) {
                        cellDiffs.add(new Pair<>(e - 1, f));  // f ==> field, comparator, aspect
                        break;
                    }
                    f++;
                }
            }
            prevElem = elem;
            e++;
        }
        return cellDiffs;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class CompositeSequentialComparator implements Comparator<T> {
        private String[] comparatorOrder;
        public CompositeSequentialComparator(String[] comparatorOrder) {
            this.comparatorOrder = comparatorOrder;
        }
        @Override
        public int compare(T o1, T o2) {
            for(String name : comparatorOrder) {
                Comparator<T> comparator = comparators.get(name);
                if(comparator == null) {
                    throw new IllegalArgumentException("Comparator '" + name + "' not registered");
                }
                int value = comparator.compare(o1, o2);
                if(value != 0) {
                    return value;
                }
            }
            return 0;
        }
    }


    //////////
    // TEST //
    //////////

//    public static void main(String[] args) {
//        List<Person> x = new ArrayList<>();
//        x.add(new Person(x, y, z));
//        x.add(new Person(x, y, z));
//        x.add(new Person(x, y, z));
//        x.add(new Person(x, y, z));
//        x.add(new Person(x, y, z));
//
//        CompositeComparatorBuilder<Person> builder =
//            new CompositeComparatorBuilder<>();
//
//        builder.addChildComparator("XXX", new Comparator<Person>() {
//            @Override
//            public int compare(Person j1, Person j2) {
//                if(value != 0) {
//                    return value;
//                }
//                return 0;
//            }
//        });
//
//        Comparator<Person> comparator =
//            builder.build(
//                "XXX", "status", "createdTime", "name", "id");
//        Arrays.sort(jobs, comparator);
//    }
}
