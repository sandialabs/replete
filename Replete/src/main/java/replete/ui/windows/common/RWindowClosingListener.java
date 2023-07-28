package replete.ui.windows.common;

import replete.event.ExtChangeListener;

public interface RWindowClosingListener extends ExtChangeListener<RWindowClosingEvent> {
    // Nothing extra needed here.  This interface allows
    // client code not to have to specify <CommonWindowClosingEvent>.
}
