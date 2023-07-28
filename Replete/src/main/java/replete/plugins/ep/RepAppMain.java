package replete.plugins.ep;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import replete.SoftwareVersion;
import replete.cli.CommandLineParser;
import replete.cli.argfile.ArgFileConfig;
import replete.cli.errors.CommandLineParseException;
import replete.cli.errors.UserRequestedHelpException;
import replete.cli.options.Option;
import replete.collections.Pair;
import replete.errors.ExceptionUtil;
import replete.plugins.PluginInitializationParams;
import replete.plugins.PluginInitializationParamsGroup;
import replete.plugins.PluginInitializationResults;
import replete.plugins.PluginManager;
import replete.util.AppMain;
import replete.util.ReflectionUtil;
import replete.util.SystemExitProtectionContext;
import replete.util.SystemExitRequestedException;
import replete.util.SystemUtil;

// This application supports the "--config" command line option so users
// can provide a file with all of the other command line arguments.  This
// kind of feature is traditionally implemented by creating a simple
// INI-type format (or JSON or XML if something more complex is desired)
// and parsing that file within the app itself.  However, this was instead
// implemented in Replete's command line parser.  Thus, the file provided
// can simply contain the other command line arguments that would otherwise
// have to be provided directly to this application.  This makes the feature
// a little less targeted directly to this app (there's no custom property
// file format yet) but now ANY application that ever uses the command line
// parser component can leverage having a file that contains the actual
// command line arguments.  This will serve as a quick, easy option for
// developers who want this capability but at any time in the future a
// more specific, local properties file feature can be implemented if the
// centralized command line argument feature is not enough.

public class RepAppMain extends AppMain {


    ////////////
    // FIELDS //
    ////////////

    private static boolean verbose = false;   // Prints additional detail (doesn't affect errors)
    private static boolean quiet = false;     // Prevents printing of errors)


    //////////
    // MAIN //
    //////////

    private static String[] replaceWithTestArgs(String[] args) {
        args = new String[] {
            "--config=C:\\Users\\dtrumbo\\Desktop\\rep-args.txt",
//            "-?",
            "-v",
//            "--config=C:\\Users\\dtrumbo\\work\\eclipse-main\\Replete\\src\\plugins\\replete\\plugins\\ep\\test.txt",
//            "-q",
//            "--loadclassname=asfas",
//            "--loadjardir=C:\\Users\\dtrumbo\\work\\eclipse-main\\AvondaleBundler\\build\\deploy\\avondale-3.0.0\\bin\\lib",
            "--ignoreclass=org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin",
//            "--loadclassdir=C:\\Users\\dtrumbo\\work\\eclipse-main\\Avondale\\target\\classes",
            "--dynmain=gov.sandia.webcomms.http.HttpAppMain", "-?"    // Valid
//            "--dynmain=xgov.sandia.webcomms.http.HttpAppMain", "-?"   // Doesn't exist
//            "--dynmain=replete.SoftwareVersion", "-?"     // Exists but no main
//            "--dynmain=replete.util.DebugMain", "toss"       // Exists, main, but throws exception
//            "--dynmain=replete.util.DebugMain", "aaa", "--aaa", "--a", "-aaaaaaa",     // Valid
//            "--dynmain=replete.util.DebugMain"
        };
        return args;
    }

    public static void main(String[] args) {
        if(SoftwareVersion.get().isDevelopment()) {
            args = replaceWithTestArgs(args);
        }

        CommandLineParser parser = new CommandLineParser(0);
        parser.setCommandName("replete-ep");
        parser.setArgFileConfig(
            new ArgFileConfig()
                .setOptionName("config")
                .setPathsRelToFileOptionName("configpathsrel")
        );
        parser.setPrintParamDelimiters(true);
//      parser.setAddlUsageMessage(addlMessage, 2);

        parser.addDefaultHelpOption();
        Option optVerbose       = parser.addBooleanOption('v', "verbose");
        Option optQuiet         = parser.addBooleanOption('q', "quiet");
        Option optLoadClassName = parser.addStringOption("loadclassname");
        Option optLoadJarFile   = parser.addPathOption("loadjarfile").setMustBeFile(true);
        Option optLoadJarDir    = parser.addPathOption("loadjardir").setMustBeDirectory(true);
        Option optLoadClassDir  = parser.addPathOption("loadclassdir").setMustBeDirectory(true);
        Option optIgnoreClasses = parser.addStringOption("ignoreclass");
        Option optDynMainClass  = parser.addStringOption("dynmain", true);

        optVerbose.setHelpDescription("xxx");
        optQuiet.setHelpDescription("xxx");

        optLoadClassName.setHelpDescription("xxx");
        optLoadClassName.setHelpParamName("CLASS");

        optLoadJarFile.setHelpDescription("xxx");
        optLoadJarFile.setHelpParamName("FILE");

        optLoadJarDir.setHelpDescription("xxx");
        optLoadJarDir.setHelpParamName("DIR");

        optLoadClassDir.setHelpDescription("xxx");
        optLoadClassDir.setHelpParamName("DIR");

        optDynMainClass.setHelpDescription(
            "A class whose main method is to be executed instead of the standard client or " +
            "server applications.  All arguments that follow this option will " +
            "be passed to the main method as its command-line arguments.");
        optDynMainClass.setHelpParamName("CLASS");
        optDynMainClass.setAllowMulti(false);
        optDynMainClass.setHasXArgs(true); // Allows argument to have arbitrary, un-parsed arguments on command-line

        optIgnoreClasses.setHelpDescription("xxx");
        optIgnoreClasses.setHelpParamName("CLASS");

        try {
            parser.parse(args);
        } catch(CommandLineParseException e) {
            SystemUtil.protectedExit(1);
        } catch(UserRequestedHelpException e) {
            return;
        }

        verbose                 = parser.getOptionValue(optVerbose, false);
        quiet                   = parser.getOptionValue(optQuiet, false);
        String[] ignoreClasses  = parser.getOptionValues(optIgnoreClasses, new String[0]);
        String   dynMainClass   = parser.getOptionValue(optDynMainClass);
        String[] dynMainArgs    = parser.getXArgsArray(optDynMainClass);

        out("==========================================");
        out("===== Replete Extensibility Platform =====");
        out("==========================================");
        out("<Version: " + SoftwareVersion.get().getFullVersionString() + ">");

        for(String ignorePluginClass : ignoreClasses) {
            PluginManager.addIgnoreClass(ignorePluginClass);
        }

        out("<-------- Initializing Plug-ins --------->");

        initializePlugins(parser, optLoadClassName, optLoadJarFile, optLoadJarDir, optLoadClassDir);

        out("<------------- Dynamic Main ------------->");

        out("Dynamic Main Class: " + dynMainClass);
        out("Dynamic Main Args:  " + (dynMainArgs == null ? "[]" : Arrays.toString(dynMainArgs)));

        launchDynamicMain(dynMainClass, dynMainArgs);
    }

    private static void initializePlugins(CommandLineParser parser, Option optLoadClassName,
                                          Option optLoadJarFile, Option optLoadJarDir,
                                          Option optLoadClassDir) {

        PluginInitializationParams params = new PluginInitializationParams();

        for(Pair<Option<?>, Object> parsedArg : parser.getOrderedParsedArguments()) {
            Option option = parsedArg.getValue1();
            if(option == optLoadClassName) {
                String value = (String) parsedArg.getValue2();
                params.add(new PluginInitializationParamsGroup().setClassNames(value));

            } else if(option == optLoadJarFile) {
                File path = (File) parsedArg.getValue2();
                params.add(new PluginInitializationParamsGroup().setJarFiles(path));

            } else if(option == optLoadJarDir) {
                File path = (File) parsedArg.getValue2();
                params.add(new PluginInitializationParamsGroup().setJarDirs(path));

            } else if(option == optLoadClassDir) {
                File path = (File) parsedArg.getValue2();
                params.add(new PluginInitializationParamsGroup().setClassDirs(path));
            }
        }

        for(PluginInitializationParamsGroup paramsGroup : params.getGroups()) {
            paramsGroup.setShowProgress(verbose);
        }

        PluginInitializationResults results = PluginManager.initialize(params);

        // If verbose is enabled and there is any message type whatsoever,
        // print the validation context (just the messages, not the empty
        // frames).
        if(verbose) {
            if(results.getValidationContext().hasMessage()) {
                System.out.println("Validation Results:");
                out(results.getValidationContext().toString());
            }

        // Else if verbose is not desired but also quiet hasn't been
        // indicated, and there are errors, then print the validation
        // context, which will include just messages, error or otherwise.
        } else if(!quiet) {
            if(results.getValidationContext().hasError()) {
                System.out.println("Validation Results:");
                err(results.getValidationContext().toString());
            }
        }

        out("Summary Results: " + results);
        List<URL> cpPaths = SystemUtil.getClassPathUrls();
        out("Post Plugin Initialization Classpath Paths:");
        for(URL path : cpPaths) {
            out("  " + path);
        }
    }

    private static void launchDynamicMain(String dynMainClass, String[] dynMainArgs) {
        Class<?> clazz;
        try {
            clazz = Class.forName(dynMainClass);
        } catch(Exception e) {
            err("[Invalid Dynamic Main Class]");
            err(ExceptionUtil.toCompleteString(e, 4).trim());
            SystemUtil.protectedExit(1);
            return;             // Only needed for compilation reasons
        }

        Method m;
        try {
            m = ReflectionUtil.getMethod(clazz, "main", String[].class);
        } catch(Exception e) {
            err("[Invalid Dynamic Main Class: Does not contain a 'main' method]");
            err(ExceptionUtil.toCompleteString(e, 4).trim());
            SystemUtil.protectedExit(2);
            return;             // Only needed for compilation reasons
        }

        out("v____________ Executing Main ____________v");

        Exception dynMainEx = null;
        SystemExitProtectionContext context = SystemUtil.addSystemExitProtectionContext();
        try {
            if(dynMainArgs == null) {
                m.invoke(null, (Object) new String[0]);
            } else {
                m.invoke(null, (Object) dynMainArgs);
            }

        } catch(Exception e) {
            if(e instanceof InvocationTargetException &&
                    ((InvocationTargetException) e).getCause() instanceof SystemExitRequestedException) {
                // context.getStatus() will NOT be null at this point.
                // Cannot proceed with next SystemUtil.protectedExit
                // until we're out of the current main invocation's
                // context.

            } else {
                dynMainEx = e;     // Save this exception for later processing.
            }                      // We need to leave the SEP context first.

        } finally {
            SystemUtil.removeSystemExitProtectionContext();
        }

        // If context.getStatus() is not null, then a
        // SystemExitRequestedException was thrown earlier.
        // Now that we're out of that main's SEP context,
        // we can now exit using the same infrastructure.
        Integer status = context.getStatus();
        if(status != null) {
            err("[Dynamic Main Invocation Error] Status Code: " + status);
            SystemUtil.protectedExit(status);    // The status code here of the wrapped main might always conflict with
                                                 // the RepAppMain's own status codes, but that's not a big deal

        // If the dynamic main threw an exception we need
        // to wait until we're out of its SEP context so
        // we can also use the same infrastructure (call
        // SystemUtil.protectedExit).
        } else if(dynMainEx != null) {
            err("[Dynamic Main Invocation Error] Exception:");
            err(ExceptionUtil.toCompleteString(dynMainEx, 4).trim());
            SystemUtil.protectedExit(3);

        // Confirm that the status code will be zero if verbose mode enabled.
        } else {
            out("[Dynamic Main Invocation Complete] Status Code: 0");
        }
    }

    private static void out(String s) {
        if(verbose) {
            System.out.println(s);
        }
    }
    private static void err(String s) {
        if(!quiet) {
            System.out.println(s);     // Don't use stderr so all output is guaranteed sequential
        }
    }
}
