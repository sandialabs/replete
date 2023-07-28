package replete.scrutinize.wrappers.net;

import java.net.NetworkInterface;

import replete.scrutinize.core.BaseSc;

public class NetworkInterfaceSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return NetworkInterface.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getNetworkInterfaces",
            //getDefault //??
            "displayName",
            "hardwareAddress",
            "index",
            "inetAddresses",
            "interfaceAddresses",
            "getMTU",
            "name",
            "parent",
            "subInterfaces",
            "loopback",
            "pointToPoint",
            "up",
            "virtual",
            "supportsMulticast"
        };
    }
}
