package replete.collections.tl;

import replete.collections.tl.events.TLChangeEvent;
import replete.event.ExtChangeListener;

public interface TLChangeListener<T> extends ExtChangeListener<TLChangeEvent<T>> {

}
