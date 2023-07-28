package replete.xstream;

import replete.util.DateUtil;

/**
 * @author Derek Trumbo
 */

public class SerializationMetadata {


    ////////////
    // FIELDS //
    ////////////

    protected String appName;
    protected String appVersion;
    protected String serializationDate;
    protected String user;
    protected String serializationLib;
    protected String serializationLibVersion;
    protected String targetObjectClass;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SerializationMetadata() {
        // Good to use as a builder.
    }

    public SerializationMetadata(Object o) {
        setTargetObject(o);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getAppName() {
        return appName;
    }
    public String getAppVersion() {
        return appVersion;
    }
    public String getSerializationDate() {
        return serializationDate;
    }
    public String getUser() {
        return user;
    }
    public String getSerializationLib() {
        return serializationLib;
    }
    public String getSerializationLibVersion() {
        return serializationLibVersion;
    }
    public String getTargetObjectClass() {
        return targetObjectClass;
    }

    // Mutators

    public SerializationMetadata setAppName(String appName) {
        this.appName = appName;
        return this;
    }
    public SerializationMetadata setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }
    public SerializationMetadata setSerializationDate(String serializationDate) {
        this.serializationDate = serializationDate;
        return this;
    }
    public SerializationMetadata setUser(String user) {
        this.user = user;
        return this;
    }
    public SerializationMetadata setSerializationLib(String serializationLib) {
        this.serializationLib = serializationLib;
        return this;
    }
    public SerializationMetadata setSerializationLibVersion(String serializationLibVersion) {
        this.serializationLibVersion = serializationLibVersion;
        return this;
    }
    public SerializationMetadata setTargetObjectClass(String targetObjectClass) {
        this.targetObjectClass = targetObjectClass;
        return this;
    }

    // Mutators (Compound)

    public SerializationMetadata setTargetObject(Object o) {
        appName = System.getProperty("program.name");
        appVersion = System.getProperty("program.version");

        if(appName == null || appName.equals("")) {
            appName = "<unknown>";
        }

        if(appVersion == null || appVersion.equals("")) {
            appName = "<unknown>";
        }

        serializationDate = DateUtil.toLongString(System.currentTimeMillis());
        user = System.getProperty("user.name");

        serializationLib = "XStream";
        serializationLibVersion = "xstream-1.4.3/xpp3_min-1.1.4c";

        targetObjectClass = o.getClass().getName();
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SerializationMetadata [appName=");
        builder.append(appName);
        builder.append(", appVersion=");
        builder.append(appVersion);
        builder.append(", serializationDate=");
        builder.append(serializationDate);
        builder.append(", user=");
        builder.append(user);
        builder.append(", serializationLib=");
        builder.append(serializationLib);
        builder.append(", serializationLibVersion=");
        builder.append(serializationLibVersion);
        builder.append(", targetObjectClass=");
        builder.append(targetObjectClass);
        builder.append("]");
        return builder.toString();
    }
}
