package replete.io.flexible;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import replete.io.FileUtil;
import replete.text.StringUtil;

public class FlexibleSerializationPropertyManager {


    ////////////
    // FIELDS //
    ////////////

    private static Map<Class, FlexibleSerializationProperties> propMap =
        new HashMap<Class, FlexibleSerializationProperties>();
    private static Map<Class, OneWayValueTranslator> globalClassValueTranslators;
    private static Map<Class, OneWayValueTranslator> globalSerializedValueTranslators;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    public static FlexibleSerializationProperties getProperties(Class clazz) {
        FlexibleSerializationProperties props = propMap.get(clazz);
        if(props == null) {
            props = new FlexibleSerializationProperties();
            propMap.put(clazz, props);
        }
        return props;
    }
    public static boolean hasProperties(Class clazz) {
        return propMap.containsKey(clazz);
    }

    // Mutators

    public static void putProperties(Class clazz, FlexibleSerializationProperties props) {
        propMap.put(clazz, props);
    }
    public static void addGlobalClassValueTranslator(Class clazz, OneWayValueTranslator T) {
        if(globalClassValueTranslators == null) {
            globalClassValueTranslators = new HashMap<Class, OneWayValueTranslator>();
        }
        globalClassValueTranslators.put(clazz, T);
    }
    public static void addGlobalSerializedValueTranslator(Class clazz, OneWayValueTranslator T) {
        if(globalSerializedValueTranslators == null) {
            globalSerializedValueTranslators = new HashMap<Class, OneWayValueTranslator>();
        }
        globalSerializedValueTranslators.put(clazz, T);
    }


    //////////
    // MISC //
    //////////

    public static void initialize(File propFile) {
        if(propFile.exists()) {
            String text = FileUtil.getTextContent(propFile);
            String[] lines = text.split("\n");
            Class clazz = null;
            for(String line : lines) {
                if(line.matches("^\\s*[a-zA-Z0-9_\\.\\$]+:\\s*$")) {
                    try {
                        clazz = Class.forName(StringUtil.cut(line.trim(), 1));
                    } catch(Exception e) {
                        clazz = null;
                    }
                } else if(line.matches("^\\s*[a-zA-Z]+\\s*=\\s*.*$") && clazz != null) {
                    String[] parts = line.trim().split("\\s*=\\s*", 2);
                    String key = parts[0].trim();
                    String val = parts[1].trim();

                    if(key.equalsIgnoreCase("skipClassFields")) {
                        if(!val.isEmpty()) {
                            FlexibleSerializationProperties props = getProperties(clazz);
                            String[] fields = val.split("\\s*,\\s*");
                            for(String field : fields) {
                                props.addSkipClassField(field);
                            }
                        }
                    } else if(key.equalsIgnoreCase("skipSerializedFields")) {
                        if(!val.isEmpty()) {
                            FlexibleSerializationProperties props = getProperties(clazz);
                            String[] fields = val.split("\\s*,\\s*");
                            for(String field : fields) {
                                props.addSkipSerializedField(field);
                            }
                        }
                    } else if(key.equalsIgnoreCase("aliasesSerializedToClass")) {
                        if(!val.isEmpty()) {
                            FlexibleSerializationProperties props = getProperties(clazz);
                            String[] fields = val.split("\\s*,\\s*");
                            for(String field : fields) {
                                String[] kv = field.split("\\s*>\\s*");
                                if(kv.length == 2) {
                                    props.addAliasSerializedToClass(kv[0], kv[1]);
                                }
                            }
                        }
                    } else if(key.equalsIgnoreCase("aliasesClassToSerialized")) {
                        if(!val.isEmpty()) {
                            FlexibleSerializationProperties props = getProperties(clazz);
                            String[] fields = val.split("\\s*,\\s*");
                            for(String field : fields) {
                                String[] kv = field.split("\\s*>\\s*");
                                if(kv.length == 2) {
                                    props.addAliasClassToSerialized(kv[0], kv[1]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
