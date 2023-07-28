package finio.ui.multidlg;

import java.nio.charset.StandardCharsets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.text.editor.REditor;
import replete.ui.windows.escape.EscapeDialog;

public class TextInputSourcePanel extends InputSourcePanel {


    ////////////
    // FIELDS //
    ////////////

    private EscapeDialog parent;
    private REditor edText;
    private JButton btnAccept;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TextInputSourcePanel(EscapeDialog parent) {
        this.parent = parent;

        JButton btnCancel;
        Lay.BLtg(this,
            "C", edText = Lay.ed("", "ruler"),
            "S", Lay.FL("R",
                btnAccept = Lay.btn("&Accept", CommonConcepts.ACCEPT),
                Lay.p(btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL), "eb=5l"),
                "gap=0,eb=10t"
            ),
            "eb=11"
        );

        btnAccept.addActionListener(e -> fireAcceptNotifier());
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
        return new InputBundle[] {
            new InputBundle()
                .setText(text)
                .setBytes(text.getBytes(StandardCharsets.UTF_8))
        };
    }

    @Override
    protected void cleanUp() {
        // Do nothing
    }

    @Override
    public String getTitle() {
        return "Text";
    }

    @Override
    public ImageIcon geIcon() {
        return ImageLib.get(CommonConcepts.RENAME);
    }

    @Override
    public void focus() {
        edText.focus();    // Implementation required for some reason to not let dialog focus go haywire.
    }
}
