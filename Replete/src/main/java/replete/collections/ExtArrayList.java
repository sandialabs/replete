package replete.collections;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import replete.util.User;
import replete.xstream.XStreamWrapper;

/**
 * This class allows for easy handling of various parts
 * of an object hierarchy to contain objects that are
 * extensions (objects that implement the ExtensionPoint
 * interface).
 */

public class ExtArrayList<T> extends ArrayList<T> {


    ////////////
    // FIELDS //
    ////////////

    // This is just the bare minimum of logic that could be
    // placed into this class to allow the class to help
    // decide what "kinds" of objects are allowed into the
    // list.  This boolean, if true, means that no two elements
    // in the list can be of the same type.  The class could
    // theoretically also have an int[] {min, max} instead
    // indicating the minimum # of elements of each class and
    // the maximum # of elements of each class.  However, the
    // former presents a design challenge as how would this
    // class know how to enforce the minimums when it always
    // starts out empty?  Additionally it would present usage
    // problems restricting how developers add and remove
    // items from it.  For now, we'll stick with the single
    // boolean that will at least instances where a developer
    // has obviously used the list in an incorrect way.  Most
    // usages of this class are simply either:
    //   1) There's a single instance of each type or
    //   2) There is no limit on the types or quantities,
    // so this field should suffice.

    private boolean requireUniqueClasses;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExtArrayList() {
        super();
    }
    public ExtArrayList(boolean requireUniqueClasses) {
        super();
        this.requireUniqueClasses = requireUniqueClasses;
    }
    public ExtArrayList(ExtArrayList<?> other) {
        super((ExtArrayList) other);
        this.requireUniqueClasses = other.isRequireUniqueClasses();
    }
    public ExtArrayList(ExtArrayList<?> other, boolean requireUniqueClasses) {
        super((ExtArrayList) other);
        this.requireUniqueClasses = requireUniqueClasses;
    }
    public ExtArrayList(Collection<? extends T> c) {
        super(c);
    }
    public ExtArrayList(Collection<? extends T> c, boolean requireUniqueClasses) {
        super(c);
        this.requireUniqueClasses = requireUniqueClasses;
    }
    public ExtArrayList(int initialCapacity) {
        super(initialCapacity);
    }
    public ExtArrayList(int initialCapacity, boolean requireUniqueClasses) {
        super(initialCapacity);
        this.requireUniqueClasses = requireUniqueClasses;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isRequireUniqueClasses() {
        return requireUniqueClasses;
    }

    // Accessors (Computed)

    // Returns first element whose type is or is inherited from provided type.
    public <E extends T> E getElementByType(Class<?> clazz) {  // Removed <? extends T> so any class can be not found (like equals(Object o))
        for(T elem : this) {
            if(clazz.isAssignableFrom(elem.getClass())) {
                return (E) elem;
            }
        }
        return null;
    }

    // Returns all elements whose type is or is inherited from provided type.
    public List<T> getElementsByType(Class<?> clazz) {
        List<T> found = new ArrayList<>();
        for(T elem : this) {
            if(clazz.isAssignableFrom(elem.getClass())) {
                found.add(elem);
            }
        }
        return found;
    }

    public boolean containsElementByType(Class<?> clazz) {
        return getElementByType(clazz) != null;
    }
    public int indexOfElementsByType(Class<?> clazz) {
        return indexOfElementsByType(clazz.getName());
    }
    public int indexOfElementsByType(String className) {
        int i = 0;
        for(T elem : this) {
            if(className.equals(elem.getClass().getName())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    // Mutators

    public ExtArrayList setRequireUniqueClasses(boolean requireUniqueClasses) {
        this.requireUniqueClasses = requireUniqueClasses;
        return this;
    }

    @Override
    public boolean add(T elem) {
        if(requireUniqueClasses) {
            for(T e : this) {
                if(e.getClass().equals(elem.getClass())) {
                    throw new IllegalArgumentException(
                        "an element of type '" + elem.getClass() +
                        "' already exists in this list");
                }
            }
        }
        return super.add(elem);
    }
    @Override
    public T set(int index, T elem) {
        if(requireUniqueClasses) {
            int i = 0;
            for(T e : this) {
                if(i != index) {
                    if(e.getClass().equals(elem.getClass())) {
                        throw new IllegalArgumentException(
                            "an element of type '" + elem.getClass() +
                            "' already exists in this list");
                    }
                }
                i++;
            }
        }
        return super.set(index, elem);
    }
    public void addReplaceByType(T elem2, Class<?> clazz) {
        int found = -1;
        for(int i = size() - 1; i >= 0; i--) {
            T elem = get(i);
            if(clazz.isAssignableFrom(elem.getClass())) {
                found = i;
                break;
            }
        }
        if(found == -1) {
            add(elem2);
        } else {
            set(found, elem2);
        }
    }

    public void removeAll(Class<?> clazz) {
        for(int i = size() - 1; i >= 0; i--) {
            T elem = get(i);
            if(clazz.isAssignableFrom(elem.getClass())) {
                remove(i);
                break;
            }
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "{ExtArrayList}" + super.toString();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws IOException {
        ExtArrayList<Person> list = new ExtArrayList<>();
        list.add(new Person("joe"));
        list.add(new Person("mark"));
        Outer outer = new Outer();
        outer.invitees = list;
        File file = User.getDesktop("list.xml");
        XStreamWrapper.registerConverter(new ExtArrayListXStreamConverter());
        XStreamWrapper.writeToFile(outer, file);
        Outer outer2 = XStreamWrapper.loadTarget(file);
        System.out.println(outer2.equals(outer));
    }

    private static class Outer {
        ExtArrayList<Person> invitees = new ExtArrayList<>();
        float price = 0.99F;
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((invitees == null) ? 0 : invitees.hashCode());
            result = prime * result + Float.floatToIntBits(price);
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
            Outer other = (Outer) obj;
            if(invitees == null) {
                if(other.invitees != null) {
                    return false;
                }
            } else if(!invitees.equals(other.invitees)) {
                return false;
            }
            if(Float.floatToIntBits(price) != Float.floatToIntBits(other.price)) {
                return false;
            }
            return true;
        }
    }

    private static class Person {
        String name;
        int age;
        Person(String n) {
            name = n;
            age = 77;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + age;
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
            Person other = (Person) obj;
            if(age != other.age) {
                return false;
            }
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
}
