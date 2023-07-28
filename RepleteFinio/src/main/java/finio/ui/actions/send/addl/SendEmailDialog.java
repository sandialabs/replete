package finio.ui.actions.send.addl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import finio.appstate.AppState;
import finio.ui.app.AppContext;
import replete.mail.Mailer;
import replete.ui.button.RRadioButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.text.RTextField;
import replete.ui.text.bubble.BubbleTextField;
import replete.ui.text.bubble.BubbleValidator;
import replete.ui.text.editor.REditor;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeDialog;

public class SendEmailDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static int SEND = 0;
    private static int CANCEL = 1;

    // Other

    private int result = CANCEL;
    private BubbleTextField txtFrom;
    private BubbleTextField txtTo;
    private BubbleTextField txtCc;
    private BubbleTextField txtBcc;
    private RTextField txtSubject;
    private RRadioButton optTextPlain;
    private REditor txtBody;
    private boolean bccOn;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SendEmailDialog(JFrame owner, AppContext ac) {
        super(owner, "Send E-mail", true);
        setIcon(CommonConcepts.E_MAIL);

        final JButton btnSend, btnCancel, btnCc, btnBcc;
        final JPanel pnlCcBcc, pnlFields, pnlSubject;

        final JPanel pnlCc = Lay.BL(
            "W", Lay.lb("CC:", "prefw=60"),
            "C", txtCc = createEmailBubbleTextField(),
            "eb=5b"
        );
        final JPanel pnlBcc = Lay.BL(
            "W", Lay.lb("BCC:", "prefw=60"),
            "C", txtBcc = createEmailBubbleTextField(),
            "eb=5b"
        );

        RRadioButton optHtml;
        Lay.BLtg(this,
            "C", Lay.BL(
                "N", pnlFields = Lay.BxL(
                    Lay.BL(
                        "W", Lay.lb("From:", "prefw=60"),
                        "C", txtFrom = createEmailBubbleTextField(),
                        "eb=5b"
                    ),
                    Lay.BL(
                        "W", Lay.lb("To:", "prefw=60"),
                        "C", txtTo = createEmailBubbleTextField(),
                        "E", pnlCcBcc = Lay.GL(
                            1, 2,
                            btnCc = Lay.btn("CC", CommonConcepts.ADD, "focusable=false"),
                            btnBcc = Lay.btn("BCC", CommonConcepts.ADD, "focusable=false"),
                            "hgap=5,vgap=0,eb=5l"
                        ),
                        "eb=5b"
                    ),
                    pnlSubject = Lay.BL(
                        "W", Lay.lb("Subject:", "prefw=60"),
                        "C", txtSubject = Lay.tx("", "prefh=30"),
                        "eb=5b"
                    )
                ),
                "C", Lay.BL(
                    "N", Lay.FL("R",
                        "hgap=0,vgap=0",
                        Lay.lb("MIME Type:", "eb=2r"),
                        optTextPlain = Lay.opt("Plain Text", "selected"),
                        optHtml = Lay.opt("HTML")
                    ),
                    "C", txtBody = Lay.ed("", "ruler")
                ),
                "eb=5tlr"
            ),
            "S", Lay.FL("R",
                btnSend = Lay.btn("&Send", CommonConcepts.E_MAIL_SEND),
                btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL)
            ),
            "size=[700,500],center"
        );
        Lay.grp(optTextPlain, optHtml);
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = getValidationMessage();
                if(msg == null) {
                    String s = txtSubject.getText().trim();
                    if(s.isEmpty()) {
                        if(!Dialogs.showConfirm(SendEmailDialog.this, "Do you wish to send without a subject?", "Send E-mail?", true)) {
                            txtSubject.focus();
                            return;
                        }
                    }
                    result = SEND;
                    close();
                } else {
                    Dialogs.showWarning(SendEmailDialog.this, msg, "Send E-mail Warning");
                }
            }
        });
        btnCancel.addActionListener(e -> close());
        btnCc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlCcBcc.remove(btnCc);
                pnlCcBcc.updateUI();

                if(bccOn) {
                    pnlFields.remove(pnlBcc);
                }
                pnlFields.remove(pnlSubject);

                pnlFields.add(pnlCc);
                if(bccOn) {
                    pnlFields.add(pnlBcc);
                }
                pnlFields.add(pnlSubject);
                pnlFields.updateUI();
            }
        });
        btnBcc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnlCcBcc.remove(btnBcc);
                pnlCcBcc.updateUI();

                pnlFields.remove(pnlSubject);

                pnlFields.add(pnlBcc);
                pnlFields.add(pnlSubject);
                pnlFields.updateUI();

                bccOn = true;
            }
        });
        String defEmail = ac.getConfig().getDefaultEmailFromAddress();
        if(defEmail != null) {
            txtFrom.addBubble(defEmail);
        }
        // DEV MODE
        txtTo.addBubble("dtrumbo@sandia.gov");
    }

    private BubbleTextField createEmailBubbleTextField() {
        BubbleValidator validator = new BubbleValidator() {
            public boolean bubbleValid(String bubble) {
                String[] parts = bubble.split("@");
                if(parts == null || parts.length != 2) {
                    return false;
                }
                for(String part : parts) {
                    if(part.length() == 0) {
                        return false;
                    }
                }
                return true;
            }
        };

        return new BubbleTextField()
            .setBubbleChars(",;")
            .setTrimBubbles(true)
            .setValidator(validator)
        ;
    }

    private String getValidationMessage() {

        if(txtFrom.getBubbles().size() == 0) {
            txtFrom.focus();
            return "Please supply a valid sender address.";

        } else if(txtFrom.getBubbles().size() > 1) {
            txtFrom.focus();
            return "Can only have 1 sender address.";

        } else if(txtTo.getBubbles().size() == 0) {
            txtTo.focus();
            return "Please supply one or more valid recipient addresses.";
        }

        return null;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getResult() {
        return result;
    }
    public SendEmailDetails getDetails() {
        return new SendEmailDetails()
            .setFrom(txtFrom.getBubbles().get(0))
            .setTo(txtTo.getBubbles())
            .setCc(txtCc.getBubbles().size() == 0 ? null : txtCc.getBubbles())
            .setBcc(txtBcc.getBubbles().size() == 0 ? null : txtBcc.getBubbles())
            .setSubject(txtSubject.getText().trim())
            .setBody(txtBody.getText())
            .setMimeType(optTextPlain.isSelected() ? Mailer.FORMAT_PLAIN : Mailer.FORMAT_HTML)
        ;
    }

    // Mutators

    public void setBody(String text) {
        txtBody.setText(text);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        AppState.setState(new AppState());
        SendEmailDialog dlg = new SendEmailDialog(null, new AppContext());
        dlg.setVisible(true);
    }
}
