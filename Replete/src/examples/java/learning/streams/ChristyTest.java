package learning.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChristyTest {

    public static void main(String[] args) {

        List<Person> people = new ArrayList<>();
        people.add(new Person("Dave"));        // 4
        people.add(new Person("Susan"));
        people.add(new Person("Phyllis"));
        people.add(new Person("Fred"));        // 4
        people.add(new Person("Alan"));        // 4
        people.add(new Person("Veronica"));

//        var people = new ArrayList<Person>();

        int total = 0;
        for(Person p : people) {           // Handled by .stream()
            String name = p.getName();     // Handled by .map(...)
            int len = name.length();       // Handled by .map(...)
            total += len;
        }
        System.out.println(total);

        List<Integer> allLengths = people.stream()
            .map(p -> p.getName())
            .map(n -> n.length())
            .collect(Collectors.toList())
        ;
        Integer totalLen = people.stream()
            .map(p -> p.getName())
            .map(n -> n.length())
            .collect(Collectors.summingInt(Integer::intValue))
        ;

//        int a = totalLen.intValue();

        System.out.println(allLengths);
        System.out.println(totalLen);

        Integer firstHalfAlphaTotal = people.stream()
            //--
            .map(p -> p.getName())
            .filter(n -> {
                char firstCh = n.charAt(0);
                // At least in English (and only English)
                // upper and lower case are only off by the 32 bit  (and 32 on ASCII table is the space character)

//                firstCh = firstCh | 32;   // Lower case
//                firstCh = firstCh & ~32;  // Upper case

                if(firstCh >= 'A' && firstCh <= 'M') {
                    return true;
                }
                return false;
            })
            //--
            .map(n -> n.length())
            //--
            .collect(Collectors.summingInt(Integer::intValue))
        ;

    }

    private static class Person {
        private String name;
        public Person(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
}
