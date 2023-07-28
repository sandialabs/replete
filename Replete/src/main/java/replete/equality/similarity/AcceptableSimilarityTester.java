package replete.equality.similarity;

import java.io.IOException;

import replete.collections.RHashMap;

// Not nearly good enough for our purposes:
//     http://commons.apache.org/proper/commons-lang/javadocs/api-release/index.html
//     return new EqualsBuilder()
//         .append(this.id, otherObject.id)
//         .append(this.name, otherObject.name)
//         .isEquals()
//     ;
// Need to make something like:
// XXXX x = new XXXXBuilder()
//     .addRule("name", new Rule<String>() {boolean test(String o1, String o2) {return o1.charAt(0) == o2.charAt(0);}))
//     .setY()
// Comparator<T> Not enough
//     int compare(T o1, T o2); - Allows to decide which of the two objects is "greater" or "lesser" or "equals"

public class AcceptableSimilarityTester {
    public static void main(String[] args) throws IOException {
        DriversLicense lic;

        Person joe = new Person("Joe", 12, null);
        Person sally = new Person("Sally", 25, new DriversLicense(17));
        Person markus = new Person("Markus", 25, null);
        Person markus2 = new Person("Markus", 39, lic = new DriversLicense(19));
        Person tony = new Person("Tonyxx", 39, lic);

        AcceptableSimilarityRule<Person> rule = new ObjectMemberAccessRule<Person>()
            .addRule((p1, p2) -> p1.age == p2.age)
        ;
        AcceptableSimilarityRule<Person> rule2 = new ObjectMemberAccessRule<Person>()
            .addFieldRule("name", new SameRule())
        ;
        AcceptableSimilarityRule<Person> rule3 = new ObjectMemberAccessRule<Person>()
            .addFieldRule("license", new SameRule())
        ;
        AcceptableSimilarityRule<Person> rule3b = new ObjectMemberAccessRule<Person>()
            .addFieldRule("license", new ObjectMemberAccessRule<DriversLicense>()
                .addRule(new SameRule())  //..
            )
            .addRule(
                new AndRule<Person>(
                    (p1, p2) -> p1.name.length() == p2.name.length(),
                    (p1, p2) -> p1.name.length() != p2.name.length()
                )
            )
        ;

//        System.out.println(rule.test(joe, sally));
//        System.out.println(rule.test(sally, markus));
//        System.out.println(rule2.test(markus, markus2));
//        System.out.println(rule3.test(markus2, tony));
//        System.out.println(rule3b.test(markus2, tony));

        AcceptableSimilarityRule<Person> rulex = new ObjectMemberAccessRule<Person>()
//            .addRule(new EqualsRule<Person>())
//            .addDefaultFieldRule(new BothNullRule())
            .addDefaultFieldRule(new EqualsRule(false))
            .addFieldRule("name", new EqualsRule())
            .addFieldRule("age", new GreaterThanOrEqualToRule())
            .addFieldRule("map", new MapRule()
                .addDefaultEntryRule(new EqualsIgnoreCaseRule())
                .addEntryRule("austin", new EqualsIgnoreCaseRule())
            )
        ;
        Person px1 = new Person("abc", 8, new DriversLicense(11));
        Person px2 = new Person("abc", 7, new DriversLicense(11));
        px1.setMap("denver", "coloradox", "austin", "texas");
        px2.setMap("denver", "coloradoy", "austin", "Texas");
        System.out.println(rulex.test(px1, px2));

//        File f1 = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\WebComms\\test\\gov\\sandia\\webcomms\\g1-u0-httpcnncom.xml");
//        File f2 = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\WebComms\\test\\gov\\sandia\\webcomms\\g2-u0-httpcnncom.xml");
//        HttpResource r1 = XStreamWrapper.loadTargetFromFile(f1);
//        HttpResource r2 = XStreamWrapper.loadTargetFromFile(f2);
//        System.out.println(System.identityHashCode(r1.getCleanedUrl()));
//        System.out.println(System.identityHashCode(r2.getCleanedUrl()));
//        AcceptableSimilarityRule<HttpResource> httpResoRule = new ObjectMemberAccessRule<HttpResource>()
//            .addFieldRule("originalUrl", new EqualsRule())
//            .addFieldRule("cleanedUrl", new SameRule())
//            .addFieldRule("method", new SameRule())
//            .addFieldRule("providingIpPort", new EqualsRule())
//            .addFieldRule("startDownload", new LessThanRule())
//            .addMethodRule("getDownloadDuration", new GreaterThanRule())
//        ;
//        System.out.println("HTTP RESO SIMILAR = " + httpResoRule.test(r1, r2));
    }
}

class Person {
    String name;
    Integer age;
    DriversLicense license;
    RHashMap<String, String> map = new RHashMap<>();
    Person() {}
    public Person(String name, int age, DriversLicense license) {
        this.name = name;
        this.age = age;
        this.license = license;
    }
    public Person setMap(Object... pairs) {
        map.put(pairs);
        return this;
    }
}

class DriversLicense {
    int date;
    DriversLicense(int date) {
        this.date = date;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + date;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        DriversLicense other = (DriversLicense) obj;
        if(date != other.date) {
            return false;
        }
        return true;
    }
}
