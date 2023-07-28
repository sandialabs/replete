package replete.equality.similarity;

public class EqualsIgnoreCaseRule implements AcceptableSimilarityRule<String> {
    public boolean test(String o1, String o2) {
        return o1 != null && o1.equalsIgnoreCase(o2);   // o2 can be null here
    }
}
