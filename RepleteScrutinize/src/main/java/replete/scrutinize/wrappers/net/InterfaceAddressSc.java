package replete.scrutinize.wrappers.net;

import java.net.InterfaceAddress;

import replete.scrutinize.core.BaseSc;

public class InterfaceAddressSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return InterfaceAddress.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "address",
            "broadcast",
            "networkPrefixLength"
        };
    }
}
