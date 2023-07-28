package replete.collections.mm;

import java.util.Map;

public interface MapCreator<E, F> {
    Map<E, F> create();
}
