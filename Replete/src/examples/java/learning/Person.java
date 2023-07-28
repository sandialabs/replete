package learning;

import replete.cli.ConsoleUtil;

public class Person {    // Encapsulation


    ////////////
    // STATIC //
    ////////////

    public static Person readPerson() {   // Static Helper Person Creator!
        return new Person()
            .setName(ConsoleUtil.getLine())
            .setAge(Integer.parseInt(ConsoleUtil.getLine()))
            .setWeight(Float.parseFloat(ConsoleUtil.getLine()))
            .setBalance(Double.parseDouble(ConsoleUtil.getLine()))
        ;
    }


    ////////////
    // FIELDS //
    ////////////

    private String name;
    private int age;
    private float weight;
    private double balance;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Person() {
        // Default or "No-Argument" Constructor
    }
    public Person(Person other) {  // Copy Constructor
        name = other.name;
        age = other.age;
        weight = other.weight;
        balance = other.balance;
    }
    public Person(String name, int age) {
        this(name, age, 0.0F, 0.0);
    }
    public Person(String name, int age, float weight, double balance) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.balance = balance;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public float getWeight() {
        return weight;
    }
    public double getBalance() {
        return balance;
    }

    // Mutators (Builder)

    public Person setName(String name) {
        this.name = name;
        return this;
    }
    public Person setAge(int age) {
        this.age = age;
        return this;
    }
    public Person setWeight(float weight) {
        this.weight = weight;
        return this;
    }
    public Person setBalance(double balanace) {
        balance = balanace;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
            .append("Person [name=")
            .append(name)
            .append(", age=")
            .append(age)
            .append(", weight=")
            .append(weight)
            .append(", baBalance=")
            .append(balance)
            .append("]");
        return builder.toString();
    }
}
