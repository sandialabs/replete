package replete.ui.multic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ConsoleThread extends Thread {
    private Process process;
    private Thread tOut;
    private Thread tErr;
    private BufferedWriter stdInWriter;
    private StreamResultCallback mgmt;

    public ConsoleThread(Process proc, StreamResultCallback out,
                         StreamResultCallback err, StreamResultCallback m) {
        process = proc;
        mgmt = m;
        tOut = new ConsoleStreamThread(process, "out", out, err);
        tErr = new ConsoleStreamThread(process, "err", out, err);
        stdInWriter = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
    }

    public void send(String s) {
        try {
            stdInWriter.write(s + "\n");  // Send lines at a time
            stdInWriter.flush();
        } catch(IOException e) {
            mgmt.resultArrived("<Error: " + e.getMessage() + ">\n");
        }
    }

    @Override
    public void run() {

        tOut.start();
        tErr.start();

        try {
            tOut.join();
            tErr.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        try {
            process.getOutputStream().close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            process.getInputStream().close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            process.getErrorStream().close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            process.waitFor();
            mgmt.resultArrived("<Process Terminated: Exit Status=" + process.exitValue() + ">\n");
        } catch(Exception e) {
            e.printStackTrace();
        }

        process.destroy();
    }
}
