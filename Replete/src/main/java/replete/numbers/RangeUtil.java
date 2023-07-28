package replete.numbers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import replete.collections.Pair;

public class RangeUtil {
    public static <T extends Number> List<Pair<T, T>> unifyRanges(List<Pair<T, T>> initPairs) {
        List<Pair<T, T>> result = new ArrayList<Pair<T, T>>();
        if(!initPairs.isEmpty()) {
            List<Pair<T, T>> initPairsCopy = new ArrayList<Pair<T, T>>(initPairs);
            Collections.sort(initPairsCopy, new Comparator<Pair<T, T>>() {
                public int compare(Pair<T, T> o1,
                        Pair<T, T> o2) {
                    double v1 = o1.getValue1().doubleValue();
                    double v2 = o2.getValue1().doubleValue();
                    if(v1 == v2) {
                        return 0;
                    } else if(v1 < v2) {
                        return -1;
                    }
                    return 1;
                }
            });

            T start = initPairsCopy.get(0).getValue1();
            T end = initPairsCopy.get(0).getValue2();
            for(Pair<T, T> pair : initPairsCopy) {
                if(pair.getValue1().doubleValue() > end.doubleValue()) { // End of range reached
                    Pair<T, T> newPair = new Pair<T, T>(start, end);
                    result.add(newPair);
                    start = pair.getValue1();
                    end = pair.getValue2();
                } else {
                    end = pair.getValue2();
                }
            }
            Pair<T, T> newPair = new Pair<T, T>(start, end);
            result.add(newPair);
        }
        return result;
    }
    public static void main(String[] args) {
        List<Pair<Integer, Integer>> pairs = new ArrayList<Pair<Integer,Integer>>();
        pairs.add(new Pair<Integer, Integer>(14, 20));
        pairs.add(new Pair<Integer, Integer>(12, 15));
        pairs.add(new Pair<Integer, Integer>(29, 35));
        pairs.add(new Pair<Integer, Integer>(3, 5));
        pairs.add(new Pair<Integer, Integer>(22, 25));
        pairs.add(new Pair<Integer, Integer>(27, 29));
        pairs.add(new Pair<Integer, Integer>(4, 7));
        pairs.add(new Pair<Integer, Integer>(27, 30));
        pairs.add(new Pair<Integer, Integer>(28, 29));

        for(Pair<Integer, Integer> pair : pairs) {
            System.out.println(pair);
        }
        System.out.println();

        List<Pair<Integer, Integer>> unifiedPairs = unifyRanges(pairs);
        for(Pair<Integer, Integer> pair : unifiedPairs) {
            System.out.println(pair);
        }
    }
}
