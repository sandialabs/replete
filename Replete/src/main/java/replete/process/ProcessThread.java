package replete.process;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.process.StreamThread.Stream;
import replete.ttc.TransparentThreadContextThread;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class ProcessThread extends TransparentThreadContextThread {


    ////////////
    // FIELDS //
    ////////////

    // Init

    private String[] cmdArray;
    private String[] envParams;
    private File workingDir;

    // Run

    private Process process;
    private StreamThread outThread;
    private StreamThread errThread;
    private BufferedWriter inWriter;
    private boolean sleep;

    private Exception primaryError;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ProcessThread() {
        this((String[]) null);
    }
    public ProcessThread(String command) {
        this(command.split("\\s+"));
    }
    public ProcessThread(String[] cmdArray) {
        setCommand(cmdArray);
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier outArrivedNotifier = new ChangeNotifier(this);
    public void addOutArrivedListener(ChangeListener listener) {
        outArrivedNotifier.addListener(listener);
    }
    private void fireOutArrivedNotifier() {
        outArrivedNotifier.fireStateChanged();
    }
    private ChangeNotifier errArrivedNotifier = new ChangeNotifier(this);
    public void addErrArrivedListener(ChangeListener listener) {
        errArrivedNotifier.addListener(listener);
    }
    private void fireErrArrivedNotifier() {
        errArrivedNotifier.fireStateChanged();
    }
    private ChangeNotifier errorNotifier = new ChangeNotifier(this);
    public void addErrorListener(ChangeListener listener) {
        errorNotifier.addListener(listener);
    }
    private void fireErrorNotifier() {
        errorNotifier.fireStateChanged();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Exception getPrimaryError() {
        return primaryError;
    }

    // Accessors (Computed)

    public int getExitValue() {
        return process.exitValue();
    }
    public String getOutputText() {
        if(outThread == null) {
            return null;
        }
        return outThread.getResult();
    }
    public String getErrorText() {
        if(errThread == null) {
            return null;
        }
        return errThread.getResult();
    }

    // Mutators

    public void setCommand(String command) {
        setCommand(command.split("\\s+"));
    }
    public void setCommand(String[] ca) {
        cmdArray = ca;
    }
    public void setEnvironment(String[] ep) {
        envParams = ep;
    }
    public void setWorkingDir(File d) {
        workingDir = d;
    }


    /////////
    // RUN //
    /////////

    @Override
    public void run() {
        try {
            process = Runtime.getRuntime().exec(cmdArray, envParams, workingDir);

            outThread = new StreamThread(process, Stream.OUT);
            errThread = new StreamThread(process, Stream.ERR);

            outThread.addResultArrivedListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    synchronized(ProcessThread.this) {
                        fireOutArrivedNotifier();
                    }
                }
            });

            errThread.addResultArrivedListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    synchronized(ProcessThread.this) {
                        fireErrArrivedNotifier();
                    }
                }
            });

            outThread.addMgmtErrorListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    error(outThread.getManagementException());
                    destroyProcess();
                }
            });

            errThread.addMgmtErrorListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    error(errThread.getManagementException());
                    destroyProcess();
                }
            });

            outThread.start();
            errThread.start();

            inWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            sleep = true;

            Thread streamWaitThread = new Thread() {
                @Override
                public void run() {
                    try {
                        outThread.join();
                        errThread.join();
                    } catch(InterruptedException e) {
                    }
                    sleep = false;
                    synchronized(ProcessThread.this) {
                        ProcessThread.this.notify();
                    }
                };
            };
            Thread shutdownWaitThread = new Thread() {
                @Override
                public void run() {
                    while(ProcessThread.this.isAlive()) {
                        try {
                            Thread.sleep(500);
                            checkStop();
                        } catch(Exception e) {
                            sleep = false;
                            synchronized(ProcessThread.this) {
                                ProcessThread.this.notify();
                            }
                            break;
                        }
                    }
                };
            };

            streamWaitThread.start();
            shutdownWaitThread.start();

            if(sleep) {
                synchronized(this) {
                    wait();
                }
            }

        } catch(Exception e) {
            error(e);

        } finally {
            destroyProcess();
        }
    }

    private void error(Exception e) {
        synchronized(ProcessThread.this) {
            primaryError = e;
            fireErrorNotifier();
        }
    }

    private void destroyProcess() {
        outThread.interrupt();
        errThread.interrupt();
//        outThread.shutdown();
//        errThread.shutdown();??
        try {
            inWriter.close();
        } catch(IOException e) {
            error(e);
        }
        process.destroy();
    }

    public void sendText(String s) throws IOException {
        inWriter.write(s);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

        final ProcessThread thread = new ProcessThread("cmd.exe /c dir");
//        final ProcessThread thread = new ProcessThread("C:\\Users\\dtrumbo\\Wave\\bin\\wave.bat");
//        thread.setWorkingDir(new File("C:\\Users\\dtrumbo\\Wave\\bin"));
//        thread.setCommand("java ");
        thread.addOutArrivedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println(thread.getOutputText());
            }
        });
        thread.addErrArrivedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println(thread.getErrorText());
            }
        });
        thread.addErrorListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println(thread.getPrimaryError());
            }
        });
//            new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                if(result.isComplete()) {
//                    System.out.println(thread.getOutputText());   ?????
//                    System.out.println(thread.getExitValue());
//                }
//                System.out.println(result);
//            }
//        });
        thread.start();

        JFrame f = new EscapeFrame();
        JButton btn = new JButton("Stop");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                thread.stopContext();
            }
        });
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println(thread.isAlive());
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.println(thread.isAlive());
            }
        });
        Lay.FLtg(f, btn, "size=[400,400],center,visible=true");
    }
}
