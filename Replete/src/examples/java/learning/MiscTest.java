package learning;

public class MiscTest {

    // OO Topics:
    //   Encapsulation (Object, ADT)
    //   Data Hiding

    // Other Topics:
    //   Varargs: int... list
    //   Method-Builder: return this;
    //   Named Arguments: something(firstName: "Derek")
    //   Optional Arguments (java doesn't have them)

//    public void x() {
//        something("Derek", , "BigD");  // Generally not available
//    }
//    public static void x(String fName) {
//        something(fName, null);
//    }
//    public static void x(String lName, int age) {
//        something(null, lName);
//        print(age);
//    }
//    public static void x(String fName, String lName, ArrayList... lists) {
//        something(fName, lName);
//    }
//    public static void something(String firstName, optional int age = 0, optional String alias = "Timmmay") {
//        System.out.println(firstName);
//        System.out.println(age);
//    }

    public static void main(String... args) {
//        char[] chars = new char[] {'A', 'B', 'C'};
//        System.out.println(Arrays.toString('A', 'B', 'C', 'D', 'E'));
//        if(true) {
//            return;
//        }

//        String[] names = new String[3];      // Not encapsulated
//        int[]    ages  = new int[3];
//        float[]  weights = new float[3];
//        double[] bankAccountBalances = new double[3];

//        Object[][] allData = new Object[3][4];
//        allData[0][0] = "Derek";
//        allData[0][1] = 12;

        Person[] people  = new Person[3];

        Person joey = new Person()
            .setName("Joey")
            .setAge(20)
            .setWeight(20034.12F)
            .setBalance(-21.2F)
        ;

        System.out.println("Enter The Shiat 8=======>");

        // while(!done) {  (using all lists instead of arrays)
        for(int i = 0; i < 3; i++) {

            Person person = Person.readPerson();

            people[i] = person;

            System.out.println(person);

//            if(person.thisAge >= 18) {
//                System.out.println("Can now fight for your country - just not drink.");
//            }
        }

        //  yourFunction(b: 1, a: 3, c: 2);
    }

    private static void yourFunction(int a, int b, int c) {   // 3 PARAMETERS
        yourFunction(1, 2, 3);   // 3 ARGUMENTS
    }
    // Modifiers; Return Type; Name; Args; Code Block
    // Primitive Data Types
    // Abstract Data Types
}

// 4:
// Public ("public")
// Package ("")              // DEFAULT
// Protected ("protected")
// Private ("private")