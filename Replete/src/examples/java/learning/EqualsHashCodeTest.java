package learning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EqualsHashCodeTest {
    public static void main(String[] args) {
//        Person p = new Person("1", 1, 1.0F, 1.0);

//        Shape[] shapes = new Shape[] {
//            new Triangle("Bermuda"),
//            new Rectangle("Farm")
//        };
//        for(Shape someShapeOfSomeSubType : shapes) {
//            someShapeOfSomeSubType.paint();
//        }

        // Primitive:
        // boolean, char, short, int, long, float, double
//
//        String s1 = "Mars";
//        String s2 = new String("Mars");
//        if(s1 == s2) {
//            System.out.println("equals!");
//        } else {
//            System.out.println("not equals :(");
//        }
//
//        int a = 3;
//        int b = 4;
//
//        if(a == b) {
//
//        }

        OldPerson p = new OldPerson("Marco");
        OldPerson p2 = new OldPerson("Polo");
        System.out.println(p == p2);         // Returns false
        System.out.println(p.equals(p2));    // p == p2 : returns false

        List<OldPerson> people = new ArrayList<>();
        people.add(p);
        System.out.println(people.contains(p));
        System.out.println(people.contains(p2));
        people.add(p2);
        System.out.println(people.size());

        Set<OldPerson> set = new HashSet<>();
        set.add(p);
        System.out.println(set.contains(p));
        System.out.println(set.contains(p2));
        set.add(p2);
        System.out.println(set.size());

        System.out.println(p.equals(p2));
        System.out.println(p.hashCode() + " ? " + p2.hashCode());
    }
}

// Terminology Pairs:
//   Parent Class / Child Class
//   Subclass / Superclass
//   Base Class / Derived Class

abstract class Shape {
    protected String label;

    protected Shape(String label) {
        this.label = label;
    }

    // Accessors / Mutators
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    abstract void paint();  // Forces class to be abstract
}

class Triangle extends Shape {
    public Triangle(String label) {
        super(label);
    }
    @Override
    void paint() {
        System.out.println("Triangle Painting! " + label);
    }
}

class Rectangle extends Shape {
    public Rectangle(String label) {
        super(label);
    }
    @Override
    void paint() {
        System.out.println("Rectangle Painting! " + label);
    }
}

class OldPerson {
    private String name;
    public OldPerson(String name) {
        this.name = name;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        OldPerson other = (OldPerson) obj;
        if(name == null) {
            if(other.name != null) {
                return false;
            }
        } else if(!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
