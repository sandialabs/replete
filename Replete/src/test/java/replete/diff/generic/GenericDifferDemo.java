package replete.diff.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import replete.diff.DiffTreePanel;
import replete.diff.generic.GenericObjectDiffer;
import replete.diff.generic.GenericObjectDifferParams;
import replete.diff.generic.ParentClassContainer.Parent;
import replete.diff.generic.ParentClassContainer.Recursable;
import replete.ui.lay.Lay;

public class GenericDifferDemo {

    public static void main(String[] args) {

        GenericObjectDifferParams params =
            new GenericObjectDifferParams()
        ;

        GenericObjectDifferParams paramsBlackList =
            new GenericObjectDifferParams()
                .setUseFunctionBlacklist(true)
                .addFieldToBlacklist("boolField")
                .addFieldToBlacklist("doubleIntArray")
        ;

        Recursable innerOne = new Recursable(12, null);
        Recursable o1 = new Recursable(11, innerOne);
        innerOne.setRecursiveField(o1);

        Recursable innerTwo = new Recursable(22, null);
        Recursable o2 = new Recursable(21, innerTwo);
        innerTwo.setRecursiveField(o2);

        GenericObjectDiffer differ = new GenericObjectDiffer(params);
        DiffTreePanel centerDiffPanel = new DiffTreePanel();
        centerDiffPanel.setCurrentResult(differ.diff(o1, o2), "Blacklist", "o1", "o2");

        GenericDifferDemoObject d1 =
            new GenericDifferDemoObject(
                new GenericDifferDemoObject(
                    new GenericDifferDemoObject(null, false),
                    false),
                true)
        ;
        GenericDifferDemoObject d2 =
            new GenericDifferDemoObject(
                new GenericDifferDemoObject(
                    new GenericDifferDemoObject(null, true),
                    true),
                false)
        ;

        GenericObjectDiffer differOnlyPrimitives = new GenericObjectDiffer(paramsBlackList);
        DiffTreePanel rightDiffPanel = new DiffTreePanel();
        rightDiffPanel.setCurrentResult(differOnlyPrimitives.diff(d1, d2), "Whitelist", "new", "old");

        Lay.GLtg(Lay.fr(), 1, 2,
            centerDiffPanel,
            rightDiffPanel,
            "size=800,center,visible"
        );

    }

    public static class GenericDifferDemoObject {
        private int intField;
        public String stringField;
        public boolean boolField;
        public Float floatField;
        public String[] stringArray;
        private int[][] doubleIntArray;
        private List<Double> doubleList;
        public Map<String, Integer> stringIntegerMap;
        private Set<Integer> integerSet;
        public Iterable<Integer> integerIterable;
        private GenericDifferDemoObject subObject;
        public Parent classTypeTestObject;

        public GenericDifferDemoObject setSubObject(GenericDifferDemoObject subObject) {
            this.subObject = subObject;
            return this;
        }

        public GenericDifferDemoObject(GenericDifferDemoObject subObject, boolean diff) {
            this.subObject = subObject;
            if(diff) {
                intField = 1;
                stringField = "Object One";
                boolField = true;
                floatField = 1.11F;
                stringArray = new String[] {"This", "is", "TestObject", "One", "LEFT", "b"};
                doubleIntArray = new int[][] {{1, 2, 3}, {4, 5, 6}};

                doubleList = new ArrayList<>();
                doubleList.add(1d);
                doubleList.add(1.1);
                doubleList.add(1.11);

                stringIntegerMap = new HashMap<>();
                stringIntegerMap.put("key 1", 1);
                stringIntegerMap.put("key 2", 2);
                stringIntegerMap.put("key 3", 3);

                List<Integer> integers = new ArrayList();
                integers.add(10);
                integers.add(11);
                integers.add(12);
                integerIterable = integers;

                integerSet = new HashSet<>();
                integerSet.add(1);
                integerSet.add(2);
                integerSet.add(3);
                integerSet.add(7);

                classTypeTestObject = new ParentClassContainer.Child1();
            } else {
                intField = 2;
                stringField = "Object Two";
                boolField = false;
                floatField = 2.22F;
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

                List<Integer> integers = new ArrayList<>();
                integers.add(10);
                integers.add(21);
                integers.add(22);
                integerIterable = integers;

                integerSet = new HashSet<>();
                integerSet.add(1);
                integerSet.add(3);
                integerSet.add(7);
                integerSet.add(9);

                classTypeTestObject = new ParentClassContainer.Child2();
            }
        }

        public GenericDifferDemoObject(boolean diff) {
            subObject = this;
            if(diff) {
                intField = 1;
                stringField = "Object One";
                boolField = true;
                floatField = 1.11F;
                stringArray = new String[] {"This", "is", "TestObject", "One", "LEFT", "b"};
                doubleIntArray = new int[][] {{1, 2, 3}, {4, 5, 6}};

                doubleList = new ArrayList<>();
                doubleList.add(1d);
                doubleList.add(1.1);
                doubleList.add(1.11);

                stringIntegerMap = new HashMap<>();
                stringIntegerMap.put("key 1", 1);
                stringIntegerMap.put("key 2", 2);
                stringIntegerMap.put("key 3", 3);

                List<Integer> integers = new ArrayList();
                integers.add(10);
                integers.add(11);
                integers.add(12);
                integerIterable = integers;

                integerSet = new HashSet<>();
                integerSet.add(1);
                integerSet.add(2);
                integerSet.add(3);
                integerSet.add(7);

                classTypeTestObject = new ParentClassContainer.Child1();
            } else {
                intField = 2;
                stringField = "Object Two";
                boolField = false;
                floatField = 2.22F;
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

                List<Integer> integers = new ArrayList<>();
                integers.add(10);
                integers.add(21);
                integers.add(22);
                integerIterable = integers;

                integerSet = new HashSet<>();
                integerSet.add(1);
                integerSet.add(3);
                integerSet.add(7);
                integerSet.add(9);

                classTypeTestObject = new ParentClassContainer.Child2();
            }
        }
    }
}
