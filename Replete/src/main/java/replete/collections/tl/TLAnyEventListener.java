package replete.collections.tl;

import replete.collections.tl.events.TLEvent;
import replete.event.ExtChangeListener;

public interface TLAnyEventListener<T> extends ExtChangeListener<TLEvent<T>> {

}
