package replete.params.hier;

import java.io.Serializable;

public interface Criteria<T> extends Serializable {
    boolean appliesTo(T obj);
}
