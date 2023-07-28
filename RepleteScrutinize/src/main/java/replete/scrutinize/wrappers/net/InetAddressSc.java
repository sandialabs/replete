package replete.scrutinize.wrappers.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Map;
import java.util.TreeMap;

import replete.scrutinize.core.BaseSc;

public class InetAddressSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return InetAddress.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getLocalHost",
            "getLoopbackAddress",
            "address",
            "canonicalHostName",
            "hostAddress",
            "hostName",
            "anyLocalAddress",
            "isAnyLocalAddress",
            "linkLocalAddress",
            "multicastAddress",
            "siteLocalAddress",
            "isLoopbackAddress"
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        InetAddress i = (InetAddress) nativeObj;
        Map<String, Object> fields = new TreeMap<>();
        fields.put("IP Version", i instanceof Inet4Address ? "4" : "6");
        return fields;
    }
}
