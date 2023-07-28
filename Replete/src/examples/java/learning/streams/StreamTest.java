package learning.streams;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JButton;

import learning.Person;
import replete.text.StringUtil;

public class StreamTest {

    public static void main(String[] args) {

        // Streams & Lambda expressions came in Java 8

        // Deal with problems: expressivity, simplicity, readability and quality

        List<String> list = null;
        Map<String, String> map = null;

//        String familyNames = "";
//        for(String elem : list) {
//            elem = elem.substring(0, 1);
//            Person p = findPerson(elem);
//            if(p.age > 10) {
//                Family f = new Family(p);
//                familyNames += f.getName() + ",";
//            }
//        }

        Function<String, String> mapper = new Function<String, String>() {  // Anonymous Inner Class
            @Override
            public String apply(String s) {
                return s.substring(0, 1);
            }
        };
        Function<String, String> mapper2 = (String s) -> {     // Lambda Expression
            return s.substring(0, 1);
        };
        Function<String, String> mapper3 = (s) -> {     // Lambda Expression
            return s.substring(0, 1);
        };
        Function<String, String> mapper4 = s -> {
            return s.substring(0, 1);
        };
        Function<String, String> mapper5 = s -> s.substring(0, 1);

        JButton btnGo = new JButton("Go");
        btnGo.addActionListener(new ActionListener() {       // Anonymous inner class
            @Override
            public void actionPerformed(ActionEvent e) {
                // do something!
                updateLabels();
            }
        });
        ActionListener myListener = e -> System.out.println(e);
        btnGo.addActionListener((ActionEvent e) -> {  // Provides an implementation for actionPerformed
            updateLabels();
//            return 1;
        });
        MyHandler handler = new MyHandler();
        btnGo.addActionListener(handler);
//        myWindow.addWindowListener(handler);

        list.stream()               // .stream() followed by the operations (method calls on the Stream object) you want, those operations take arguments
//            .map(new Mapper())
//            .map(mapper)
//            .map(mapper2)
//            .map(mapper3)

            .map(s -> s.substring(0, 1))
        ;

        // "Hey Google, give me a list of all the family names of the people whose IDs are in this list"
    }

    // Arrays and Lists iterate over elements
    // Maps you can iterate over: keys, values, entries

//    public List<String> getIncompatibleConflicts(List<Conflict> conflicts) {
//        List<String> incompatConflicts = new ArrayList<>();
//
//        for(Conflict conflict : conflicts) {                                     // Standard method
//            String incompatVersions = conflict.getIncompatibleDependencies();
//            if(incompatVersions != null) {
//                incompatConflicts.add(
//                    conflict.getFirst().getGroupId() + ":" +
//                    conflict.getFirst().getArtfId() + ":" +
//                    incompatVersions
//                );
//            }
//        }
//
//        return incompatConflicts;
//    }

    public List<Ssn> collect1990Ssns() {
        List<Person> people = new ArrayList<>();

        List<Ssn> list1990babies = new ArrayList<>();
        for(Person p : people) {                          // Standard method
            String name = p.getName();
            if(!StringUtil.isBlank(name)) {
                int sp = name.indexOf(' ');
                String lastName = name.substring(sp + 1);
                Ssn ssn = Ssn.lookup(lastName);
                if(ssn.isBetween(1990, 1999)) {
                    list1990babies.add(ssn);
                }
            }
        }

        List<Ssn> list1990babies2 = people.stream()
            .map(p -> p.getName())      // math sense, transform
            .filter(n -> !StringUtil.isBlank(n))
            .map(n -> {
                int sp = n.indexOf(' ');
                return n.substring(sp + 1);
            })
            .map(ln -> Ssn.lookup(ln))
            .filter(ssn -> ssn.isBetween(1990, 1999))       // "regular operations"
            .collect(Collectors.toList())                   // "Terminal operation"
        ;

        return list1990babies2;
    }

    public static class Ssn {
        public static Ssn lookup(String ln) {return null;}
        public static boolean isBetween(int a, int b) {return false;}
    }

    private static interface Function<I, O> {
        O apply(I input /*, ParamType p2 ,a, d,,d ,d*/);
    }
    private static interface Producer<O> {
        O produce();
    }
    private static interface Consumer<I> {
        void consume(I input);
    }

    private static Object updateLabels() {
        // TODO Auto-generated method stub
        return null;
    }

    private static class MyHandler implements ActionListener, KeyListener, WindowListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        }
        @Override
        public void windowOpened(WindowEvent e) {
        }
        @Override
        public void windowClosing(WindowEvent e) {
        }
        @Override
        public void windowClosed(WindowEvent e) {
        }
        @Override
        public void windowIconified(WindowEvent e) {
        }
        @Override
        public void windowDeiconified(WindowEvent e) {
        }
        @Override
        public void windowActivated(WindowEvent e) {
        }
        @Override
        public void windowDeactivated(WindowEvent e) {
        }
        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
        }
        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

//    private static class MyMapper implements Function<String, String> {
//        @Override
//        public String apply(String t) {
//            return t.substring(0, 1);
//        }
//    }
}

//interface PeopleDataConsumer {
//    void processPeople(Person p);
//}
//
//interface PeopleDataConsumer extends Consumer<Person> {
//    void accept(Person p);
//}
