package learning.generics;

import java.util.ArrayList;
import java.util.List;

public class GenericsMain extends ArrayList<Baker> {
//public class GenericsMain extends ArrayList<Person> {
//public class GenericsMain extends ArrayList<? extends Person> {
//public class GenericsMain extends ArrayList<P extends Person> {
    // Knows to apply Person rules to the class

    // ? extends Person - compiler would be torn between,
    // well do I apply Baker rules to the class or PoliceOffer rules
    // to the class. Does the class contain Person objects that have a
    // bake() method or does it contain Person objects that contain
    // shoot() method.

//public class GenericsMain<P extends Person> { // THIS CLASS IS TEMPLATIZED/GENERICIZED

    private static final int CONSTANT = 123;

    public static void main(String[] args) {

        //CONSTANT = 11234;  // Error

        // <? extends Person> - construct for pattern matching

        // 3rdYearSeargant -> Seargant -> PoliceOfficer -> Person

        GenericsMain gm;
        //Object o = gm.get(4);
        //method(gm.get(4).bake());   // Should this *compile* ?

        //CALL_METHOD_42(ARG0);

        Person p = new Person();
        Baker b = new Baker();
        Officer o = new Officer();

        //List<Sword> swords = new ArrayList<Sword>();
        //                                   ^^^^^ Required
        //List<Sword> swords = new ArrayList<>();
        //                                   ^ Newer versions of Java this is no longer required
        //var swords = new ArrayList<Sword>();
        // For var, you can't use <> but must now move <Sword> back to the right

        List l0 = new ArrayList();
        // WARNINGS:
        //   Multiple markers at this line
        //     - ArrayList is a raw type. References to generic type ArrayList<E> should be
        //       parameterized
        //     - List is a raw type. References to generic type List<E> should be parameterized

        List<Person> l1 = new ArrayList<>();
        List<? extends Person> l2 = new ArrayList</*don't understand intersection of ? extends BLAH and <>*/>();  // What the hell is this doing? Weird...
        List<? extends Person> l3;

        //new ArrayList<? extends Person>();  // "I don't even know what I am going to put in here yet" (Error: Cannot instantiate the type ArrayList<? extends Person>)
        new ArrayList<Person>();   // Stores Person, Baker, Officer

        l3 = new ArrayList<Person>();
        l3 = new ArrayList<Baker>();
        l3 = new ArrayList<Officer>();

        List<Baker> blist = new ArrayList<>();
        List<Officer> olist = new ArrayList<>();

        // p2 is declared as either List<Person> OR List<Baker> OR List<Officer>

        l1.add(p);
        l1.add(b);
        l1.add(o);

//        l2.add(p);  // This is an error because the compiler doesn't know what l2
//        l2.add(b);  // could be pointing to at this stage during execution.
//        l2.add(o);  // Is this a list of Bakers or Officers?  Can't even add a Person in case it's a list of Bakers
//
//        l3.add(p);  // Same error
//        l3.add(b);
//        l3.add(o);

        ((List) l3).add(p);   // "Screw the generics system" cast - "let me go back to pre-generics land"
//        l3.add(b);
//        l3.add(o);

        // The method add(capture#1-of ? extends Person) in the type
        // List<capture#1-of ? extends Person> is not applicable for the arguments (Baker)
        // p2.add(b);

        // The method add(capture#2-of ? extends Person) in the type
        // List<capture#2-of ? extends Person> is not applicable for the arguments (Officer)
        // p2.add(o);

        aMethodWithGenericParams(l1);
//        aMethodWithGenericParams(l2);       // Error
//        aMethodWithGenericParams(blist);    // Error
//        aMethodWithGenericParams(olist);    // Error

        aMethodWithGenericQuestionExtends(l1);
        aMethodWithGenericQuestionExtends(l2);   // Error

        aMethodWithGenericQuestionExtends(blist);
        aMethodWithGenericQuestionExtends(olist);

        for(Person x1 : l1) {

        }
        for(Person x2 : l2) {

        }

        //List<Person> and List<? extends Person> are not the same thing

        // "Minecraft" (Literal) and "Minec.*" (Pattern)

    }

    public static void method(Baker b) {}
    public static void method(Officer b) {}
    public static void method(Person p) {}

    public static void aMethodWithGenericParams(List<Person> list) {

        System.out.println(list.size());

        Person p = list.get(0);

        //p.speak();

    }
    public static void aMethodWithGenericQuestionExtends(List<? extends Person> list) {

        System.out.println(list.size());

        Person p = list.get(0);

        //p.speak();

    }
//    public static void aMethodWithGenericParams(P p) {
//
//    }

    public void typeInference() {


        int x = 5;
        //int y = 4.5;      // 4.5 is a literal numeric constant of type double

        //var x = 5;
        //var y = 4.5;

        //x.speak();  // because x is not a Person; x has been determined to be an int

//        var p = new Person();
//
//
//        p.pop open intellisense here;
//
//
//        someLooselyTypedParams(new Integer());
//        someLooselyTypedParams(new Frog());

    }

//    public void someLooselyTypedParams(var y) {
//        y.doSomething();
//    }
}
