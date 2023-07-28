package replete.io.flexible;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class FlexibleSerializationObject implements FlexibleExternalizable {
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        FlexibleSerializationUtil.write(out, this);
    }
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        FlexibleSerializationUtil.read(in, this);
    }
}
