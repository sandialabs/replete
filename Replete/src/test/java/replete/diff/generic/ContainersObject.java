package replete.diff.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContainersObject {
    public String[] stringArray;
    public int[][] doubleIntArray;

    public List<Double> doubleList;
    public Map<String, Integer> stringIntegerMap;
    public Set<Integer> integerSet;

    public ContainersObject(boolean version) {
        if(version) {
            stringArray = new String[] {"This", "is", "TestObject", "One", "Extra Word"};
            doubleIntArray = new int[][] {{1, 2, 3}, {4, 5, 6, 7}, {9, 8, 7}};

            doubleList = new ArrayList<>();
            doubleList.add(1d);
            doubleList.add(1.1);
            doubleList.add(1.11);
            doubleList.add(9.99);

            stringIntegerMap = new HashMap<>();
            stringIntegerMap.put("key 1", 1);
            stringIntegerMap.put("key 2", 2);
            stringIntegerMap.put("key 3", 3);
            stringIntegerMap.put("key 9", 9);

            integerSet = new HashSet<>();
            integerSet.add(1);
            integerSet.add(2);
            integerSet.add(3);
            integerSet.add(7);
            integerSet.add(9);

        } else {

            stringArray = new String[] {"This", "is", "TestObject", "Two"};
            doubleIntArray = new int[][] {{1, 2, 3}, {7, 8, 9}};

            doubleList = new ArrayList<>();
            doubleList.add(2d);
            doubleList.add(2.2);
            doubleList.add(1.11);

            stringIntegerMap = new HashMap<>();
            stringIntegerMap.put("key 1", 1);
            stringIntegerMap.put("key 2", 3);
            stringIntegerMap.put("key 3", 5);

            integerSet = new HashSet<>();
            integerSet.add(1);
            integerSet.add(3);
            integerSet.add(7);
            integerSet.add(9);
        }
    }
}
