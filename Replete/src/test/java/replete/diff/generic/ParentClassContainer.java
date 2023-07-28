package replete.diff.generic;

public class ParentClassContainer {
    public static class Parent {
        public int number = 4;
    }
    public static class Child1 extends Parent {
        public int number = 7;
        public int number1 = 1;
    }
    public static class Child2 extends Parent {
        public int number2 = 2;
    }
    public static class ParentContainer {
        public Parent field;

        public ParentContainer(Parent field) {
            this.field = field;
        }
    }

    public static class DuplicateHolder {
        public Object f1;
        public Object f2;

        public DuplicateHolder(Object o) {
            f1 = o;
            f2 = o;
        }
    }

    public static class Recursable {
        int intField;
        Recursable recursiveField;

        public Recursable setRecursiveField(Recursable recursiveField) {
            this.recursiveField = recursiveField;
            return this;
        }

        public Recursable(int intField, Recursable recursiveField) {
            this.intField = intField;
            this.recursiveField = recursiveField;
        }
    }

    public static class StaticHolder {
        public static int s1;
        public int ns2;

        public StaticHolder(int s1, int ns2) {
            this.s1 = s1;
            this.ns2 = ns2;
        }
    }

}
