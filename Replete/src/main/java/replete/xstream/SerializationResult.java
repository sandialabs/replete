package replete.xstream;

/**
 * @author Derek Trumbo
 */

public class SerializationResult {
    protected SerializationMetadata metadata;
    protected Object targetObject;

    private SerializationResult() {}

    public SerializationResult(SerializationMetadata md, Object obj) {
        metadata = md;
        targetObject = obj;
    }

    public SerializationMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SerializationMetadata md) {
        metadata = md;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object obj) {
        targetObject = obj;
    }
}
