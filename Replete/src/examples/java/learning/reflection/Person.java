package learning.reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Person extends Lifeform {
    private String name;
    private int age;
    private float height;
    private Car car;
    private List<String> addresses = new ArrayList<>(Arrays.asList("123 Maple St.", "1000 Birch Rd.", "555 Elm Dr."));
    private long[] values = {6, 7, 8, 9};
    private Dog[] dogs = {new Dog("fluffy", "Small"), new Dog("Stinky", "Chiwawa")};
    private Map<String, Integer> testMap;
    private Iterable<Integer> testIterable;

    public Person(int soulsparks, String name, int age, float height, Car car, boolean mapType, boolean iterable) {
        super(soulsparks);
        this.name = name;
        this.age = age;
        this.height = height;
        this.car = car;
        testMap = new HashMap<String, Integer>();
        if(mapType) {
            testMap.put("key 1", 1);
            testMap.put("key 2", 2);
            testMap.put("key 3", 3);
        } else {
            testMap.put("key 1", 1);
            testMap.put("key 2", 2222);
            testMap.put("key4", 4);
        }
        ArrayList<Integer> integers = new ArrayList();
        if(iterable) {
            integers.add(10);
            integers.add(20);
            integers.add(30);
        } else {
            integers.add(10);
            integers.add(99);
            integers.add(40);
        }
        testIterable = integers;
    }

    @Override
    public String toString() {
        return name + "!!!!!";
    }


}
