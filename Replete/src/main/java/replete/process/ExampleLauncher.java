package replete.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import replete.SoftwareVersion;
import replete.cli.CommandLineParser;
import replete.cli.errors.CommandLineParseException;
import replete.cli.errors.UserRequestedHelpException;
import replete.cli.options.Option;
import replete.io.FileUtil;
import replete.util.DateUtil;
import replete.util.ShutdownUtil;
import replete.util.SystemUtil;
import replete.util.User;

// https://stackoverflow.com/questions/54686/how-to-get-a-list-of-current-open-windows-process-with-java
// https://serverfault.com/questions/323795/display-complete-command-line-including-arguments-for-windows-process
// https://zeroturnaround.com/rebellabs/how-to-deal-with-subprocesses-in-java/

public class ExampleLauncher {

    // Also, ProcessBuilder does not handle quotes in arguments appropriately for Windows!!

    // Windows:
    //   While blocking (waitFor)...
    //     Ctrl+C seems to be working from command prompt
    //        The soft kill signal gets to the underlying process's and shutdown hooks are run and process is ended
    //        Spawned process seems to always end before main process:
    //            **********
    //            For input string: "ProcessId"
    //            17068
    //            Waiting...
    //            Waiting...
    //            Waiting...
    //            Waiting...
    //            -- Debug Shutdown Action --
    //            -- Launcher Shutdown Action --
    //     Hardkill seems to be working from command prompt (clicking red X exit button)
    //       The hard kill signal does result in the underlying process's shutdown hooks NOT GETTING RUN and process is ended.
    //       Both processes are dead by end
    //     Hardkill of main process from Task Manager DOES NOT kill spawned process, does not fire hooks (as expected)
    //     Hardkill of spawned process from TM does not fire hooks (as expected)
    //   Getting process ID for a recently spawned process (not from Java -- not using Java 9 yet -- but from "wmic")
    //   Background seems to work, even if Eclipse gets confused - keeps console "active" (stop button, and not terminated) until spawned process is dead
    //     This means that Eclipse's console is tied not to PROCESS LIFECYCLE but rather to I/O STREAMS LIFECYCLE
    //     The main process dies, but the spawned process does continue until it dies
    //     on Command prompt with --background provided, process is spawned and control immediately returned to prompt (launcher process dies)
    //     Due to inheritIO being called, when the spawned process exits it still prints to the same command prompt
    //       because the stream got tied to that window
    //   When this is wrapped in .bat file....
    //     The Ctrl+C / Hard kill seems to work just fine from the command-line if executing *.bat file from CP and that calls java
    //     Background seems to work just fine
    // Linux:
    //   ps command doesn't seem to have create date (actually it might have this), image name (well it's truncated...), or executable path
    //     but it has PID and full command-line which are the most important things
    //   Ctrl+C seems to be working fine, though the main process seems to end before spawned process: (diff than Win)
    //        Waiting...
    //        Waiting...
    //        Waiting...
    //        -- Launcher Shutdown Action --
    //        -- Debug Shutdown Action --
    //   kill on main proc doesnt kill spawned....
    //   kill on spawned works correctly
    //        Waiting...
    //        -- Debug Shutdown Action --
    //        143
    //        -- Launcher Shutdown Action --
    //   kill -9 on spawned process works correctly
    //     it DOESN'T fire hooks, but then the launcher process does fire its hooks and ends normally
    //        Waiting...
    //        137
    //        -- Launcher Shutdown Action --
    //   Getting process ID for a recently spawned process (not from Java -- not using Java 9 yet -- but from "ps")
    //   Background seems to be working just fine, main process ends immediately, spawned process continues fine until it's done
    //        LINE=dd      ,31341,bash
    //        Spawned Process ID ==> 13900
    //        -- Launcher Shutdown Action --
    //        DebugMain Invoked With Args: [sleep50000, toss, hooktask, 76f7e49a-f35b-412e-8523-6ac6cdad6d36]
    //        *
    //        **
    //        ***
    //        ****
    //        *****
    //        ******
    //        *******
    //        ********
    //        *********
    //        **********
    //        dd@venom:~/proctest/replete-1.1.0$ Exception in thread "main" replete.errors.IntentionalDebugException: DebugMain Exception
    //                at replete.errors.ExceptionUtil.toss(ExceptionUtil.java:153)
    //                at replete.util.DebugAppMain.main(DebugAppMain.java:64)
    //        -- Debug Shutdown Action --
    //   When this is wrapped in a script file...
    //     Ctrl+C seems to work the same, softkill|hardkill on spawned process allows main process to die normally
    //     softkill on main process launched within script lets its hooks run and spawned process CONTINUES
    //     hardkill on main process launched within script stops the script and spawned process CONTINUES
    //        Waiting...
    //        ./script: line 3: 14716 Killed                  java -cp replete-1.1.0/replete-1.1.0.jar replete.process.Launcher --jar=/home/dd/proctest/replete-1.1.0/replete-1.1.0.jar --toss --hooktask --sleep=20000
    //        dd@venom:~/proctest$ Exception in thread "main" replete.errors.IntentionalDebugException: DebugMain Exception
    //                at replete.errors.ExceptionUtil.toss(ExceptionUtil.java:153)
    //                at replete.util.DebugAppMain.main(DebugAppMain.java:64)
    //        -- Debug Shutdown Action --
    //   Background in a script seems to work just as expected, main process exits immediately, script exits immediately, x
    //        Spawned Process ID ==> 14841
    //        -- Launcher Shutdown Action --
    //        DebugMain Invoked With Args: [sleep20000, toss, hooktask, 1533b6d1-5826-4b3d-8f25-6ef3564f0c1b]
    //        *
    //        **
    //        ***
    //        ****
    //        *****
    //        ******
    //        *******
    //        ********
    //        *********
    //        **********
    //        dd@venom:~/proctest$ Exception in thread "main" replete.errors.IntentionalDebugException: DebugMain Exception
    //                at replete.errors.ExceptionUtil.toss(ExceptionUtil.java:153)
    //                at replete.util.DebugAppMain.main(DebugAppMain.java:64)
    //        -- Debug Shutdown Action --

//    @echo off
//    SETLOCAL ENABLEDELAYEDEXPANSION
//    set MY_PATH=%~dp0
//    echo %MY_PATH%
//    java -jar proctest.jar %*

//    #!/bin/bash
//
//    java -cp replete-1.1.0/replete-1.1.0.jar replete.process.Launcher --jar=/home/dd/proctest/replete-1.1.0/replete-1.1.0.jar --toss --hooktask --sleep=20000 --background


// C:\Users\dtrumbo\work\eclipse-alt\Replete\build\deploy\replete-1.1.0\replete-1.1.0.jar
    private static String[] replaceWithTestArgs(String[] args) {
        args = new String[] {
            "--hooktask",
//            "--toss",
            "--background",
            "--sleep=10000",
            "--jar=C:\\Users\\dtrumbo\\work\\eclipse-alt\\Replete\\build\\deploy\\replete-1.1.0\\replete-1.1.0.jar",
        };
        return args;
    }
    public static void main(String[] args) throws Exception {
        if(SoftwareVersion.get().isDevelopment()) {
            args = replaceWithTestArgs(args);
        }

        CommandLineParser parser = new CommandLineParser();
        Option optSleep      = parser.addIntegerOption("sleep");
        Option optToss       = parser.addBooleanOption("toss");
        Option optHookTask   = parser.addBooleanOption("hooktask");
        Option optJarCp      = parser.addPathOption("jar").setMustBeFile(true);
        Option optBackground = parser.addBooleanOption("background");

        try {
            parser.parse(args);
        } catch(CommandLineParseException e) {
            SystemUtil.protectedExit(1);
        } catch(UserRequestedHelpException e) {
            return;
        }

        Integer sleep      = parser.getOptionValue(optSleep);
        boolean toss       = parser.getOptionValue(optToss, false);
        boolean hooktask   = parser.getOptionValue(optHookTask, false);
        boolean background = parser.getOptionValue(optBackground, false);
        File    jarCp      = parser.getOptionValue(optJarCp);

        ShutdownUtil.setEnabled(true);
        ShutdownUtil.addAction(() -> {
            System.out.println("-- Launcher Shutdown Action --");
            FileUtil.writeTextContent(User.getDesktop("shutdown-file-launcher.txt"), DateUtil.toLongString(System.currentTimeMillis()));
        });

        List<String> jargs = new ArrayList<>();
        if(sleep != null) {
            jargs.add("sleep" + sleep);
        }
        if(toss) {
            jargs.add("toss");
        }
        if(hooktask) {
            jargs.add("hooktask");
        }
        UUID uuid = UUID.randomUUID();
        jargs.add(uuid.toString());

        JavaProcessLauncher launcher = new JavaProcessLauncher()
            .setCpPaths(jarCp)
            .setClassName("replete.util.DebugAppMain")
            .setAppArgs(jargs.toArray(new String[0]))
            .setProcessMatcher(
                p -> p.getCommandLine() != null && p.getCommandLine().contains(uuid.toString()))
            .setBackground(background)
        ;

        LaunchResult result = launcher.launch();
        System.out.println("Spawned Process ID ==> " + result.getProcessId() + (result.getProcessId() == null ? " (Exited Too Fast)" : ""));

        if(background && result.getProcessId() != null && result.getProcess().isAlive()) {
            FileUtil.writeTextContent(User.getDesktop("pid.txt"), "" + result.getProcessId());
        }
    }
}
