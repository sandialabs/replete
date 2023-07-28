package learning.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import replete.diff.DiffTreePanel;
import replete.diff.generic.GenericObjectDiffer;
import replete.text.StringLib;
import replete.text.StringUtil;
import replete.ui.lay.Lay;
import replete.util.ReflectionUtil;

public class ReflectionRecursionExample {

    public static void main(String[] args) {
        SteeringWheel sw = new SteeringWheel(42.42F);

        Car bmw = new Car("BMW", "XSeries", 1997, sw);
        Car ford = new Car("Ford", "F-150", 2001, null);

        Person dave = new Person(1000, "Dave", 27, 32.33F, bmw, true, true);
        Person barb = new Person(2000, "Barbara", 34, 32.33F, ford, false, false);

        Car[] cars = {bmw, ford};
//        printData(cars);

//        Class<? extends Person> p = dave.getClass();

//        ReflectionDiffer differ = new ReflectionDiffer(params);
//        DiffResult result = differ.diff(dave, barb);
//        DiffResult result2 = differ.diff(bmw, ford);

//        printFields(dave);
//        printFields(barb);

//        printData(dave);
//        printData(barb);

//        diff(dave, barb).getDifference().render();

        DiffTreePanel diffPanel = new DiffTreePanel();
        diffPanel.setCurrentResult(new GenericObjectDiffer(null).diff(dave, barb), "People");
        Lay.BLtg(Lay.fr(),
            "C", diffPanel,
            "size=800,center,visible"
        );
    }



    private static String getArrayContext(Object array1, int i) {
        return "[" + i + "] : " + Array.get(array1, i).getClass().getName();
    }

    private static boolean isMap(Object obj) {
        return obj instanceof Map;
    }

    private static boolean isCollection(Object obj) {
        return obj instanceof Collection;
    }


    private static void printData(Object obj) {
        printData(obj, 0);
    }
    private static void printData(Object obj, int level) {
        int numSpaces = level * 4;
        String indent = StringUtil.spaces(numSpaces);
        int numSpaces2 = (level + 1) * 4;
        String spaces2 = StringUtil.spaces(numSpaces2);


        if(obj == null) {
            System.out.println(StringLib.NULL);

        } else if(isPrimitiveValue(obj)) {
            System.out.println(obj);

        } else if(isArray(obj)) {
            for(int i = 0; i < Array.getLength(obj); i++) {
                System.out.print(indent + "[" + i + "] =");
                Object elem = Array.get(obj, i);
                printLineCleanup(elem);
                printData(elem, level + 1);
            }
        } else if(isIterable(obj)) {
            int i = 0;
            for(Object elem : (Iterable) obj) { // similar to above
                System.out.print(indent + "[" + i + "] =");
                printLineCleanup(elem);
                printData(elem, level + 1);
                i++;
            }
        } else {
            Class<?> clazz = obj.getClass();

            System.out.print(indent);
            System.out.println("Class: " + clazz.getName() + " / Object: " + obj.hashCode() + " [" + obj + "]");
            System.out.print(indent);
            System.out.println("Superclass: " + clazz.getSuperclass().getName());

            try {
                Field[] fields = ReflectionUtil.getFields(obj);


                int f = 0;
                for(Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    System.out.print(indent);
                    System.out.print("Field " + f + ": (" + field.getModifiers() + ") " + field.getType().getName() + " " + field.getName() + " =");
                    printLineCleanup(value);

                    printData(value, level + 1);
                    f++;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void printLineCleanup(Object value) {
        if(value == null || isPrimitiveValue(value)) {
            System.out.print(" ");
        } else {
            System.out.println();
        }
    }

    // Method are like Fields: names, modifiers, return types, typed argument list
    // Has invoke instead of get m.invoke(obj, a, b, c, e);

    private static void printFields(Object obj) {
        printFields(obj, 0);
    }
    private static void printFields(Object obj, int level) {
        Class<?> clazz = obj.getClass();

        int numSpaces = level * 4;
        String spaces = StringUtil.spaces(numSpaces);

        System.out.print(spaces);
        System.out.println("Class: " + clazz.getName() + " / Object: " + obj.hashCode() + " [" + obj + "]");
        System.out.print(spaces);
        System.out.println("Superclass: " + clazz.getSuperclass().getName());

//        List<Field> allFields = new ArrayList<>();
//        Class<?> curClass = clazz;
//        while(!curClass.equals(Object.class)) {
//            Field[] fields = curClass.getDeclaredFields(); // Only the fields declared in A class
//            allFields.add(fields);
//            curClass = curClass.getSuperclass();
//        }
//        ^^Good code, this is why we use ReflectionUtil so we don't have to type it out all the time

        try {
            Field[] fields = ReflectionUtil.getFields(obj);
            // ^All fields from all ancestor classes included, but order of fields is unpredictable

            int f = 0;
            for(Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);

                System.out.print(spaces);
                System.out.print("Field " + f + ": (" + field.getModifiers() + ") " + field.getType().getName() + " " + field.getName() + " =");

                if(value == null) {
                    System.out.println(" " + StringLib.NULL);

                } else if(isPrimitiveValue(value)) {
                    System.out.println(" " + value);

                } else {
                    System.out.println();
                    printFields(value, level + 1);
                }

                // Not going to worry about arrays & collections since this is a pure "printFields" method

                f++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

//        try {
//            System.out.println(clazz.getName());
//            Field f = clazz.getDeclaredField("age");
//            f.setAccessible(true);
//            Object value = f.get(obj);
//            System.out.println("(" + f.getModifiers() + ") " + f.getType().getName() + " " + f.getName() + " = " + value + ";");
//        } catch(NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch(SecurityException e) {
//            e.printStackTrace();
//        } catch(IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch(IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

    private static boolean isPrimitiveValue(Object value) {
        return
            value instanceof Boolean   ||
            value instanceof Byte      ||
            value instanceof Short     ||
            value instanceof Integer   ||
            value instanceof Long      ||
            value instanceof Float     ||   // 4 bytes
            value instanceof Double    ||   // 8 bytes = 16 bytes????
            value instanceof Character ||
            value instanceof String
        ;
    }

    private static boolean isArray(Object value) {
        return value.getClass().isArray();
    }

    private static boolean isIterable(Object value) {
        return value instanceof Iterable;
    }
}
