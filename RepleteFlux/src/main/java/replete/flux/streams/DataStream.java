package replete.flux.streams;

import java.util.ArrayList;
import java.util.List;

import replete.plugins.HumanDescriptor;

public class DataStream {        // Maybe made into a configurable PersistentController one day
    private HumanDescriptor systemDescriptor = new HumanDescriptor();
    List<Object> o = new ArrayList<>();
    public int size() {
        return o.size();
    }
    public HumanDescriptor getSystemDescriptor() {
        return systemDescriptor;
    }
    public DataStream setSystemDescriptor(HumanDescriptor systemDescriptor) {
        this.systemDescriptor = systemDescriptor;
        return this;
    }
}
