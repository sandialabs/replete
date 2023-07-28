package replete.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import replete.errors.RuntimeConvertedException;
import replete.util.OsUtil;
import replete.util.User;

// https://stackoverflow.com/questions/931536/how-do-i-launch-a-completely-independent-process-from-a-java-program
//  - inheritIO is a useful ProcessBuilder convenience method that ties the child
//    process's streams to the current process, negating the need to thoroughly
//    consume the child process's stdout and stderr streams.

public class JavaProcessLauncher {


    ////////////
    // FIELDS //
    ////////////

    private String[] nonStdOpts;
    private String[] sysProps;
    private File[]   cpPaths;
    private String   className;         // Used with cpPaths
    private File     jarPath;           // Only either jarPath or cpPaths+className can be used
    private String[] appArgs;
    private File     workingDir;        // Will inherit from current process if null
    private boolean  inheritIo = true;  // Very common behavior to inherit from parent process
    // TODO: Options to consume stdout/stderr if !inheritIo
    private boolean  background;
    private Predicate<OsProcessDescriptor> processMatcher;
    private boolean  debug;
    private File     javaAgentPath;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String[] getNonStdOpts() {
        return nonStdOpts;
    }
    public String[] getSysProps() {
        return sysProps;
    }
    public File[] getCpPaths() {
        return cpPaths;
    }
    public String getClassName() {
        return className;
    }
    public File getJarPath() {
        return jarPath;
    }
    public String[] getAppArgs() {
        return appArgs;
    }
    public File getWorkingDir() {
        return workingDir;
    }
    public boolean isInheritIo() {
        return inheritIo;
    }
    public boolean isBackground() {
        return background;
    }
    public Predicate<OsProcessDescriptor> getProcessMatcher() {
        return processMatcher;
    }
    public boolean isDebug() {
        return debug;
    }
    public File getJavaAgentPath() {
        return javaAgentPath;
    }

    // Mutators

    public JavaProcessLauncher setNonStdOpts(String... nonStdOpts) {
        this.nonStdOpts = nonStdOpts;
        return this;
    }
    public JavaProcessLauncher setSysProps(String... sysProps) {
        this.sysProps = sysProps;
        return this;
    }
    public JavaProcessLauncher setCpPaths(File... cpPaths) {
        this.cpPaths = cpPaths;
        return this;
    }
    public JavaProcessLauncher setClassName(String className) {
        this.className = className;
        return this;
    }
    public JavaProcessLauncher setJarPath(File jarPath) {
        this.jarPath = jarPath;
        return this;
    }
    public JavaProcessLauncher setAppArgs(String... appArgs) {
        this.appArgs = appArgs;
        return this;
    }
    public JavaProcessLauncher setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
        return this;
    }
    public JavaProcessLauncher setInheritIo(boolean inheritIo) {
        this.inheritIo = inheritIo;
        return this;
    }
    public JavaProcessLauncher setBackground(boolean background) {
        this.background = background;
        return this;
    }
    public JavaProcessLauncher setProcessMatcher(Predicate<OsProcessDescriptor> processMatcher) {
        this.processMatcher = processMatcher;
        return this;
    }
    public JavaProcessLauncher setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
    public JavaProcessLauncher setJavaAgentPath(File javaAgentPath) {
        this.javaAgentPath = javaAgentPath;
        return this;
    }


    ////////////
    // LAUNCH //
    ////////////

    public LaunchResult launch() {
        List<String> processArgs = new ArrayList<>();
        processArgs.add("java");    // Could have options for this too: full path, javaw

        if(nonStdOpts != null) {
            for(String nonStdOpt : nonStdOpts) {
                processArgs.add(nonStdOpt);   // Each string should start with "-X"
            }
        }

        if(sysProps != null) {
            for(String sysProp : sysProps) {
                processArgs.add(sysProp);     // Each string should start with "-D" (e.g. "-D<NAME>=<VALUE>")
            }
        }

        if(javaAgentPath != null) {
            processArgs.add("-javaagent:" + javaAgentPath.getAbsolutePath());
        }

        if(cpPaths != null) {
            processArgs.add("-cp");
            String delim = OsUtil.isWindows() ? ";" : ":";
            String joinedPaths = Arrays.stream(cpPaths)
                .map(f -> f.getAbsolutePath())
                .collect(Collectors.joining(delim))
            ;
            processArgs.add(joinedPaths);
            processArgs.add(className);

        } else if(jarPath != null) {
            processArgs.add("-jar");
            processArgs.add(jarPath.getAbsolutePath());
            // Manifest will be checked for entry point class

        } else {
            throw new IllegalArgumentException("Neither classpath paths provided nor JAR path provided.");
        }

        if(appArgs != null) {
            for(String appArg : appArgs) {
                if(OsUtil.isWindows()) {
                    // https://bugs.java.com/view_bug.do?bug_id=7032109
                    // https://bugs.openjdk.java.net/browse/JDK-8131908
                    appArg = appArg.replaceAll("\"", Matcher.quoteReplacement("\\\""));
                }
                processArgs.add(appArg);
            }
        }

        // TODO: The part below could be in a non-Java specific
        // process launcher/process manager.

        if(debug) {
            System.out.println("Java Process Command & Args: " + processArgs.size());
            processArgs.stream().forEach(a -> System.out.println("  [" + a + "]"));
        }

        ProcessBuilder builder = new ProcessBuilder()
            .command(processArgs)
            .directory(workingDir)
        ;
        if(inheritIo) {
            builder.inheritIO();
        }

        LaunchResult result = new LaunchResult();
        result.setCommandTokens(processArgs);     // Can one day separate out token creation so can inspect before launch
        try {
            Process proc = builder.inheritIO().start();
            result.setProcess(proc);
            if(processMatcher != null) {        // This part needed until we switch to Java 9
                List<OsProcessDescriptor> pds = ProcessUtil.getProcessList();
                Optional<OsProcessDescriptor> pd = pds.stream()
                    .filter(processMatcher)
                    .findFirst()
                ;
                if(pd.isPresent()) {
                    Integer pid = pd.get().getProcessId();
                    result.setProcessId(pid);
                    if(debug) {
                        System.out.println("Java Process PID: " + pid);
                    }
                } else {
                    if(debug) {
                        System.out.println("Java Process PID: (None Found)");
                    }
                }
            }
            if(!inheritIo) {
                // Then we must somehow consume all the stdout and stderr stream
                // content so the child process doesn't hang when it writes there.
            }
            if(!background) {
                while(true) {
                    boolean exited = proc.waitFor(500, TimeUnit.MILLISECONDS);
                    if(exited) {
                        if(debug) {
                            System.out.println("Java Process Exit Status: " +
                                proc.exitValue() + (proc.isAlive() ? " (still alive for some reason?)" : ""));
                        }
                        break;
                    }
                }
            }
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
        return result;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws IOException {
        JavaProcessLauncher launcher = new JavaProcessLauncher()
            .setAppArgs("a b", "\"a b\"", "2")           // Testing quote issues on Windows
            .setJarPath(User.getDesktop("debug.jar"))
        ;

        launcher.launch();
//        ProcessBuilder builder = new ProcessBuilder()
//            .command(new String[] {"java", "-jar", "debug.jar", "\\\"a c\\\"", "2"})
//            .directory(User.getDesktop())
//            .inheritIO()
//        ;
//        System.out.println(builder.command());
//        Process p = builder.start();
    }
}
