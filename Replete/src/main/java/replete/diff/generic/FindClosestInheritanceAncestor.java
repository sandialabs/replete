package replete.diff.generic;

public class FindClosestInheritanceAncestor {
    public static Class<?> findClosestAncestor(Object o1, Object o2) {
        Class c1 = o1.getClass();
        while(c1 != Object.class && c1 != o2.getClass()) {
            Class c2 = o2.getClass();
            while(c2 != Object.class && c1 != c2) {
                c2 = c2.getSuperclass();
            }

            if(c1 == c2) {
                return c1;
            }

            c1 = c1.getSuperclass();
        }
        return c1;
    }

    public static void main(String[] args) {
        Object c1 = new Double(3.14);
        Object c2 = new Integer(3);

        System.out.println(c1.getClass() + " - " + c2.getClass());
        System.out.println(findClosestAncestor(c1, c2).getName());
    }
}

