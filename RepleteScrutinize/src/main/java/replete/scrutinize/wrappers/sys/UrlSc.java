package replete.scrutinize.wrappers.sys;

import java.net.URL;

import replete.scrutinize.core.BaseSc;

public class UrlSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return URL.class;
    }

    @Override
    public String getSimpleToString(Object o) {
        URL url = (URL) o;
        return url.toString();
    }
}
