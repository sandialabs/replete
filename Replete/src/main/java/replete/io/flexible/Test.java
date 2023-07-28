package replete.io.flexible;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import replete.profiler.RProfiler;
import replete.text.StringUtil;
import replete.util.ZipUtil;
import replete.xstream.XStreamWrapper;

public class Test {
    private static final boolean DO_REG = true;
    private static final boolean DO_EXT = true;
    private static final boolean DO_JAVA = true;
    private static final boolean DO_XML = true;
    private static final boolean DO_WRITE = true;
    private static final boolean DO_READ = true;

    public static final int MAX_PERSON = 10000;
    private static File dirConf = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\Replete\\src\\replete\\io\\flexible");
    private static File dirTest = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\Replete\\src\\replete\\io\\flexible\\test");
    private static File java = new File(dirTest, "test.bin");
    private static File javaE = new File(dirTest, "test-flexible.bin");
    private static File xml = new File(dirTest, "test.xml");
    private static File xmlE = new File(dirTest, "test-flexible.xml");
    private static List<File> files = new ArrayList<File>();
    private static Car car;
    private static CarE carE;

    public static void main(String[] args) throws IOException {
        FlexibleSerializationPropertyManager.initialize(new File(dirConf, "sz.conf"));
//        FlexibleSerializationPropertyManager.getProperties(PersonE.class)
//            .addSerializedValueTranslator("kilometers", new ValueTranslator<Double, Double>() {
//                @Override
//                public Double translate(Double value) {
//                    return value * 1000;
//                }
//            });

        if(DO_JAVA) {
            if(DO_REG) {
                files.add(java);
            }
            if(DO_EXT) {
                files.add(javaE);
            }
        }
        if(DO_XML) {
            if(DO_REG) {
                files.add(xml);
            }
            if(DO_EXT) {
                files.add(xmlE);
            }
        }

        if(DO_WRITE) {
            createDefaultCars();
            writeAllCars(car, carE);
            printFileSizes();
            zipFiles();
        }

        if(DO_READ) {
            readAndPrintFromFiles();
        }
    }
    private static void readAndPrintFromFiles() {
        Car carRead = null;
        CarE carERead = null;
        Car carReadX = null;
        CarE carEReadX = null;
        if(DO_JAVA) {
            if(DO_REG) {
                carRead = (Car) readJava(java, "Java");
            }
            if(DO_EXT) {
                carERead = (CarE) readJava(javaE, "JavaE");
            }
        }
        if(DO_XML) {
            if(DO_REG) {
                carReadX = (Car) readXml(xml, "XML");
            }
            if(DO_EXT) {
                carEReadX = (CarE) readXml(xmlE, "XMLE");
            }
        }
        if(DO_JAVA) {
            if(DO_REG) {
                System.out.println("Read Java:  " + carRead);
            }
            if(DO_EXT) {
                System.out.println("Read JavaE: " + carERead);
            }
        }
        if(DO_XML) {
            if(DO_REG) {
                System.out.println("Read XML:   " + carReadX);
            }
            if(DO_EXT) {
                System.out.println("Read XMLE:  " + carEReadX);
            }
        }
    }
    private static void zipFiles() throws IOException {
        for(File file : files) {
            File zipFile = new File(file.getParentFile(), file.getName() + ".zip");
            ZipUtil.zipSingleFile(file, zipFile);
            System.out.println("Zip Of " + file.getName() + ": " + StringUtil.commas(zipFile.length()));
        }
    }
    private static void createDefaultCars() {
        if(DO_REG) {
            car = createCar();
        }
        if(DO_EXT) {
            carE = createCarE();
        }
    }
    private static void printFileSizes() {
        if(DO_JAVA) {
            if(DO_REG) {
                System.out.println("Java:     " + StringUtil.commas(java.length()) + " bytes");
            }
            if(DO_EXT) {
                System.out.println("JavaE:    " + StringUtil.commas(javaE.length()) + " bytes (should be 3 more than Java)");
            }
            if(DO_REG && DO_EXT) {
                System.out.println(" - Ratio: " + ((double) javaE.length() / java.length()));
            }
        }
        if(DO_XML) {
            if(DO_REG) {
                System.out.println("XStream:  " + StringUtil.commas(xml.length()) + " bytes");
            }
            if(DO_EXT) {
                System.out.println("XStreamE: " + StringUtil.commas(xmlE.length()) + " bytes (should be 2 more than XStream)");
            }
            if(DO_REG && DO_EXT) {
                System.out.println(" - Ratio: " + ((double) xmlE.length() / xml.length()));
            }
        }
    }


    /////////////
    // WRITING //
    /////////////

    private static void writeAllCars(Car car, CarE carE) {
        if(DO_JAVA) {
            if(DO_REG) {
                writeJava(java, "Java", car);
            }
            if(DO_EXT) {
//                FlexibleSerializationUtil.Pg = new ManualTimeProfiler();
                writeJava(javaE, "JavaE", carE);
                System.out.println(FlexibleSerializationUtil.X + " / " + FlexibleSerializationUtil.Y);
//                FlexibleSerializationUtil.Pg.print();
            }
        }
        if(DO_XML) {
            if(DO_REG) {
                writeXml(xml, "XML", car);
            }
            if(DO_EXT) {
//                FlexibleSerializationUtil.Pg = new ManualTimeProfiler();
                writeXml(xmlE, "XMLE", carE);
//                FlexibleSerializationUtil.Pg.print();
            }
        }
    }
    private static void writeJava(File file, String type, Object obj) {
        System.out.println("Writing (" + type + "): " + obj);
        RProfiler P = RProfiler.get("Time");
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(obj);
        } catch(Exception e) {
            System.out.println("   ERROR! " + e.getMessage() + " [" + e.getClass().getName() + "]");
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        P.endAndPrint(true);
    }
    private static void writeXml(File file, String type, Object obj) {
        System.out.println("Writing (" + type + "): " + obj);
        RProfiler P = RProfiler.get("Time");
        try {
            XStreamWrapper.writeToFile(obj, file);
        } catch(IOException e) {
            System.out.println("   ERROR! " + e.getMessage() + " [" + e.getClass().getName() + "]");
        }
        P.endAndPrint(true);
    }


    /////////////
    // READING //
    /////////////

    private static Object readJava(File file, String type) {
        System.out.println("Reading (" + type + "): " + file);
        RProfiler P = RProfiler.get("Time");
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(file));
            return in.readObject();
        } catch(Exception e) {
            System.out.println("   ERROR! " + e.getMessage() + " [" + e.getClass().getName() + "]");
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            P.endAndPrint(true);
        }
        return null;
    }
    private static Object readXml(File file, String type) {
        System.out.println("Reading (" + type + "): " + file);
        RProfiler P = RProfiler.get("Time");
        try {
            return XStreamWrapper.loadTarget(file);
        } catch(Exception e) {
            System.out.println("   ERROR! " + e.getMessage() + " [" + e.getClass().getName() + "]");
        } finally {
            P.endAndPrint(true);
        }
        return null;
    }

    private static Car createCar() {
//        Person joe = new Person();
//        joe.fName = "Joe";
//        joe.lName = "Fillibuster";
//        joe.age = 45;
        Car car = new Car();
        car.make = "Ford";
        car.model = "F-150";
        car.year = 1998;
//        car.driver = joe;
        return car;
    }

    private static CarE createCarE() {
//        PersonE joe = new PersonE();
//        joe.fName = "Joe";
//        joe.lName = "Fillibuster";
//        joe.age = 45;
        CarE car = new CarE();
        car.make = "Ford";
        car.model = "F-150";
        car.year = 1998;
//        car.driver = joe;
        return car;
    }
}

class Car implements Serializable {
    String make;
    String model;
    int year;
//    Person driver;
    List<Person> driver = new ArrayList<Person>();
    public Car() {
        Random r = new Random();
        for(int x = 0; x < Test.MAX_PERSON; x++) {
            int rand = r.nextInt(50) + 10;
            Person p = new Person();
            p.fName = "Joe" + rand;
            p.lName = "Fillibuster" + rand;
            p.age = rand;
            driver.add(p);
        }
    }
    private final int length = 100;

    @Override
    public String toString() {
        return
            "length=" + length +
            ",make=" + make +
            ",model=" + model +
            ",year=" + year +
            ",driver=[<list>]";//" + driver + "]";
    }
}

class Person implements Serializable {
    String fName;
    String lName;
    int age;
    @Override
    public String toString() {
        return "fName=" + fName + ",lName=" + lName + ",age=" + age;
    }
}

class CarE implements Serializable {
    String make;
    String model;
    int year;
//    PersonE driver;
    List<PersonE> driver = new ArrayList<PersonE>();
    public CarE() {
        Random r = new Random();
        for(int x = 0; x < Test.MAX_PERSON; x++) {
            int rand = r.nextInt(50) + 10;
            PersonE p = new PersonE();
            p.fName = "Joe" + rand;
            p.lName = "Fillibuster" + rand;
            p.age = rand;
            driver.add(p);
        }
        System.out.println("CTOR CarE");
    }
    private final int length = 100;

    @Override
    public String toString() {
        return
            "length=" + length +
            ",make=" + make +
            ",model=" + model +
            ",year=" + year +
            ",driver=[<list>]";
    }
}

class PersonE extends FlexibleSerializationObject {
    String fName;
    String lName;
    int age;
    final String type = "Human";

    public PersonE() {} // Required public default constructor.

    @Override
    public String toString() {
        return
            "fName=" + fName +
            ",lName=" + lName +
            ",age=" + age
        ;
    }

//    private Object readResolve() {
//        System.out.println("PersonRR");
////        fName = "Scott";
//        return this;
//    }

//    @Override
//    public void writeExternal(ObjectOutput out) throws IOException {
//        out.writeObject(fName + ";" + lName);
//        FlexibleSerializationUtil.write(out, this, null
            //new FlexibleSerializationProperties()
//                .addAliasSerializedToClass("kilometers", "meters")
//                .addClassValueTranslator("fName",
//                    new ValueTranslator<String, String>() {
//                        @Override
//                        public String translate(String value) {
//                            return value + "!BOOTSTRAP";
//                        }
//                    })
//                .addSkipClassField("age")
//        );
//    }

//    @Override
//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        String str = (String) in.readObject();
//        String[] parts = str.split(";");
//        fName = parts[0];
//        lName = parts[1];
//        FlexibleSerializationUtil.read(in, this, null
//            new FlexibleSerializationProperties()
//                .addSerializedValueTranslator("meters", new OneWayValueTranslator<Double,Integer>() {
//                    public Integer translate(Double value) {
//                        return (int) value.doubleValue();
//                    }
//                })
//                .addAliasSerializedToClass("kilometers", "meters")
//                .addSerializedValueTranslator("kilometers", new ValueTranslator<Double, Double>() {
//                    @Override
//                    public Double translate(Double value) {
//                        return value * 1000;
//                    }
//                })
//                .addSerializedValueTranslator("fName",
//                    new ValueTranslator<String, String>() {
//                        @Override
//                        public String translate(String value) {
//                            return StringUtil.cut(value, 10);
//                        }
//                    })
//                .addAliasSerializedToClass("age", "ageX")
//                .addSkipClassField("age")
//                .addSkipClassField("CONST")
//                .addSkipSerializedField("fName")
//        );
//    }
}
