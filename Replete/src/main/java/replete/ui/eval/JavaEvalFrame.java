package replete.ui.eval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.pstate.PersistentState;
import replete.pstate.StateReader;
import replete.pstate.StateWriter;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.text.editor.REditor;
import replete.ui.windows.Dialogs;
import replete.ui.windows.notifications.NotificationFrame;
import replete.ui.worker.RWorker;
import replete.util.User;


/**
 * Useful for debugging.  Gives you a window into which you can
 * type arbitrary Java code that will be compiled and executed.
 * The goal is to simplify the amount of coded needed to be
 * typed just to see how a certain method behaves in Java.
 *
 * @author Derek Trumbo
 */

public class JavaEvalFrame extends NotificationFrame {


    ////////////
    // FIELDS //
    ////////////

    protected REditor edInput;
    protected JTextArea txtOutput;
    protected JTextArea txtResolvedInput;
    protected JTextArea txtSource;
    private JButton btnRun;
    private JButton btnClose;

    protected EvalState state = new EvalState();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public JavaEvalFrame() {
        super("Java Evaluator");
        setIcon(RepleteImageModel.EVAL_LOGO);

        state.read();

        JPanel pnlButtons = Lay.FL("R",
            btnRun = Lay.btn("&Run", CommonConcepts.PLAY),
            btnClose = Lay.btn("&Close", CommonConcepts.CANCEL)
        );

        Lay.BLtg(this,
            "C", Lay.GL(2, 1,
                Lay.BL(
                    "N", Lay.lb("Input (code that would go in a main method, \"@@\" can be used for \"System.out.println\"):"),
                    "C", Lay.sp(edInput = Lay.ed(state.getCode().trim(), "font=Courier-New,size=16")),
                    "eb=5lr"
                ),
                Lay.BL(
                    "C", Lay.TBL(
                        "Output", Lay.sp(txtOutput = Lay.txa("", "editable=false,font=Courier-New,size=16")),
                        "Resolved Input", Lay.sp(txtResolvedInput = Lay.txa("", "editable=false,font=Courier-New,size=16")),
                        "Generated Source", Lay.sp(txtSource = Lay.txa("", "editable=false,font=Courier-New,size=16"))
                    ),
                    "eb=5lr"
                )
            ),
            "size=[800,500],center,dco=dispose"
        );

        edInput.getScrollPane().setShowRuler(true);

        getStatusBar().setRightComponent(pnlButtons);
        setShowStatusBar(true);

        edInput.selectAll();
        btnRun.addActionListener(e -> doRun());
        btnClose.addActionListener(e -> close());

        setDefaultButton(btnRun);

        addClosingListener(e -> {
            state.setCode(edInput.getText());
            state.write();
        });
    }

    protected ChangeListener guiMessageListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            txtOutput.append(e.getSource().toString());
        }
    };

    protected static ChangeListener consoleMessageListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            System.out.print(e.getSource().toString());
        }
    };


    /////////
    // RUN //
    /////////

    private void doRun() {
        txtOutput.setText("");
        btnRun.setEnabled(false);
        RWorker<Void, Void> worker = new RWorker<Void, Void>() {
            @Override
            protected Void background(Void gathered) throws Exception {
                EvalRunner runner = new EvalRunner();
                runner.addMessageListener(guiMessageListener);
                EvalRunResults results = runner.run(edInput.getText());
                txtResolvedInput.setText(results.resolvedInput);
                txtSource.setText(results.sourceCode);
                return null;
            }
            @Override
            protected void complete() {
                try {
                    getResult();
                } catch(Exception e) {
                    Dialogs.showDetails(JavaEvalFrame.this,
                        "An error has occurred compiling and/or executing this code.",
                        "Error", e);
                }
                edInput.requestFocusInWindow();
                btnRun.setEnabled(true);
            }
        };
        addTaskAndExecuteFg("Compiling and Running Code", worker);

    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    protected class EvalState extends PersistentState {
        protected String code = "";

        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }

        @Override
        protected void addStateReaders() {
            addStateReader(new StateReader() {
                public boolean read() {
                    File file = User.getHome("eval.state");
                    if(!file.exists()) {
                        return false;
                    }
                    try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        StringBuilder buffer = new StringBuilder();
                        while((line = reader.readLine()) != null) {
                            buffer.append(line);
                            buffer.append('\n');
                        }
                        code = buffer.toString();
                        return true;
                    } catch(Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            });
        }

        @Override
        protected void addStateWriters() {
            addStateWriter(new StateWriter() {
                public boolean write() {
                    File file = User.getHome("eval.state");
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write(code + "\n");
                        writer.close();
                        return true;
                    } catch(Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            });
        }
    }


    //////////
    // MAIN //
    //////////

    public static void main(String[] args) {
        if(args.length == 0) {
            JavaEvalFrame fraEval = new JavaEvalFrame();
            fraEval.setVisible(true);
        } else {
            EvalRunner runner = new EvalRunner();
            runner.addMessageListener(consoleMessageListener);
            runner.run(args[0]);
        }
    }
}
