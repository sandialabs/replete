package replete.util;

import java.util.Properties;


public class ManagedStreamUtil {

    private static ManagedStreamUtil managedStreamUtilSingleton;
    private ManagedStream outStream;
    private ManagedStream errStream;
    public static final String ENABLED_PROPERTY = "replete.console.out.enabled";
    public static final String TIMESTAMP_PROPERTY = "replete.console.out.timestamp";
    public static final String TRACE_PROPERTY = "replete.console.out.trace";
    public static final String ERR_ENABLED_PROPERTY = "replete.console.err.enabled";
    public static final String ERR_TIMESTAMP_PROPERTY = "replete.console.err.timestamp";
    public static final String ERR_TRACE_PROPERTY = "replete.console.err.trace";

    private ManagedStreamUtil() {

    }

    public static ManagedStreamUtil getStreamManager() {
        if(managedStreamUtilSingleton == null) {
            managedStreamUtilSingleton = new ManagedStreamUtil();
        }
        return managedStreamUtilSingleton;
    }

    public ManagedStreamUtil initialize() {
        ManagedStreamParams outProps = new ManagedStreamParams();
        ManagedStreamParams errProps = new ManagedStreamParams();
        Properties system_props = System.getProperties();
        boolean outputOn = Boolean.parseBoolean(system_props.getProperty(ENABLED_PROPERTY, "true"));
        boolean timeStampOn = Boolean.parseBoolean(system_props.getProperty(TIMESTAMP_PROPERTY, "false"));
        boolean traceOn = Boolean.parseBoolean(system_props.getProperty(TRACE_PROPERTY, "false"));
        boolean errOutputOn = Boolean.parseBoolean(system_props.getProperty(ERR_ENABLED_PROPERTY, "true"));
        boolean errTimeStampOn = Boolean.parseBoolean(system_props.getProperty(ERR_TIMESTAMP_PROPERTY, "false"));
        boolean errTraceOn = Boolean.parseBoolean(system_props.getProperty(ERR_TRACE_PROPERTY, "false"));

        outProps.setOutputOn(outputOn);
        outProps.setTimeStampOn(timeStampOn);
        outProps.setTraceOn(traceOn);
        errProps.setOutputOn(errOutputOn);
        errProps.setTimeStampOn(errTimeStampOn);
        errProps.setTraceOn(errTraceOn);

        outStream = new ManagedStream(System.out, outProps);
        errStream = new ManagedStream(System.err, errProps);
        System.setOut(outStream);
        System.setErr(errStream);

        return this;
    }

    public ManagedStreamUtil setOutParams(ManagedStreamParams params) {
        outStream.setParams(params);
        return this;
    }

    public ManagedStreamUtil setErrParams(ManagedStreamParams params) {
        errStream.setParams(params);
        return this;
    }

    public static void main(String[] args) {
        ManagedStreamUtil mstream = getStreamManager();
        mstream.initialize();
        ManagedStreamParams params = new ManagedStreamParams();
        params.setTimeStampOn(true);
        mstream.setOutParams(params);

        System.out.print("test\n");
        System.out.println("test2");
        params.setTraceOn(true);
        mstream.setOutParams(params);
        System.out.print(4);
        System.out.print(5);
        System.out.println();
        System.out.print("This \n is a multiline \n string");
        params.setTraceOn(false);
        mstream.setOutParams(params);
        System.out.printf("This is a number, %d\n", 4);
        System.out.println(12);
    }

}
