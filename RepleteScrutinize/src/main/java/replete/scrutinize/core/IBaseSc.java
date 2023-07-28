package replete.scrutinize.core;

import java.io.Serializable;
import java.util.Map;

import replete.plugins.ExtensionPoint;

public interface IBaseSc extends ExtensionPoint, Serializable, Map {
    Map<String, ScFieldResult> getFields();
    Class<?> getHandledClass();
    String[] getExtractedFields();
    Map<String, Object> getCustomFields(Object nativeObj);
    default String getSimpleToString(Object o) {
        return null;
    }
}
