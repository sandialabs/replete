package replete.ui.multic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import replete.errors.ExceptionUtil;


public class ConsoleStreamThread extends Thread {
    private Process process;
    private String type;
    private StreamResultCallback out;
    private StreamResultCallback err;

    public ConsoleStreamThread(Process proc, String t, StreamResultCallback o, StreamResultCallback e) {
        process = proc;
        type = t;
        out = o;
        err = e;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            if(type.equals("out")) {
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            }

            int cnt;
            char[] chars = new char[4];
            while((cnt = reader.read(chars)) != -1) {
                String what = new String(chars, 0, cnt);
                if(type.equals("out")) {
                    out.resultArrived(what);
                } else {
                    err.resultArrived(what);
                }
            }

        } catch(Exception e) {
            err.resultArrived(ExceptionUtil.toCompleteString(e, 4));

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    err.resultArrived(ExceptionUtil.toCompleteString(e, 4));
                }
            }
        }
    }
}
