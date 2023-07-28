package replete.io.flexible;

public interface OneWayValueTranslator<F,T> {
    T translate(F value);
}
