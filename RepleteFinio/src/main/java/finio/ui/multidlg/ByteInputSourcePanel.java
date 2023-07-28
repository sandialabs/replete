package finio.ui.multidlg;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import finio.ui.bytes.InputMode;
import replete.numbers.NumUtil;
import replete.ui.combo.RComboBox;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.text.editor.REditor;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeDialog;

public class ByteInputSourcePanel extends InputSourcePanel {


    ////////////
    // FIELDS //
    ////////////

    private EscapeDialog parent;
    private RComboBox<InputMode> cboInputMode;
    private REditor edText;
    private JButton btnAccept;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ByteInputSourcePanel(EscapeDialog parent) {
        this.parent = parent;

        JButton btnCancel;
        Lay.BLtg(this,
            "N", Lay.FL("L", cboInputMode = Lay.cb(InputMode.values()), "nogap,eb=5b"),
            "C", edText = Lay.ed("", "ruler"),
            "S", Lay.FL("R",
                btnAccept = Lay.btn("&Accept", CommonConcepts.ACCEPT),
                Lay.p(btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL), "eb=5l"),
                "gap=0,eb=10t"
            ),
            "eb=11"
        );

        btnAccept.addActionListener(e -> {
            String text = edText.getText();
            InputMode inputMode = cboInputMode.getSelected();
            byte[] bytes = extractBytes(text, inputMode);
            if(bytes == null) {
                Dialogs.showWarning(parent,
                    "The provided input does not match the required format for '" +
                        inputMode + "'.");
            } else {
                fireAcceptNotifier();
            }
        });
        btnCancel.addActionListener(e -> fireCancelNotifier());
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void postActivate() {
        parent.setDefaultButton(btnAccept);
    }

    @Override
    protected InputBundle[] getDataBundles() {
        String text = edText.getText();
        byte[] bytes = extractBytes(text, cboInputMode.getSelected());
        return new InputBundle[] {
            new InputBundle()
                .setBytes(bytes)
        };
    }

    private byte[] extractBytes(String text, InputMode mode) {
        byte[] bytes = null;
        switch(mode) {
            case DECIMAL:
                text = text.trim();
                String[] parts = text.split("\\s+");
                bytes = new byte[parts.length];
                boolean neg = false;
                boolean big = false;
                try {
                    int b = 0;
                    for(String part : parts) {
                        int v = Integer.parseInt(part);
                        if(v > 255 || v < -128) {
                            throw new IllegalStateException();
                        }
                        if(v > 127) {
                            big = true;
                        }
                        if(v < 0) {
                            neg = true;
                        }
                        if(big && neg) {
                            throw new IllegalStateException();
                        }
                        bytes[b++] = (byte) v;
                    }
                } catch(Exception e) {
                    bytes = null;
                }
                break;

            case OCTAL:
                break;

            case HEX:
                if(text.matches("^[\\s0-9A-Fa-f]*$")) {
                    text = text.toUpperCase();                    // Multiple passes here
                    text = text.replaceAll("\\s+", "");

                    if(text.length() == 1) {
                        char ch = text.charAt(0);
                        int v = (ch >= 65) ? ch - 55 : ch - 48;
                        bytes = new byte[] {(byte) v};

                    } else if(NumUtil.isEven(text.length())) {    // Odd # doesn't make any sense
                        bytes = new byte[text.length() / 2];
                        int c = 0;
                        for(int b = 0; b < bytes.length; b++) {
                            char ch = text.charAt(c++);
                            int v = (ch >= 65) ? ch - 55 : ch - 48;
                            byte n = (byte) v;
                            n <<= 4;
                            ch = text.charAt(c++);
                            v = (ch >= 65) ? ch - 55 : ch - 48;
                            n |= v;
                            bytes[b] = n;
                        }
                    }
                }
                break;

            case BINARY:        // NOT DONE!
                if(text.matches("^[\\s:01]*$")) {
                    text = text.replaceAll("[\\s:]+", "");

                    if(text.length() < 8) {
                        char ch = text.charAt(0);
                        int v = (ch >= 65) ? ch - 55 : ch - 48;
                        bytes = new byte[] {(byte) v};

                    } else if(text.length() % 8 == 0) {    // Non-byte divisions don't make any sense
                        bytes = new byte[text.length() / 8];
                        int c = 0;
                        for(int b = 0; b < bytes.length; b++) {
                            byte n = 0;
                            for(int x = 0; x < 8; x++) {
                                char ch = text.charAt(c++);
                                if(ch == '1') {
                                    n |= 1;
                                }
                                n <<= 1;
                            }
                            bytes[b] = n;
                        }
                    }
                }
                break;

            case ASCII:
                boolean bad = false;
                for(int c = 0; c < text.length(); c++) {
                    char ch = text.charAt(c);
                    if(ch >= 128) {
                        bad = true;
                        break;
                    }
                }
                if(!bad) {
                    bytes = text.getBytes(StandardCharsets.UTF_8);
                }
                break;

            default:
                System.err.println("error");
                break;
        }
        return bytes;
    }

    @Override
    protected void cleanUp() {
        // Do nothing
    }

    @Override
    public String getTitle() {
        return "Bytes";
    }

    @Override
    public ImageIcon geIcon() {
        return ImageLib.get(CommonConcepts.BINARY);
    }

    @Override
    public void focus() {
        edText.focus();    // Implementation required for some reason to not let dialog focus go haywire.
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        EscapeDialog dialog = new EscapeDialog("Test");
        ByteInputSourcePanel pnlInput = new ByteInputSourcePanel(dialog);
        pnlInput.addAcceptListener(e -> {
            System.out.println(Arrays.toString(pnlInput.getDataBundles()));
            pnlInput.edText.focus();
            pnlInput.edText.getTextPane().selectAll();
        });
        Lay.BLtg(dialog,
            "C", pnlInput,
            "size=[400,200],center,visible"
        );
    }
}
