package learning.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HatProvider {

    private static final int NUM_PEOPLE = 100;
    private static List<Person> line = new ArrayList<>();

    public static void main(String[] args) {
        for(int i = 0; i < NUM_PEOPLE; i++) {
            line.add(new Person());
        }

        Runnable hatDistributionProcess = new Runnable() {
            @Override
            public void run() {
                int newHatSize = new Random().nextInt(9) + 2;
                for(int i = 0; i < line.size(); i++) {
                    Person p = line.get(i);
                    if(p.hatSize == newHatSize) {
                        p.hatCount++;
                        //remove person from line at the position they're in
                        //add to end of the line
                    }
                }
            }
        };

        Thread hatProvider1 = new Thread(hatDistributionProcess);
        Thread hatProvider2 = new Thread(hatDistributionProcess);

        hatProvider1.start();
        hatProvider2.start();


    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private static class Person {
        //name, age, ssn
        int hatSize;
        int hatCount = 0;

        public Person() {
            hatSize = new Random().nextInt(9) + 2;      //  2-10 incl off-by-one errors
        }
    }
}
