package learning;

import java.util.ArrayList;
import java.util.List;

public class RandomStuff {

    public static void main(String[] args) {

        List<Person> list = new ArrayList<Person>();
//        List<? extends Person> list = new ArrayList<? extends Person>();
        MyContainer<Person> p = new MyContainer<>();

        MyContainer<Student> s2 = new MyContainer<>();

        Object o = new Object();
        Student s = new Student();

//        list.add(o);  // Error
//        list.add(s);

        List<Person> list2 = new ArrayList<>();

//        var v = someFunc();                  // Java 9+ feature (not exactly sure when)
//        var v2 = new ArrayList<Person>();

        processList(list);

//        Method m = x -> {
//            return 42 + x;
//        };
//        int x = m(5);
    }

    public static int someFunc() {
        return 42;
    }

    public static void processList(List<Person> list) {

        for(int i = 0; i < list.size(); i++) {
            Person p = list.get(i);
        }
        for(Object o : list) {
        }
    }

    private static class Person {

    }

    private static class Student extends Person {

    }

    private static class MyContainer<T extends Person> {
        T obj;
    }
}
