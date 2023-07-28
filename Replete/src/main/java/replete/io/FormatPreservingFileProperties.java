package replete.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * This properties file reader/writer takes the standard
 * Java properties file interpretation a step further
 * (java.util.Properties.[load/store]).
 *
 * First of all, the Java Properties does not allow
 * comments in any location other than the beginning of
 * the file.  Also, you may want to have comments on
 * the same line as the properties themselves.
 *
 * This file properties interpreter preserves all
 * whitespace, including newlines, and all comments,
 * either on their own line or at the end of a property
 * line.
 *
 * Only the characters that comprise the value of the
 * property are modified.  This class contains an option
 * to remove property lines that are no longer in the
 * set of properties.
 *
 * Additionally, this allows the developer to align
 * the values in a column by lining up the equals
 * signs.
 *
 * What this class does not yet do is support multi-
 * line properties (neither with \ at end of line nor
 * otherwise).  No escape characters are supported
 * (\# nor \= mean anything special).  This means that
 * property values with newline characters will not
 * be read back in properly.
 *
 * Future Possibilities:
 * 1.  option on whether or not to write out the new
 *     properties that aren't in the file.
 * 2.  option on whether or not to write out the date
 *     written to the file as a comment much like
 *     the Java properties interpretation.
 *
 * @author Derek Trumbo
 */

public class FormatPreservingFileProperties extends Properties {


    ////////////
    // FIELDS //
    ////////////

    protected File file;
    protected String initialSaveTemplate;
    protected boolean removeAbsentProperties;

    protected List<String> lines;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FormatPreservingFileProperties(File file) {
        this(file, null, false);
    }
    public FormatPreservingFileProperties(File file, boolean removeAbsentProperties) {
        this(file, null, removeAbsentProperties);
    }
    public FormatPreservingFileProperties(File file, String initialSaveTemplate, boolean removeAbsentProperties) {
        this.file = file;
        this.initialSaveTemplate = initialSaveTemplate;
        this.removeAbsentProperties = removeAbsentProperties;

        lines = new ArrayList<String>();
        load();
    }


    //////////
    // LOAD //
    //////////

    protected void load() {
        if(!file.exists()) {
            if(initialSaveTemplate != null) {
                try {
                    BufferedReader reader = new BufferedReader(new StringReader(initialSaveTemplate));
                    String line;
                    while((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                } catch(Exception e) {}
            }
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null) {

                // Record raw line.
                lines.add(line);

                // If it's not a total comment line...
                if(!line.trim().startsWith("#")) {

                    int eq = line.indexOf("=");
                    int hash = line.indexOf("#");

                    // If there is a key/value pair on this line...
                    // (if the equals sign isn't after a # char
                    // (e.g. "invalid line  # x = y + b"))
                    if(eq != -1 && (hash == -1 || eq < hash)) {
                        String key = line.substring(0, eq).trim();

                        // If the key isn't blank (e.g. " = invalid line")
                        if(!key.equals("")) {
                            String value;

                            // If there's no end of line comment...
                            if(hash == -1) {
                                value = line.substring(eq + 1);

                            // Else if there is...
                            } else {
                                value = line.substring(eq + 1, hash);
                            }

                            setProperty(key, value.trim());
                            //System.out.println("Set [" + key + "] to [" + value.trim() + "]"); // Debug
                        }
                    }
                }
            }
        } catch(Exception e) {

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(Exception e) {

                }
            }
        }
    }


    //////////
    // SAVE //
    //////////

    public void save() {

        // If there is no file, but no template provided,
        // just save out the current properties with no
        // additional formatting of any kind.
        if(!file.exists() && initialSaveTemplate == null) {
            savePropsOnly();
        } else {
            savePropsAndLines();
        }
    }

    private void savePropsOnly() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for(Object key : keySet()) {
                String keyS = (String) key;
                String value = getProperty(keyS) != null ? getProperty(keyS) : "";
                String newLine = keyS + " = " + value;
                writer.write(newLine + "\n");
            }

        } catch(Exception e) {

        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch(Exception e) {

                }
            }
        }
    }

    private void savePropsAndLines() {
        Set<String> writtenKeys = new HashSet<String>();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for(String line : lines) {

                // Initially we'll just say we're gonna write out original raw line.
                String newLine = line;

                // If it's not a total comment line...
                if(!line.trim().startsWith("#")) {

                    int eq = line.indexOf("=");
                    int hash = line.indexOf("#");

                    // If there is a key/value pair on this line...
                    // (if the equals sign isn't after a # char
                    // (e.g. "invalid line  # x = y + b"))
                    if(eq != -1 && (hash == -1 || eq < hash)) {
                        String key = line.substring(0, eq).trim();

                        // If the key isn't blank (e.g. " = invalid line")
                        if(!key.equals("")) {

                            // If the key exists as a property still...
                            // (maybe this is configurable in ctor?)
                            if(!containsKey(key) && removeAbsentProperties) {
                                newLine = null;

                            } else {
                                String value = getProperty(key) != null ? getProperty(key) : "";
                                String sp = (line.charAt(eq - 1) == ' ') ? " " : "";
                                if(!value.equals("")) {
                                    value = sp + value;
                                }

                                // If there's an end of line comment, keep it
                                // and any whitespace between it and the value.
                                if(hash != -1) {
                                    int start = hash;
                                    while(line.charAt(start - 1) <= 32) {
                                        start--;
                                    }
                                    value = value + line.substring(start);
                                }

                                newLine = line.substring(0, eq) + "=" + value;
                                writtenKeys.add(key);
                            }
                        }
                    }
                }

                // Write out the line if it hasn't been canceled.
                if(newLine != null) {
                    writer.write(newLine + "\n");
                }
            }

            // Write out any other properties in the object that
            // were not in the template.
            for(Object key : keySet()) {
                String keyS = (String) key;
                if(!writtenKeys.contains(keyS)) {
                    String value = getProperty(keyS) != null ? getProperty(keyS) : "";
                    String newLine = keyS + " = " + value;
                    writer.write(newLine + "\n");
                }
            }

        } catch(Exception e) {

        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch(Exception e) {

                }
            }
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        File testFile = new File("./testpropfile");
        String templ = generateInitialTemplate();
        FormatPreservingFileProperties props = new FormatPreservingFileProperties(testFile, templ, false);
        for(Object key : props.keySet()) {
            System.out.println("[" + key + "]=[" + props.getProperty((String) key) + "]");
        }
        if(testFile.exists()) {
            changeProps(props);
        } else {
            setProps(props);
        }
        props.setProperty("newProp", "Sun");
        props.save();
    }

    private static void changeProps(FormatPreservingFileProperties props) {
        props.setProperty("boolValue", "" + !Boolean.parseBoolean(props.getProperty("boolValue")));
        props.setProperty("intValue", "" + (Integer.parseInt(props.getProperty("intValue")) + 1));
        props.setProperty("doubleValue", "" + (Double.parseDouble(props.getProperty("doubleValue")) + 5.0));
        props.setProperty("strValue", props.getProperty("strValue") + "-X");
    }

    private static void setProps(FormatPreservingFileProperties props) {
        props.setProperty("boolValue", "false");
        props.setProperty("intValue", "1");
        props.setProperty("doubleValue", "20.0");
        props.setProperty("strValue", "Hello");
    }

    private static String generateInitialTemplate() {
        return
            "\n" +
            "# Line Comment 1\n" +
            "    # Line Comment 2\n" +
            "\n" +
            "  boolValue = false  # EOL Comment 1\n" +
            "intValue    = 25     # EOL Comment 2\n" +
            "\n" +
            "invalid line  # EOL Comment 3\n" +
            "\n" +
            "# Line Comment 3\n" +
            "\n" +
            "  doubleValue      = 90.0\n" +
            "\n" +
            "  strValue=hello\n" +
            "\n" +
            "  Abc 123 # prop\n" +
            "  Abc 123 # prop = 4\n" +
            "\n" +
            "x = y\n\n";
    }
}
