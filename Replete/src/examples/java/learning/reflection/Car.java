package learning.reflection;

public class Car {
    private String make;
    private String model;
    private int year;
    private SteeringWheel steeringWheel;

    public Car(String make, String model, int year, SteeringWheel steeringWheel) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.steeringWheel = steeringWheel;
    }
}