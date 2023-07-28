package replete.io.flexible;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

// This class ignores any conflicts in serialVersionUID.

public class FlexibleObjectInputStream extends ObjectInputStream {

    public FlexibleObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass resultClassDesc = super.readClassDescriptor();// initially streams descriptor
        Class localClass; // the class in the local JVM that this descriptor represents.
        try {
            localClass = Class.forName(resultClassDesc.getName());
        } catch (ClassNotFoundException e) {
            //logger.error("No local class for " + resultClassDescriptor.getName(), e);
            return resultClassDesc;
        }
        ObjectStreamClass localClassDesc = ObjectStreamClass.lookup(localClass);
        if(localClassDesc != null) { // only if class implements serializable
            long localSUID = localClassDesc.getSerialVersionUID();
            long streamSUID = resultClassDesc.getSerialVersionUID();

            // Check for serialVersionUID mismatch.
            if(streamSUID != localSUID && FlexibleExternalizable.class.isAssignableFrom(localClass)) {
                // Use local class descriptor for deserialization.
                resultClassDesc = localClassDesc;
            }
        }
        return resultClassDesc;
    }
}