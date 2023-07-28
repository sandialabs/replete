package replete.scrutinize.wrappers.sys;

import replete.scrutinize.core.BaseSc;

public class ClassSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Class.class;
    }

    @Override
    public String getSimpleToString(Object o) {
        Class<?> clazz = (Class<?>) o;
        return clazz.getName();
    }
}
