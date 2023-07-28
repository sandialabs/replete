package replete.ui.multic;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import replete.errors.ExceptionUtil;
import replete.ui.button.ToggleIconButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.text.RTextPane;


public class ConsolePanel extends JPanel {
    private static Style redStyle;
    private static Style mgmtStyle;

    private ConsoleThread cThread;
    private Process process;
    private RTextPane txtOutput;
    private JButton btnStop;
    private JButton btnRestart;
    private JButton btnClear;
    private ToggleIconButton btnPinOutput;
    private String label;
    private String command;
    private File workingDir;
    private String input = "";
    private boolean resetInput;
    private boolean pinOutput;

    public JTextPane getTxtOutput() {
        return txtOutput;
    }

    public boolean isProcessRunning() {
        if(process != null) {
            try {
                process.exitValue();
                return false;
            } catch(IllegalThreadStateException e) {
                return true;
            }
        }
        return false;
    }

    public void stopProcess() {
        if(process != null) {
            process.destroy();
            process = null;
            btnStop.setEnabled(false);
            printMgmt("<Process Stop Requested>\n");
            printMgmt("<*Any grandchild processes spawned were not stopped (Java behavior)>\n");
            //Java has no ability to kill grandchild processes.
            // In other words, this stop button may not have done what
            // you want it to.  Check your running processes via your OS to verify.
            btnRestart.setEnabled(true);
        }
    }

    public ConsolePanel(String lbl, String cmd, File wd, boolean topLabels, boolean fixedWidth) {
        label = lbl;
        command = cmd;
        workingDir = wd;
        txtOutput = new RTextPane();
        addStylesToDocument(txtOutput);
        txtOutput.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if(isProcessRunning() && (e.getKeyChar() > 31 || e.getKeyChar() == 10)) {
                    synchronized(this) {
                        if(resetInput) {
                            txtOutput.append(input, Color.green.darker());
                            resetInput = false;
                        }
                        txtOutput.append("" + e.getKeyChar(), Color.green.darker());
                        if(e.getKeyChar() != 10) {
                            input += e.getKeyChar();
                        }
                    }
                }

                // Don't let the text pane itself append the typed char.
                if(e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                    e.consume();
                }
            }
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                    e.consume();
                }
            }
            public void keyPressed(KeyEvent e) {
                if(isProcessRunning()) {
                    txtOutput.setCaretPosition(txtOutput.getText().length());
                    if(e.getKeyCode() == 8) {
                        synchronized(this) {
                            if(input.length() > 0) {
                                if(resetInput) {
                                    txtOutput.append(input, Color.green.darker());
                                    resetInput = false;
                                }
                                input = input.substring(0, input.length() - 1);
                                try {
                                    txtOutput.getDocument().remove(txtOutput.getCaretPosition() - 1, 1);
                                } catch(BadLocationException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    } else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        // TODO: sometimes the NEW LINE typed for cat input
                        // gets manifested after the cat's output is returned
                        // the new line should manifest before more output
                        // is allowed to be shown.  -- maybe a fluke?
                        cThread.send(input.trim());
                        input = "";
                    }
                }
                if(e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                    e.consume();
                }
            }
        });

        btnStop = Lay.btn(
            CommonConcepts.STOP, 2, "ttt=Stop-Process,enabled=false",
            (ActionListener) e -> stopProcess()
        );

        btnRestart = Lay.btn(
            CommonConcepts.RESTART, 2, "ttt=Restart-Process,enabled=false",
            (ActionListener) e -> {
                if(process == null) {
                    startProcess();
                }
            }
        );

        btnClear = Lay.btn(
            CommonConcepts.CLEAR, 2, "ttt=Clear-Console", (ActionListener) e -> {
                synchronized(this) {
                    txtOutput.setText("");
                }
            }
        );

        btnPinOutput = new ToggleIconButton(ImageLib.get(MultiConsolesImageModel.PIN));
        btnPinOutput.setToolTipText("Pin Scroll To Bottom On Change");
        btnPinOutput.addActionListener(e -> {
            synchronized(this) {
                pinOutput = btnPinOutput.isSelected();
                if(!pinOutput) {
                    int l = txtOutput.getDocument().getLength();
                    if(l != 0) {
                        txtOutput.setCaretPosition(l - 1);
                    }
                }
            }
        });
        btnPinOutput.setSelected(true);
        pinOutput = true;

        changeLabel(topLabels);
        changeFont(fixedWidth);
    }

    public void startProcess() {
        try {
            process = Runtime.getRuntime().exec(command, null, workingDir);
            btnStop.setEnabled(true);
            btnRestart.setEnabled(false);
            cThread = new ConsoleThread(process,
                new StreamResultCallback() {
                    public void resultArrived(String msg) {
                        printOut(msg);
                    }
                },
                new StreamResultCallback() {
                    public void resultArrived(String msg) {
                        printErr(msg);
                    }
                },
                new StreamResultCallback() {
                    public void resultArrived(String msg) {
                        if(msg.contains("Terminated")) {
                            process = null;
                            btnStop.setEnabled(false);
                            btnRestart.setEnabled(true);
                        }
                        printMgmt(msg);
                    }
                });
            cThread.start();
        } catch(IOException e) {
            printErr(ExceptionUtil.toCompleteString(e, 4));
        }
    }

    private void addStylesToDocument(JTextPane txt) {
        StyledDocument doc = txt.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        redStyle = doc.addStyle("red", def);
        StyleConstants.setForeground(redStyle, Color.red);
        mgmtStyle = doc.addStyle("green", def);
        StyleConstants.setForeground(mgmtStyle, Color.blue);
    }

    public void printOut(String str) {
        print(str, null);
    }
    public void printErr(String str) {
        print(str, redStyle);
    }
    public void printMgmt(String str) {
        print(str, mgmtStyle);
    }

    private void print(final String str, final Style style) {
        if(EventQueue.isDispatchThread()) {
            printInner(str, style);
        } else {
            EventQueue.invokeLater(() -> printInner(str, style));
        }
    }

    private void printInner(String str, Style style) {
        try {
            synchronized(this) {
                txtOutput.getDocument().insertString(txtOutput.getDocument().getLength(), str, style);
                resetInput = true;
                if(pinOutput) {
                    System.out.println(txtOutput.getBounds());
                    txtOutput.scrollRectToVisible(new Rectangle(10000, 10000));
                    txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
                }
            }
        } catch(Exception e) {}
    }

    public void changeFont(boolean fixedWidth) {
        String name = fixedWidth ? "Courier New" : "Arial";
        txtOutput.setFont(new Font(name, txtOutput.getFont().getStyle(), txtOutput.getFont().getSize()));
    }

    public void changeLabel(boolean topLabels) {
        removeAll();
        if(topLabels) {
            Lay.BLtg(this,
                "N", Lay.FL("L",
                    Lay.lb(label, "eb=2l"),
                    btnStop,
                    btnRestart,
                    btnClear,
                    btnPinOutput,
                    "hgap=5,vgap=5"
                ),
                "C", Lay.p(Lay.sp(txtOutput), "eb=5l"),
                "bg=blue"
            );
        } else {
            Lay.BLtg(this,
                "W", Lay.BxL("Y",
                    Lay.lb(label, "valign=top,eb=5,alignx=0.5"),
                    Lay.FL("C", btnStop, btnRestart, btnClear, btnPinOutput, "alignx=0.5")
                ),
                "C", Lay.p(Lay.sp(txtOutput), "eb=5t")
            );
        }
        updateUI();
    }
}
