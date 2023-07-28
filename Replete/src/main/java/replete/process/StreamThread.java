package replete.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;

public class StreamThread extends Thread {


    ///////////
    // ENUMS //
    ///////////

    public enum Stream {
        OUT,
        ERR
    }


    ////////////
    // FIELDS //
    ////////////

    private Process process;
    private BufferedReader reader;
    private Stream stream;
    private StringBuffer buffer;
    private String prevLine;
    private Exception mgmtException;


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected ChangeNotifier resultArrivedNotifier = new ChangeNotifier(this);
    public void addResultArrivedListener(ChangeListener listener) {
        resultArrivedNotifier.addListener(listener);
    }
    protected void fireResultArrivedNotifier() {
        resultArrivedNotifier.fireStateChanged();
    }

    protected ChangeNotifier mgmtErrorNotifier = new ChangeNotifier(this);
    public void addMgmtErrorListener(ChangeListener listener) {
        mgmtErrorNotifier.addListener(listener);
    }
    protected void fireMgmtErrorNotifier() {
        mgmtErrorNotifier.fireStateChanged();
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StreamThread(Process p, Stream s) {
        process = p;
        stream = s;
        buffer = new StringBuffer();
    }


    /////////
    // RUN //
    /////////
private InputStreamReader xreader;
    @Override
    public void run() {
        try {
            if(stream == Stream.OUT) {
                xreader = new InputStreamReader(process.getInputStream());
                reader = new BufferedReader(xreader);
            } else {
                xreader = new InputStreamReader(process.getErrorStream());
                reader = new BufferedReader(xreader);
            }

            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                prevLine = line;
                fireResultArrivedNotifier();
            }

        } catch(Exception e) {
            mgmtException = e;
            fireMgmtErrorNotifier();

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    mgmtException = e;
                    fireMgmtErrorNotifier();
                }
            }
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public String getResult() {
        return buffer.toString();
    }
    public Exception getManagementException() {
        return mgmtException;
    }
    public boolean isError() {
        return mgmtException != null;
    }
    public String getPreviousLine() {
        return prevLine;
    }


    //////////
    // MISC //
    //////////

    /*????????
    public void shutdown() {
        if(reader != null) {
            try {
                if(stream == Stream.OUT) {
                    xreader.close();
//                    process.getInputStream().close();
                } else {
                    xreader.close();
//                    process.getErrorStream().close();
                }
            } catch(Exception e) {
                mgmtException = e;
                fireMgmtErrorNotifier();
           }
        }
    }
    */
}
