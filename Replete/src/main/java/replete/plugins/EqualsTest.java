package replete.plugins;

public class EqualsTest {

    public static void main(String[] args) {
        Object o1 = new Object();
        Object o2 = new Object();

        System.out.println(o1.equals(o2));

        Person p1 = new Person();
        Person p2 = new Person();

        System.out.println(p1.equals(p2));

        PersonEQUALS x1 = new PersonEQUALS();
        PersonEQUALS x2 = new PersonEQUALS();

        System.out.println(x1.equals(x2));

        Cop c1 = new Cop();
        Cop c2 = new Cop();

        System.out.println(c1.equals(c2));
    }
}

class Person {}
class PersonEQUALS extends SerializableEmptyEqualsObject {}
class Cop extends PersonEQUALS {
    int x;
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + x;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        Cop other = (Cop) obj;
        if(x != other.x) {
            return false;
        }
        return true;
    }
}

