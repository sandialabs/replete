package replete.ui.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicOptionPaneUI;

import replete.errors.ExceptionUtil;
import replete.io.FileUtil;
import replete.mail.Mailer;
import replete.text.StringUtil;
import replete.ui.GuiUtil;
import replete.ui.button.RButton;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.sp.IconDescriptor;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.text.editor.REditor;
import replete.ui.windows.notifications.NotificationDialog;
import replete.ui.worker.RWorker;
import sun.swing.DefaultLookup;

public class ExceptionDialog extends NotificationDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int SOURCE_LINE_RANGE = 14;
    private static ExceptionSubmissionDetails globalSubmissionDetails;

    private RTabbedPane tabs;
    private REditor     edSource;
    private RButton     btnSubmit;
    private boolean     hasSourceText;
    private String      sourceFile;
    private int         sourceLine;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExceptionDialog(Window parent, ExceptionDetails exDetails) {
        this(parent, exDetails, null);
    }
    public ExceptionDialog(Window parent, ExceptionDetails exDetails, final ExceptionSubmissionDetails smDetails) {
        super(parent, "Example Frame for Test", true);

        edSource = Lay.ed("Searching...", "font=Monospaced,editable=false");

        Object message = toMsgStr(exDetails.getMessage());

        String title = exDetails.getTitle();
        if(title == null) {
            title = Dialogs.DFLT_ERR_TITLE;
        }

        setTitle(title);
        setIcon(CommonConcepts.ERROR);

        if(exDetails.isPrintStackTrace() && exDetails.getError() != null) {
            exDetails.getError().printStackTrace();
        }

        final String detailsMessage = createDetailsMessage(exDetails);

        File ws = new File("C:\\Users\\emridgw\\workspace\\eclipse");
        exDetails
            .addSourceDir(new File(ws, "Avondale\\src"))
            .addSourceDir(new File(ws, "Cortext\\src"))
            .addSourceDir(new File(ws, "Finio\\src"))
            .addSourceDir(new File(ws, "Finio\\plugins"))
            .addSourceDir(new File(ws, "Reframe\\src"))
            .addSourceDir(new File(ws, "Reframe\\test"))
            .addSourceDir(new File(ws, "Replete\\src"))
            .addSourceDir(new File(ws, "RepleteExternals\\src"))
            .addSourceDir(new File(ws, "Orbweaver\\src"))
            .addSourceDir(new File(ws, "WebComms\\src"));

        Icon icon = (Icon) DefaultLookup.get(
            new JOptionPane(), new BasicOptionPaneUI(), "OptionPane.errorIcon");

        ActionListener submitAction = e -> {
            if(!Dialogs.showConfirm(ExceptionDialog.this, "Are you sure you would like to submit this error to the developer?", "Confirm Submit")) {
                return;
            }

            btnSubmit.setEnabled(false);
            waitOn();
            RWorker<Void, Void> worker = new RWorker<Void, Void>() {
                @Override
                protected Void background(Void gathered) throws Exception {
                    String dm = detailsMessage;
                    dm = StringUtil.cleanXmlCdata(dm);
                    dm = dm.replaceAll(" ", "&nbsp;");
                    dm = dm.replaceAll("\n", "<br>");

                    String body = "<font color='blue'><b>Exception<br>-------------------------------</b></font><br>" + dm;
                    if(hasSourceText) {
                        String code = edSource.getText();
                        code = StringUtil.cleanXmlCdata(code);
                        code = code.replaceAll(" ", "&nbsp;");
                        code = code.replaceAll("\n", "<br>");
                        body += "<br><br><br><font color='blue'><b>Source Snippit<br>" +
                            "Source File:&nbsp;" + sourceFile + "<br>" +
                            "Line Number:&nbsp;" + sourceLine + "<br>" +
                                "-------------------------------</b></font><br>" + code;
                    }
                    ExceptionSubmissionDetails sd = smDetails == null ? globalSubmissionDetails : smDetails;
                    String smtpHostIp = sd.getSmtpHostIp();
                    String fromEmail = sd.getFrom();
                    String[] toEmails = sd.getTo();
                    sendMail(smtpHostIp, fromEmail, Arrays.asList(toEmails), "Error Submission Report",
                        "<html><body><font face='monospace'>" + body + "</font></body></html>");
                    return null;
                }
                @Override
                protected void complete() {
                    try {
                        getResult();
                        btnSubmit.setText("Submitted To Developer");
                        btnSubmit.setIcon(CommonConcepts.ACCEPT);
                        Dialogs.showMessage(
                            ExceptionDialog.this,
                            "Successfully sent to developer.  Thank you!",
                            "Submit Error"
                        );
                    } catch(Exception ex) {
                        Dialogs.showDetails(ExceptionDialog.this,
                            "An error has occurred submitting this error to the developer.",
                            "Submit Error", ex);
                        //logger.error(LC_MM, e);
                        btnSubmit.setEnabled(true);
                    }
                    waitOff();
                };
            };
            worker.execute();
        };

        boolean addSubmit =
            globalSubmissionDetails != null && globalSubmissionDetails.hasAllFields() ||
            smDetails != null && smDetails.hasAllFields();

        Lay.BLtg(this,
            "N", Lay.BL(
                "W", Lay.FL(Lay.lb(icon), "eb=5"),
                "C", Lay.BL("N", Lay.lb("<html>" + message + "</html>", "eb=5"))
            ),
            "C", tabs = Lay.TBL(
                "Stack Trace",
                    CommonConcepts.STACK_TRACE,
                    Lay.sp(Lay.txa(detailsMessage, "font=Monospaced,editable=false"))
            ),
            "S", Lay.FL("R",
                addSubmit ? btnSubmit = Lay.btn("&Submit To Developer", CommonConcepts.E_MAIL_SEND, submitAction) : null,
                Lay.btn("&Close", CommonConcepts.CANCEL, "closer"),
                "bg=100,mb=[1t,black]"
            ),
            "size=[900,600],center"
        );

        checkAddSourceTab(exDetails);
    }

    public static void setSubmissionDetails(ExceptionSubmissionDetails details) {
        ExceptionDialog.globalSubmissionDetails = details;
    }

    public static void sendMail(String smtpHostIp, String from,
                                List<String> to, String subject, String body)
                                    throws Exception {
        Mailer.setDefaultFormat("text/html");
        Mailer.setSmtpHost(smtpHostIp);
        Mailer.sendEmail(from, to, null, null, subject, body);
    }

    private String createDetailsMessage(ExceptionDetails details) {
        String detailsMessage = "";
        if(details.getDetailsMessage() != null) {
            detailsMessage += details.getDetailsMessage();
        }
        if(details.getError() != null) {
            if(!detailsMessage.isEmpty()) {
                detailsMessage += "\n";
            }
            detailsMessage +=
                ExceptionUtil.toCompleteString(details.getError(), 4).trim();
        }
        return detailsMessage;
    }

    public void checkAddSourceTab(final ExceptionDetails details) {
        if(details.getSourceDirs() != null && details.getError() != null) {
            if(details.getError().getStackTrace() != null) {
                if(details.getError().getStackTrace().length != 0) {
                    String srcFile = null;
                    int num = 0;
                    for(int s = 0; s < details.getError().getStackTrace().length; s++) {
                        StackTraceElement elem = details.getError().getStackTrace()[s];
                        String cl = elem.getClassName();
                        if(cl.startsWith("finio") || cl.startsWith("replete") || cl.startsWith("gov.sandia")) {
                            srcFile = elem.getFileName();
                            num = elem.getLineNumber();
                            break;
                        }
                    }
                    if(srcFile != null && num > 0) {
                        tabs.addTab("Source Snippit", RepleteImageModel.SOURCE, edSource);
                        edSource.setStartLineNumber(Math.max(1, num - SOURCE_LINE_RANGE));
                        edSource.setShowStatusLine(false);
                        edSource.getTextPane().setAllowHorizScroll(true);
                        sourceFile = srcFile;
                        sourceLine = num;
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    List<File> found = FileUtil.find(details.getSourceDirs(), new FileFilter() {
                                        @Override
                                        public boolean accept(File file) {
                                            return
                                                (file.isDirectory() && !file.getName().startsWith(".")) ||  // Directory recurse rule
                                                (file.isFile() && file.getName().equals(sourceFile));      // File match rule
                                        }
                                    });
                                    if(found.isEmpty()) {
                                        set("Could not find file: " + sourceFile);
                                        return;
                                    }
                                    set("Parsing...");
                                    String source = FileUtil.getTextContent(found.get(0));
                                    final List<String> lines = new ArrayList<>();
                                    int theLine = 0;
                                    int n = 1;
                                    try(BufferedReader reader = new BufferedReader(new StringReader(source))) {
                                        String line;
                                        while((line = reader.readLine()) != null) {
                                            int delta = n - sourceLine;
                                            if(Math.abs(delta) <= SOURCE_LINE_RANGE) {
                                                if(delta == 0) {
                                                    theLine = lines.size();
                                                }
                                                lines.add(line);
                                            }
                                            n++;
                                        }
                                    }
                                    final int fTheLine = theLine;
                                    GuiUtil.safe(new Runnable() {
                                        @Override
                                        public void run() {
                                            edSource.getTextPane().clear();
                                            int caret = 0;
                                            for(int i = 0; i < lines.size(); i++) {
                                                if(i == fTheLine) {
                                                    edSource.getTextPane().append(lines.get(i), new Font("Courier New", Font.BOLD, 12), Color.red);
                                                } else {
                                                    edSource.getTextPane().append(lines.get(i), Color.black);
                                                }
                                                if(i <= fTheLine) {
                                                    caret += lines.get(i).length();
                                                }
                                                if(i != lines.size() - 1) {
                                                    edSource.getTextPane().append("\n");
                                                    if(i < fTheLine) {
                                                        caret++;
                                                    }
                                                }
                                            }
                                            edSource.getTextPane().setCaretPosition(caret);
                                            edSource.getScrollPane().setShowRuler(true);
                                            edSource.getScrollPane().setShowRangesAndIcons(true);
                                            edSource.getScrollPane().getRulerModel().addIcon("ErrorIcon", sourceLine - 1,
                                                new IconDescriptor(ImageLib.get(CommonConcepts.CANCEL),
                                                    details.getError().getClass().getSimpleName()));
                                        }
                                    });
                                } catch(Exception e) {
                                    set("An error occurred while searching or parsing: " + sourceFile +
                                        "\n" + ExceptionUtil.toCompleteString(e, 4));
                                }
                            }
                            private void set(final String msg) {
                                GuiUtil.safe(new Runnable() {
                                    @Override
                                    public void run() {
                                        edSource.setText(msg);
                                        edSource.getTextPane().setCaretPosition(0);
                                        hasSourceText = true;
                                    }
                                });
                            }
                        }.start();
                    }
                }
            }
        }
    }


    //////////
    // MISC //
    //////////

    private Object toMsgStr(Object msg) {
        if(msg == null) {
            return "An error has occurred.";
//        } else if(msg instanceof Component) {
//            return msg;
        }
        return msg.toString();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        ExceptionDetails exDetails = new ExceptionDetails()
            .setTitle("TITLE!")
            .setMessage("Yo you uh gotz some problemz al;siddfj alsfdsfjl;safjadf adsl;fjas;dkl f;asldfk asd fl;ka jfasdjf;las f;lkasd f;lkadsfkjasd;f; kldsf;j as;k fa s;kdf j;ads f;asd kfladsk;f ;asdf;kasdj fk;lsakfaslf jalskdfj ;.")
            .setError(new RuntimeException())
            .setDetailsMessage("some deetz");
//        Dialogs.showDetails(null, details);
        ExceptionSubmissionDetails smDetails = new ExceptionSubmissionDetails()
            .setSmtpHostIp("132.175.109.1")
            .setFrom("dtrumbo@sandia.gov")
            .setTo(new String[] {"dtrumbo@sandia.gov"})
        ;
        ExceptionDialog.setSubmissionDetails(smDetails);
        ExceptionDialog frame = new ExceptionDialog(null, exDetails);
        frame.setVisible(true);
    }
}
