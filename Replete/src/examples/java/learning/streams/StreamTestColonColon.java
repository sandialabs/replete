package learning.streams;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import replete.collections.RArrayList;

public class StreamTestColonColon {
    public static void main(String[] args) {
        List<String> names = new RArrayList<>("Derek Trumbo", "Tom Brounstein");

        StreamTestColonColon inst = new StreamTestColonColon();

        List<String> firstNames = names.stream()
//            .map(s -> s.substring(0, s.indexOf(' ')))
//            .map(s -> toFirst(s))
//            .map(s -> StreamTest2::toFirst(s))
//            .map(StreamTest2::toFirst)
//            .map(s -> inst.toFirstObj(s))
            .map(inst::toFirstObj)
            .collect(Collectors.toList())
        ;
        String[] firstNamesArr = names.stream()
            .map(s -> s.substring(0, s.indexOf(' ')))
            .toArray(String[]::new)
        ;

//        names.stream()
//            .map(n -> toFirst(n))
//            .map(n -> toFirst(n))
//            .map(n -> toFirst(n))
//            .map(n -> toFirst(n))
//            .peek(n -> sendToWebSite(n))
//            .peek(n -> System.out.println(n))
//            .map(n -> toFirst(n))
//            .map(n -> toFirst(n))
//            .map(n -> toFirst(n))
//            .map(n -> toFirst(n))
////            .forEach(n -> System.out.println("{" + n + "}"))
//            .forEach(n -> coolPrinter(n))
//        ;

        names.stream()
            .map(n -> toFirst(n))
        //        .forEach(n -> System.out.println("{" + n + "}"))
//            .forEach(n -> coolPrinter(n))
            .forEach(StreamTestColonColon::coolPrinter)
        ;

        Random random = new Random();

//        Stream<Integer> myCoolRandomStream = Stream.generate(() -> random.nextInt());
        /*Stream<Integer> myCoolRandomStream = */ Stream.generate(random::nextInt)     // Supplier
            .limit(100)
            .map(i -> i * 2)
//            .forEach(i -> System.out.println(i))
            .forEach(System.out::println)
        ;

        System.out.println(firstNames);
    }

    private static void coolPrinter(String nm) {     // Can use this as a Consumer<T>
        System.out.println("{" + nm + "}");
    }

    private String toFirstObj(String nm) {           // Can use this as a Function<T, R>
        return nm.substring(0, nm.indexOf(' '));
    }

    private static String toFirst(String nm) {       // Can use this as a Function<T, R>
        return nm.substring(0, nm.indexOf(' '));
    }

//    private static String produceExample() {
//        return something;
//    }
}
