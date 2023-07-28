package replete.equality.similarity;

public class GreaterThanOrEqualToRule implements AcceptableSimilarityRule<Number> {         // and the reverse
    public boolean test(Number o1, Number o2) {
        return
            o1 != null && o2 != null &&
            o1.doubleValue() >= o2.doubleValue()
        ;
    }
}
