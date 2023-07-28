package replete.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import replete.text.StringUtil;
import replete.threads.ThreadUtil;
import replete.util.OsUtil;

public class ProcessUtil {
    // TODO: get both?
    public static String[] getOutput(String command) {
        return getOutput(command.split("\\s+"));
    }
    public static String[] getOutput(String[] cmdArray) {
        try {
            Process proc = Runtime.getRuntime().exec(cmdArray);
            BufferedReader reader = null;
            List<String> lineList = new ArrayList<>();
            try {
                reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    lineList.add(line);
                }
            } catch(Exception e) {
                throw e;
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch(Exception e) {
                        throw e;
                    }
                }
            }
            return lineList.toArray(new String[0]);
        } catch(Exception e) {
            throw new RuntimeException("Could not get output from command.", e);
        }
    }
    public static String[] getError(String command) {
        return getError(command.split("\\s+"));
    }
    public static String[] getError(String[] cmdArray) {
        try {
            Process proc = Runtime.getRuntime().exec(cmdArray);
            BufferedReader reader = null;
            List<String> lineList = new ArrayList<>();
            try {
                reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    lineList.add(line);
                }
            } catch(Exception e) {
                throw e;
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch(Exception e) {
                        throw e;
                    }
                }
            }
            return lineList.toArray(new String[0]);
        } catch(Exception e) {
            throw new RuntimeException("Could not get output from command.", e);
        }
    }

    public static List<OsProcessDescriptor> getProcessList() throws IOException, InterruptedException {
        List<OsProcessDescriptor> pds = new ArrayList<>();
        if(OsUtil.isWindows()) {

            // Unfortunately the Windows "tasklist" command doesn't have an
            // option to provide the full process's command-line so we need
            // to use "wmic" instead.  This tool should be available on all
            // versions of Windows.
            // https://serverfault.com/questions/323795/display-complete-command-line-including-arguments-for-windows-process
            // http://www.pearsonitcertification.com/articles/article.aspx?p=1700427&seqNum=4
            // https://msdn.microsoft.com/en-us/library/aa394372(v=vs.85).aspx
            // https://blogs.technet.microsoft.com/askperf/2012/02/17/useful-wmic-queries/
            String[] wmicCmdLine = {
                "wmic",
                "path",
                "win32_process",
                "get",
                "commandline,creationDate,executablepath,name,processId",
                "/format:csv"
            };
            Process process = new ProcessBuilder(wmicCmdLine).start();
            Thread readThread = new Thread(() -> {
                try(BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while((line = reader.readLine()) != null) {
                        if(!line.isEmpty() && !line.startsWith("Node,CommandLine")) {
                            try {
                                // Regardless of the field order specified in the command,
                                // the output always comes out like this:
                                // MACHINE,COMMAND_LINE,CREATION_DATE,EXECUTABLE_PATH,IMAGE_NAME,PID
                                int firstComma   = line.indexOf(',');
                                int pidComma     = line.lastIndexOf(',');
                                int imageComma   = line.lastIndexOf(',', pidComma - 1);
                                int exePathComma = line.lastIndexOf(',', imageComma - 1);
                                int dateComma    = line.lastIndexOf(',', exePathComma - 1);

                                OsProcessDescriptor pd = new OsProcessDescriptor();
                                pd.setCommandLine(StringUtil.forceBlankNull(line.substring(firstComma + 1, dateComma)));
                                pd.setCreationDate(StringUtil.forceBlankNull(line.substring(dateComma + 1, exePathComma)));
                                pd.setExecutablePath(StringUtil.forceBlankNull(line.substring(exePathComma + 1, imageComma)));
                                pd.setImageName(StringUtil.forceBlankNull(line.substring(imageComma + 1, pidComma)));
                                pd.setProcessId(Integer.parseInt(line.substring(pidComma + 1)));
                                pds.add(pd);
                            } catch(Exception e) {
                                System.err.println(e.getMessage());
                            }

                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            });
            readThread.start();
            process.waitFor();
            ThreadUtil.join(readThread);

        } else if(OsUtil.isLinux()) {
            String[] psCmdLine = {
                "ps",
                "-eo",
                "%U,%p,%a",      // User, PID, Command-Line right now
                "-w",            // Not sure if this is necessary yet
                "--no-headers",  // Suppresses "USER    ,  PID,COMMAND"
            };
            Process process = new ProcessBuilder(psCmdLine).start();
            Thread readThread = new Thread(() -> {
                try(BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while((line = reader.readLine()) != null) {
                        if(!line.isEmpty()) {
                            try {
                                int firstComma = line.indexOf(',');
                                int clComma = line.indexOf(',', firstComma + 1);
                                OsProcessDescriptor pd = new OsProcessDescriptor();
                                pd.setCommandLine(StringUtil.forceBlankNull(line.substring(clComma + 1).trim()));
                                pd.setProcessId(Integer.parseInt(line.substring(firstComma + 1, clComma).trim()));
                                pds.add(pd);
                            } catch(Exception e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            });
            readThread.start();
            process.waitFor();
            ThreadUtil.join(readThread);
        } else {
            // Mac: some slight variation of 'ps'
        }
        return pds;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws IOException, InterruptedException {
//        String[] lines = getOutput("hostname");
//        System.out.println(Arrays.toString(lines));

        List<OsProcessDescriptor> pds = ProcessUtil.getProcessList();
        for(OsProcessDescriptor pd : pds) {
            System.out.println(pd);
        }
    }
}
