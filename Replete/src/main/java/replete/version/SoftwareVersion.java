package replete.version;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import replete.text.StringLib;
import replete.text.StringUtil;
import replete.util.JarUtil;

public abstract class SoftwareVersion {


    ////////////
    // FIELDS //
    ////////////

    private String version;
    private String timestamp;
    private String label;       // Can be null (means no label supplied)
    private String branch;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SoftwareVersion() {
        try(InputStream is = getClass().getResourceAsStream("software.version")) {
            if(is == null) {
                setError();
            } else {
                try {
                    Properties props = new Properties();
                    props.load(is);
                    version   = (String) props.getOrDefault("version", StringLib.UNKNOWN);
                    timestamp = (String) props.getOrDefault("timestamp", StringLib.UNKNOWN);
                    branch    = (String) props.getOrDefault("svn_branch", StringLib.UNKNOWN);
                    if(branch.trim().startsWith("${")) {
                        branch = StringLib.UNKNOWN;
                    }
                } catch(IOException e) {
                    setError();
                }
            }
        } catch(IOException e) {
            setError();
        }
    }

    private void setError() {
        version = StringLib.UNKNOWN;
        timestamp = StringLib.UNKNOWN;
        branch = StringLib.UNKNOWN;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getVersion() {
        return version;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public String getLabel() {
        return label;
    }
    public String getBranch() {
        return branch;
    }

    // Mutators

    public void setLabel(String label) {
        this.label = label;
    }


    ///////////////////////
    // COMPOSITE VERSION //
    ///////////////////////

    public String getFullVersionString() {
        String v = version + " (" + timestamp + ")";
        if(!StringUtil.isBlank(label)) {
            v += " [" + label + "]";
        }
        if(isDevelopment()) {
            v += " {Development}";
        }
        if(branch != null) {
            v += " <" + branch + ">";
        }
        return v;
    }


    //////////
    // MISC //
    //////////

    public boolean isDevelopment() {        // a.k.a. isNotPackaged(), isRunningInIde()
        return JarUtil.getEnclosingJarFile() == null;
    }
    public boolean equalsIgnoreLabel(SoftwareVersion other) {
        return equalsIgnoreLabel(other);
    }
    public boolean equalsIgnoreLabel(String otherFullVersionString) {
        String v1 = getFullVersionString();
        String v2 = otherFullVersionString;
        int p1 = v1.indexOf(')');
        int p2 = v2.indexOf(')');
        return v1.substring(0, p1 + 1).equals(v2.substring(0, p2 + 1));
    }
    public void print() {
        System.out.println("version=[" + version + "]");
        System.out.println("timestamp=[" + timestamp + "]");
        System.out.println("label=[" + label + "]");
        System.out.println("branch=[" + branch + "]");
        System.out.println("isDev=[" + isDevelopment() + "]");
        System.out.println("full=[" + getFullVersionString() + "]");
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws IOException {
        try(InputStream is = replete.SoftwareVersion.class.getResourceAsStream("software.version")) {
            if(is == null) {
                System.out.println("ERROR==null");
            } else {
                try {
                    Properties props = new Properties();
                    props.load(is);
                    System.out.println(props);
                } catch(IOException e) {
                    System.out.println("ERROR");
                }
            }
        }
    }
}
