package replete.diff.generic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import replete.diff.DiffResult;
import replete.diff.Importance;
import replete.diff.ListMapComparison;
import replete.diff.ObjectComparison;
import replete.diff.generic.ParentClassContainer.Child1;
import replete.diff.generic.ParentClassContainer.Child2;
import replete.diff.generic.ParentClassContainer.DuplicateHolder;
import replete.diff.generic.ParentClassContainer.Parent;
import replete.diff.generic.ParentClassContainer.ParentContainer;
import replete.diff.generic.ParentClassContainer.Recursable;
import replete.diff.generic.ParentClassContainer.StaticHolder;

public class GenericObjectDifferTest {

    DiffResult expectedResult;
    DiffResult actualResult;
    GenericObjectDifferParams params;
    GenericObjectDiffer diff;
    Object o1;
    Object o2;

    @Test(expected = InvalidTypeException.class)
    public void testPrimitiveTypes() {
        o1 = new Integer(4);
        o2 = new Integer(6);

        params = new GenericObjectDifferParams();

        actualResult = new GenericObjectDiffer(params).diff(o1, o2);
    }
    @Test(expected = InvalidTypeException.class)
    public void testNullException() {
        o1 = null;
        o2 = null;

        params = new GenericObjectDifferParams();

        actualResult = new GenericObjectDiffer(params).diff(o1, o2);
    }

    @Test
        public void testMatchingNull() {
            o1 = new ParentContainer(null);
            o2 = new ParentContainer(null);

            params = new GenericObjectDifferParams();
            actualResult = new GenericObjectDiffer(params).diff(o1, o2);

            expectedResult = new DiffResult();
            expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [0] : field", "(NULL)", "(NULL)");

            assertEquals(expectedResult, actualResult);
        }
    @Test
    public void testNotMatchingNull() {
        o1 = new ParentContainer(null);
        o2 = new ParentContainer(new Child1());

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [0] : field", "(NULL)", ((ParentContainer) o2).field.toString());

        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testAllPrimitivesFieldsMatching() {
        o1 = new PrimitivesObject(true);
        o2 = new PrimitivesObject(true);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [0] : boolField", "true", "true");
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [1] : byteField", "1", "1");
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [2] : charField", "1", "1");
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [3] : doubleField", "1.11", "1.11");
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [4] : floatField", "1.1", "1.1");
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [5] : intField", "111", "111");
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [6] : longField", "1111", "1111");
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [7] : shortField", "11", "11");
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [8] : stringField", "One", "One");

        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testAllPrimitivesFieldsNotMatching() {
        o1 = new PrimitivesObject(true);
        o2 = new PrimitivesObject(false);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [0] : boolField", "true", "false");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [1] : byteField", "1", "2");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [2] : charField", "1", "2");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [3] : doubleField", "1.11", "2.22");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [4] : floatField", "1.1", "2.2");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [5] : intField", "111", "222");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [6] : longField", "1111", "2222");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [7] : shortField", "11", "22");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [8] : stringField", "One", "Two");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNestedClassMatching() {
        o1 = new OuterObject(new PrimitivesObject(true), true);
        o2 = new OuterObject(new PrimitivesObject(true), true);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        ObjectComparison objectComparison = new ObjectComparison();
        objectComparison.addDifference(false, Importance.HIGH, "Field [0] : boolField", "true", "true");
        objectComparison.addDifference(false, Importance.HIGH, "Field [1] : byteField", "1", "1");
        objectComparison.addDifference(false, Importance.HIGH, "Field [2] : charField", "1", "1");
        objectComparison.addDifference(false, Importance.HIGH, "Field [3] : doubleField", "1.11", "1.11");
        objectComparison.addDifference(false, Importance.HIGH, "Field [4] : floatField", "1.1", "1.1");
        objectComparison.addDifference(false, Importance.HIGH, "Field [5] : intField", "111", "111");
        objectComparison.addDifference(false, Importance.HIGH, "Field [6] : longField", "1111", "1111");
        objectComparison.addDifference(false, Importance.HIGH, "Field [7] : shortField", "11", "11");
        objectComparison.addDifference(false, Importance.HIGH, "Field [8] : stringField", "One", "One");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [0] : innerObject", objectComparison);
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [1] : intField", "1", "1");
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [2] : stringField", "One", "One");

        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testNestedClassNotMatching() {
        o1 = new OuterObject(new PrimitivesObject(true), true);
        o2 = new OuterObject(new PrimitivesObject(false), false);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        ObjectComparison objectComparison = new ObjectComparison();
        objectComparison.addDifference(true, Importance.HIGH, "Field [0] : boolField", "true", "false");
        objectComparison.addDifference(true, Importance.HIGH, "Field [1] : byteField", "1", "2");
        objectComparison.addDifference(true, Importance.HIGH, "Field [2] : charField", "1", "2");
        objectComparison.addDifference(true, Importance.HIGH, "Field [3] : doubleField", "1.11", "2.22");
        objectComparison.addDifference(true, Importance.HIGH, "Field [4] : floatField", "1.1", "2.2");
        objectComparison.addDifference(true, Importance.HIGH, "Field [5] : intField", "111", "222");
        objectComparison.addDifference(true, Importance.HIGH, "Field [6] : longField", "1111", "2222");
        objectComparison.addDifference(true, Importance.HIGH, "Field [7] : shortField", "11", "22");
        objectComparison.addDifference(true, Importance.HIGH, "Field [8] : stringField", "One", "Two");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [0] : innerObject", objectComparison);
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [1] : intField", "1", "2");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [2] : stringField", "One", "Two");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testContainersNotMatching() {
        o1 = new ContainersObject(true);
        o2 = new ContainersObject(false);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();

        ListMapComparison subDoubleIntOne = new ListMapComparison(3, 3);
        subDoubleIntOne.addDifference(false, Importance.HIGH, "[0] : java.lang.Integer", "1", "1");
        subDoubleIntOne.addDifference(false, Importance.HIGH, "[1] : java.lang.Integer", "2", "2");
        subDoubleIntOne.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer", "3", "3");
        ListMapComparison subDoubleIntTwo = new ListMapComparison(4, 3);
        subDoubleIntTwo.addDifference(true, Importance.HIGH, "[0] : java.lang.Integer", "4", "7");
        subDoubleIntTwo.addDifference(true, Importance.HIGH, "[1] : java.lang.Integer", "5", "8");
        subDoubleIntTwo.addDifference(true, Importance.HIGH, "[2] : java.lang.Integer", "6", "9");
        subDoubleIntTwo.addDifference(true, Importance.HIGH, "[3] : java.lang.Integer", "7", "Right Array was not long enough");

        ListMapComparison doubleIntArrayComparison = new ListMapComparison(3, 2);
        doubleIntArrayComparison.addDifference(Importance.HIGH, "[0] : Array of java.lang.Integer", subDoubleIntOne);
        doubleIntArrayComparison.addDifference(Importance.HIGH, "[1] : Array of java.lang.Integer", subDoubleIntTwo);
        doubleIntArrayComparison.addDifference(true, Importance.HIGH, "[2] : Array of java.lang.Integer",
            ((ContainersObject) o1).doubleIntArray[2].toString(), "Right Array was not long enough");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [0] : doubleIntArray", doubleIntArrayComparison);

        ListMapComparison doubleListComparison = new ListMapComparison(4, 3);
        doubleListComparison.addDifference(true, Importance.HIGH, "[0] : java.lang.Double", "1.0", "2.0");
        doubleListComparison.addDifference(true, Importance.HIGH, "[1] : java.lang.Double", "1.1", "2.2");
        doubleListComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.Double", "1.11", "1.11");
        doubleListComparison.addDifference(true, Importance.HIGH,
            "[3] (Extra Left) : java.lang.Double", "9.99", "Right List was not long enough");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [1] : doubleList", doubleListComparison);

        ListMapComparison integerSetComparison = new ListMapComparison(5, 4);
        integerSetComparison.addDifference(false, Importance.HIGH, "[0] : java.lang.Integer", "1", "1");
        integerSetComparison.addDifference(true, Importance.HIGH,
            "[1] (Extra Left) : java.lang.Integer", "2", "Right Set did not provide a match");
        integerSetComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer", "3", "3");
        integerSetComparison.addDifference(false, Importance.HIGH, "[3] : java.lang.Integer", "7", "7");
        integerSetComparison.addDifference(false, Importance.HIGH, "[4] : java.lang.Integer", "9", "9");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [2] : integerSet", integerSetComparison);

        ListMapComparison stringArrayComparison = new ListMapComparison(5, 4);
        stringArrayComparison.addDifference(false, Importance.HIGH, "[0] : java.lang.String", "This", "This");
        stringArrayComparison.addDifference(false, Importance.HIGH, "[1] : java.lang.String", "is", "is");
        stringArrayComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.String", "TestObject", "TestObject");
        stringArrayComparison.addDifference(true, Importance.HIGH, "[3] : java.lang.String", "One", "Two");
        stringArrayComparison.addDifference(true, Importance.HIGH,
            "[4] : java.lang.String", "Extra Word", "Right Array was not long enough");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [3] : stringArray", stringArrayComparison);

        ListMapComparison stringIntegerMapComparison = new ListMapComparison(4, 3);
        stringIntegerMapComparison.addDifference(true, Importance.HIGH, "[0] : java.lang.Integer (Key - key 3) :", "3", "5");
        stringIntegerMapComparison.addDifference(true, Importance.HIGH, "[1] : java.lang.Integer (Key - key 2) :", "2", "3");
        stringIntegerMapComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer (Key - key 1) :", "1", "1");
        stringIntegerMapComparison.addDifference(true, Importance.HIGH,
            "[3] (Extra Left) : java.lang.Integer (Key - key 9) :", "9", "Right Map did not provide a match");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [4] : stringIntegerMap", stringIntegerMapComparison);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testContainersNotMatchingFlipped() {
        o1 = new ContainersObject(false);
        o2 = new ContainersObject(true);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();

        ListMapComparison subDoubleIntOne = new ListMapComparison(3, 3);
        subDoubleIntOne.addDifference(false, Importance.HIGH, "[0] : java.lang.Integer", "1", "1");
        subDoubleIntOne.addDifference(false, Importance.HIGH, "[1] : java.lang.Integer", "2", "2");
        subDoubleIntOne.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer", "3", "3");
        ListMapComparison subDoubleIntTwo = new ListMapComparison(3, 4);
        subDoubleIntTwo.addDifference(true, Importance.HIGH, "[0] : java.lang.Integer", "7", "4");
        subDoubleIntTwo.addDifference(true, Importance.HIGH, "[1] : java.lang.Integer", "8", "5");
        subDoubleIntTwo.addDifference(true, Importance.HIGH, "[2] : java.lang.Integer", "9", "6");
        subDoubleIntTwo.addDifference(true, Importance.HIGH, "[3] : java.lang.Integer", "Left Array was not long enough", "7");

        ListMapComparison doubleIntArrayComparison = new ListMapComparison(2, 3);
        doubleIntArrayComparison.addDifference(Importance.HIGH, "[0] : Array of java.lang.Integer", subDoubleIntOne);
        doubleIntArrayComparison.addDifference(Importance.HIGH, "[1] : Array of java.lang.Integer", subDoubleIntTwo);
        doubleIntArrayComparison.addDifference(true, Importance.HIGH, "[2] : Array of java.lang.Integer",
            "Left Array was not long enough", ((ContainersObject) o2).doubleIntArray[2].toString());
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [0] : doubleIntArray", doubleIntArrayComparison);

        ListMapComparison doubleListComparison = new ListMapComparison(3, 4);
        doubleListComparison.addDifference(true, Importance.HIGH, "[0] : java.lang.Double", "2.0", "1.0");
        doubleListComparison.addDifference(true, Importance.HIGH, "[1] : java.lang.Double", "2.2", "1.1");
        doubleListComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.Double", "1.11", "1.11");
        doubleListComparison.addDifference(true, Importance.HIGH,
            "[3] (Extra Right) : java.lang.Double", "Left List was not long enough", "9.99");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [1] : doubleList", doubleListComparison);

        ListMapComparison integerSetComparison = new ListMapComparison(4, 5);
        integerSetComparison.addDifference(false, Importance.HIGH, "[0] : java.lang.Integer", "1", "1");
        integerSetComparison.addDifference(false, Importance.HIGH, "[1] : java.lang.Integer", "3", "3");
        integerSetComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer", "7", "7");
        integerSetComparison.addDifference(false, Importance.HIGH, "[3] : java.lang.Integer", "9", "9");
        integerSetComparison.addDifference(true, Importance.HIGH,
            "[4] (Extra Right) : java.lang.Integer", "Left Set did not provide a match", "2");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [2] : integerSet", integerSetComparison);

        ListMapComparison stringArrayComparison = new ListMapComparison(4, 5);
        stringArrayComparison.addDifference(false, Importance.HIGH, "[0] : java.lang.String", "This", "This");
        stringArrayComparison.addDifference(false, Importance.HIGH, "[1] : java.lang.String", "is", "is");
        stringArrayComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.String", "TestObject", "TestObject");
        stringArrayComparison.addDifference(true, Importance.HIGH, "[3] : java.lang.String", "Two", "One");
        stringArrayComparison.addDifference(true, Importance.HIGH,
            "[4] : java.lang.String", "Left Array was not long enough", "Extra Word");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [3] : stringArray", stringArrayComparison);

        ListMapComparison stringIntegerMapComparison = new ListMapComparison(3, 4);
        stringIntegerMapComparison.addDifference(true, Importance.HIGH, "[0] : java.lang.Integer (Key - key 3) :", "5", "3");
        stringIntegerMapComparison.addDifference(true, Importance.HIGH, "[1] : java.lang.Integer (Key - key 2) :", "3", "2");
        stringIntegerMapComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer (Key - key 1) :", "1", "1");
        stringIntegerMapComparison.addDifference(true, Importance.HIGH,
            "[3] (Extra Right) : java.lang.Integer (Key - key 9) :", "Left Map did not provide a match", "9");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [4] : stringIntegerMap", stringIntegerMapComparison);

        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testContainersMatching() {
        o1 = new ContainersObject(false);
        o2 = new ContainersObject(false);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();

        ListMapComparison subDoubleIntOne = new ListMapComparison(3, 3);
        subDoubleIntOne.addDifference(false, Importance.HIGH, "[0] : java.lang.Integer", "1", "1");
        subDoubleIntOne.addDifference(false, Importance.HIGH, "[1] : java.lang.Integer", "2", "2");
        subDoubleIntOne.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer", "3", "3");
        ListMapComparison subDoubleIntTwo = new ListMapComparison(3, 3);
        subDoubleIntTwo.addDifference(false, Importance.HIGH, "[0] : java.lang.Integer", "7", "7");
        subDoubleIntTwo.addDifference(false, Importance.HIGH, "[1] : java.lang.Integer", "8", "8");
        subDoubleIntTwo.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer", "9", "9");

        ListMapComparison doubleIntArrayComparison = new ListMapComparison(2, 2);
        doubleIntArrayComparison.addDifference(Importance.HIGH, "[0] : Array of java.lang.Integer", subDoubleIntOne);
        doubleIntArrayComparison.addDifference(Importance.HIGH, "[1] : Array of java.lang.Integer", subDoubleIntTwo);
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [0] : doubleIntArray", doubleIntArrayComparison);

        ListMapComparison doubleListComparison = new ListMapComparison(3, 3);
        doubleListComparison.addDifference(false, Importance.HIGH, "[0] : java.lang.Double", "2.0", "2.0");
        doubleListComparison.addDifference(false, Importance.HIGH, "[1] : java.lang.Double", "2.2", "2.2");
        doubleListComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.Double", "1.11", "1.11");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [1] : doubleList", doubleListComparison);

        ListMapComparison integerSetComparison = new ListMapComparison(4, 4);
        integerSetComparison.addDifference(false, Importance.HIGH, "[0] : java.lang.Integer", "1", "1");
        integerSetComparison.addDifference(false, Importance.HIGH, "[1] : java.lang.Integer", "3", "3");
        integerSetComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer", "7", "7");
        integerSetComparison.addDifference(false, Importance.HIGH, "[3] : java.lang.Integer", "9", "9");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [2] : integerSet", integerSetComparison);

        ListMapComparison stringArrayComparison = new ListMapComparison(4, 4);
        stringArrayComparison.addDifference(false, Importance.HIGH, "[0] : java.lang.String", "This", "This");
        stringArrayComparison.addDifference(false, Importance.HIGH, "[1] : java.lang.String", "is", "is");
        stringArrayComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.String", "TestObject", "TestObject");
        stringArrayComparison.addDifference(false, Importance.HIGH, "[3] : java.lang.String", "Two", "Two");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [3] : stringArray", stringArrayComparison);

        ListMapComparison stringIntegerMapComparison = new ListMapComparison(3, 3);
        stringIntegerMapComparison.addDifference(false, Importance.HIGH, "[0] : java.lang.Integer (Key - key 3) :", "5", "5");
        stringIntegerMapComparison.addDifference(false, Importance.HIGH, "[1] : java.lang.Integer (Key - key 2) :", "3", "3");
        stringIntegerMapComparison.addDifference(false, Importance.HIGH, "[2] : java.lang.Integer (Key - key 1) :", "1", "1");
        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [4] : stringIntegerMap", stringIntegerMapComparison);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNotMatchingTypesWithAncestor() {
        o1 = new Child1();
        o2 = new Child2();

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [0] : number", "4", "4");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Type", "Types do not match", o1.getClass().getName(), o2.getClass().getName());

        assertEquals(expectedResult, actualResult);

        actualResult = new GenericObjectDiffer(params).diff(o2, o1);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [0] : number", "4", "4");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Type", "Types do not match", o2.getClass().getName(), o1.getClass().getName());

        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testNotMatchingTypesNoAncestor() {
        o1 = new Child1();
        o2 = new Integer(3);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Type", "Types do not match", o1.getClass().getName(), o2.getClass().getName());

        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testChildParentTypes() {
        o1 = new Child1();
        o2 = new Parent();

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(false, Importance.HIGH, "Field [0] : number", "4", "4");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Type", "Types do not match", o1.getClass().getName(), o2.getClass().getName());
        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testMismatchingTypesofInnerClass() {
        o1 = new ParentContainer(new Child1());
        o2 = new ParentContainer(new Child2());

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        ObjectComparison subComp = new ObjectComparison();
        subComp.addDifference(false, Importance.HIGH, "Field [0] : number", "4", "4");
        subComp.addDifference(true, Importance.HIGH, "Type", "Types do not match", Child1.class.getName(), Child2.class.getName());

        expectedResult.getComparison().addDifference(Importance.HIGH, "Field [0] : field", subComp);
        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testSeprateRecursiveField() {
        Recursable innerOne = new Recursable(12, null);
        o1 = new Recursable(11, innerOne);
        innerOne.setRecursiveField((Recursable) o1);

        Recursable innerTwo = new Recursable(22, null);
        o2 = new Recursable(21, innerTwo);
        innerTwo.setRecursiveField((Recursable) o2);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [0] : intField", "11", "21");
        ObjectComparison c = new ObjectComparison();
        c.addDifference(true,  "Field [0] : intField", "12", "22");
        c.addDifference(true,  "Field [1] : recursiveField", "Cycle detected - comparison curtailed");
        expectedResult.getComparison().addDifference("Field [1] : recursiveField", c);

        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testConjoinedRecursiveField() {
        o1 = new Recursable(11, null);
        o2 = new Recursable(21, (Recursable) o1);
        ((Recursable) o1).setRecursiveField((Recursable) o2);

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [0] : intField", "11", "21");
        ObjectComparison c = new ObjectComparison();
        c.addDifference(true,  "Field [0] : intField", "21", "11");
        c.addDifference(true,  "Field [1] : recursiveField", "Cycle detected - comparison curtailed");
        expectedResult.getComparison().addDifference("Field [1] : recursiveField", c);

        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testDuplicateField() {
        o1 = new DuplicateHolder(new Child1()); // DuplicateHolder sets 2 fields, f1 and f2 to be the inputted object
        o2 = new DuplicateHolder(new Child1());

        params = new GenericObjectDifferParams();
        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        ObjectComparison c1 = new ObjectComparison();
        c1.addDifference(false,  "Field [0] : number", "7", "7");
        c1.addDifference(false,  "Field [1] : number1", "1", "1");
        c1.addDifference(false,  "Field [2] : number", "4", "4");
        expectedResult.getComparison().addDifference("Field [0] : f1", c1);
        expectedResult.getComparison().addDifference("Field [1] : f2", c1);

        assertEquals(expectedResult, actualResult);
        assertEquals(
            System.identityHashCode(actualResult.getComparison().getComparisons().get(0).getValue3()),
            System.identityHashCode(actualResult.getComparison().getComparisons().get(1).getValue3())
        );
    }

    @Test
    public void testBlackList() {
        o1 = new PrimitivesObject(true);
        o2 = new PrimitivesObject(false);

        params = new GenericObjectDifferParams()
            .setUseFunctionBlacklist(true)
            .addFieldToBlacklist("byteField")
            .addFieldToBlacklist("shortField")
            .addFieldToBlacklist("intField")
            .addFieldToBlacklist("longField")
            .addFieldToBlacklist("floatField")
            .addFieldToBlacklist("doubleField")
            .addFieldToBlacklist("charField")
            .addFieldToBlacklist("stringField")
        ;

        params.removeFieldFromBlacklist("intField");

        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [0] : boolField", "true", "false");
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [1] : intField", "111", "222");

        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void testWhiteList() {
        o1 = new PrimitivesObject(true);
        o2 = new PrimitivesObject(false);

        params = new GenericObjectDifferParams()
            .setUseFunctionWhitelist(true)
            .addFieldToWhitelist("byteField")
            .addFieldToWhitelist("stringField")
        ;

        params.removeFieldFromWhitelist("byteField");

        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [0] : stringField", "One", "Two");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSkipStaticFields() {
        o1 = new StaticHolder(1, 2);
        o2 = new StaticHolder(3, 4);
        params = new GenericObjectDifferParams();

        actualResult = new GenericObjectDiffer(params).diff(o1, o2);

        expectedResult = new DiffResult();
        expectedResult.getComparison().addDifference(true, Importance.HIGH, "Field [0] : ns2", "2", "4");


        assertEquals(expectedResult, actualResult);
    }
}
