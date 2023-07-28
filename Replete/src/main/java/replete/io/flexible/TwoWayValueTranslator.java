package replete.io.flexible;

public interface TwoWayValueTranslator<F,T> {
    T translateTo(F value);
    F translateFrom(T value);
}
