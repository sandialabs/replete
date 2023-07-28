package learning.strings;

import replete.text.StringPool;

public class StringTest {

//    public class ARRAY {
//        public final int length;
//        public ARRAY(int length) {
//            this.length = length;
//        }
//    }

    // Other Topics: Concatenation Operator, StringBuilder, Larger Software System Organization

    public static void main(String[] args) {

        // Primitives: boolean/2 byte/1 short/2 int/4 long/8 float/4 double/8 char/2
        // Not a primitive: String, Object

        // char* str = "abc";    // abc\0

        // Java Array = { elements, int length }

        // int[] numbers = new int[10];

        // Array: numbers.length, Collection: numbers.size()    // NOT A THING => numbers.size   numbers.length()

        int x = 1;
        int y = 2;
        System.out.println(x == y);        // false

        //Object* o = new Object();  // Pointer
        Object o1 = new Object();   // Reference
        Object o2 = new Object();

        // o1 as 0x0F2C (340) and o2 as 0x0F34  (356)

        // Using == with two object variables provides identity checking with object
        // "Identically" equal objects have the same memory address
        // Comparing references/memory addresses

        System.out.println(o1 == o2);      // false

        //Person joe = "joe"/54/3.4;
        String c = new String("aaa");
        String a = c; //"aaa";
        String b = c; //"aaa";

        System.out.println(a == b);        // true

        // Java has concept of string pooling
        // Compiler replaces all instances of same string literal (e.g. "aaa") with same string object instance

        // Strings are immutable
        // Any object that is immutable means that it cannot be changed after its construction
        // String pooling goes hand-in-hand with String immutability

        String e = "abc";
        String f = "abc";

        System.out.println(e == f);        // true

//        e = "aZc";     // Doesn't change f

//        e[1] = 'Z';           // Not possible
//        e.setChar(1, 'Z');    // Not possible

        String part = "bc";

        String s1 = e.substring(1);     // returns "bc", each of these invokes new String(...), causing new memory allocation
        String s2 = f.substring(1);     // returns "bc", each of these invokes new String(...), causing new memory allocation

        System.out.println(part == s1);    // false
        System.out.println(s1 == s2);      // false // The contents of their 'char[] value' arrays are identical!
        System.out.println(s1 == "bc");    // false
        System.out.println(part == "bc");  // true
        System.out.println(s1.hashCode() == s2.hashCode());   // true
        System.out.println(s1.equals(s2)); // true; Checks the CONTENT of strings' char[] arrays

        StringPool myPool = new StringPool();
        s1 = myPool.resolve(s1);
        s2 = myPool.resolve(s2);

        System.out.println(s1 == s2);      // true

        // !!!!!!!!!!!! Lesson/Moral: Java Developers NEVER use == with strings, ALWAYS use .equals()

//        foo();
    }

//    private static void foo() {
//        int x = 0;   // 4 B
//        boolean b;   // 2 B  => 6 B
//
//        bar();
//    }
//
//    private static void bar() {
//        float g;   // 4 B
//        double d;  // 8 B
//        Person p;  // 4 B always  == 16 BYTES  all references are basically memory addresses
//
//        p = new Person();  // new Person() is in heap and could be like 1450 B, full fat object, return 0x0FA3 to the pointer on my stack
//
//        System.out.println(p); // go to 0x0FA3 and print the thing at that location
//
//        // Java: ALL object instances will live in heap, so all our "object" variables will be pointers but we hate that word, so references
//    }

    // Multiple return values:
//    public Object[] getMany() {
//        return new Object[] {a, b};                // Object[] result = getMany(); result[0], result[1]
//    }
//    public Composite getMany() {
//        return new Composite(a, b);                // Composite c = getMany();  c.getA(), c.getB()
//    }
//    public Pair<String, Integer> getMany() {
//        new Pair<>("aaa", 42);                     // Pair<String, Integer> result = getMany(); result.getValue1(), result.getValue2()
//    }
//    public (String, Integer) getMany() {
//        return ("aaa", 42);                        // (String a, int x) = getMany();  a, x
//    }

}
