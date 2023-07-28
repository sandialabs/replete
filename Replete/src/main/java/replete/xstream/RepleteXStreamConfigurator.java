package replete.xstream;

import com.thoughtworks.xstream.XStream;

import replete.collections.ExtArrayListXStreamConverter;

public class RepleteXStreamConfigurator implements XStreamConfigurator {
    @Override
    public void configure(XStream xStream) {
        xStream.registerConverter(new ExtArrayListXStreamConverter());
    }
}
