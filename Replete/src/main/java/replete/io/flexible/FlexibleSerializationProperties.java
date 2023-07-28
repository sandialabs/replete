package replete.io.flexible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlexibleSerializationProperties {


    ////////////
    // FIELDS //
    ////////////

    private List<String> skipClassFields = null;
    private List<String> skipSerializedFields = null;
    private Map<String, String> aliasesSerializedToClass = null;
    private Map<String, String> aliasesClassToSerialized = null;
    private Map<String, OneWayValueTranslator> classValueTranslators = null;
    private Map<String, OneWayValueTranslator> serializedValueTranslators = null;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<String> getSkipClassFields() {
        return skipClassFields;
    }
    public List<String> getSkipSerializedFields() {
        return skipSerializedFields;
    }
    public Map<String, String> getAliasesSerializedToClass() {
        return aliasesSerializedToClass;
    }
    public Map<String, String> getAliasesClassToSerialized() {
        return aliasesClassToSerialized;
    }
    public Map<String, OneWayValueTranslator> getClassValueTranslators() {
        return classValueTranslators;
    }
    public Map<String, OneWayValueTranslator> getSerializedValueTranslators() {
        return serializedValueTranslators;
    }

    // Mutators (Builder)

    public FlexibleSerializationProperties addSkipClassField(String name) {
        if(skipClassFields == null) {
            skipClassFields = new ArrayList<String>();
        }
        skipClassFields.add(name);
        return this;
    }
    public FlexibleSerializationProperties addSkipSerializedField(String name) {
        if(skipSerializedFields == null) {
            skipSerializedFields = new ArrayList<String>();
        }
        skipSerializedFields.add(name);
        return this;
    }
    public FlexibleSerializationProperties addAliasSerializedToClass(
                String serializedFieldName, String classFieldName) {
        if(aliasesSerializedToClass == null) {
            aliasesSerializedToClass = new HashMap<String, String>();
        }
        aliasesSerializedToClass.put(serializedFieldName, classFieldName);
        return this;
    }
    public FlexibleSerializationProperties addAliasClassToSerialized(
                String classFieldName, String serializedFieldName) {
        if(aliasesClassToSerialized == null) {
            aliasesClassToSerialized = new HashMap<String, String>();
        }
        aliasesClassToSerialized.put(classFieldName, serializedFieldName);
        return this;
    }
    public FlexibleSerializationProperties addClassValueTranslator(
                String classFieldName, OneWayValueTranslator T) {
        if(classValueTranslators == null) {
            classValueTranslators = new HashMap<String, OneWayValueTranslator>();
        }
        classValueTranslators.put(classFieldName, T);
        return this;
    }
    public FlexibleSerializationProperties addSerializedValueTranslator(
                String serializedFieldName, OneWayValueTranslator T) {
        if(serializedValueTranslators == null) {
            serializedValueTranslators = new HashMap<String, OneWayValueTranslator>();
        }
        serializedValueTranslators.put(serializedFieldName, T);
        return this;
    }


    //////////
    // MISC //
    //////////

    public void append(FlexibleSerializationProperties otherProps) {
        if(otherProps.getSkipClassFields() != null) {
            for(String field : otherProps.getSkipClassFields()) {
                addSkipClassField(field);
            }
        }
        if(otherProps.getSkipSerializedFields() != null) {
            for(String field : otherProps.getSkipSerializedFields()) {
                addSkipSerializedField(field);
            }
        }
        if(otherProps.getAliasesSerializedToClass() != null) {
            for(String szField : otherProps.getAliasesSerializedToClass().keySet()) {
                addAliasSerializedToClass(szField, otherProps.getAliasesSerializedToClass().get(szField));
            }
        }
        if(otherProps.getAliasesClassToSerialized() != null) {
            for(String clField : otherProps.getAliasesClassToSerialized().keySet()) {
                addAliasClassToSerialized(clField, otherProps.getAliasesClassToSerialized().get(clField));
            }
        }
        if(otherProps.getClassValueTranslators() != null) {
            for(String clField : otherProps.getClassValueTranslators().keySet()) {
                addClassValueTranslator(clField, otherProps.getClassValueTranslators().get(clField));
            }
        }
        if(otherProps.getSerializedValueTranslators() != null) {
            for(String clField : otherProps.getSerializedValueTranslators().keySet()) {
                addSerializedValueTranslator(clField, otherProps.getSerializedValueTranslators().get(clField));
            }
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "FlexibleSerializationProperties [skipClassFields=" + skipClassFields +
            ", skipSerializedFields=" + skipSerializedFields + ", aliasesSerializedToClass=" +
            aliasesSerializedToClass + ", aliasesClassToSerialized=" + aliasesClassToSerialized +
            "]";
    }
}
