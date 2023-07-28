package replete.ui.csv;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.text.RTextField;
import replete.ui.windows.escape.EscapeDialog;

public class CsvGeneratorNameDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int OK = 1;
    public static final int CANCEL = 2;

    private int result = CANCEL;

    private String name;
    private RTextField text;
    private RButton btnOk, btnCancel;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public CsvGeneratorNameDialog(JFrame parent, String label, String defaultName) {
        super(parent, label, true);
        setIcon(CommonConcepts.COPY);

        Lay.BLtg(this,
            "C", text = Lay.tx(defaultName, "selectall, prefw=300, maxw=300"),
            "S", Lay.FL("R",
                btnOk = Lay.btn("&OK", CommonConcepts.ACCEPT),
                btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL)
            ),
            "eb=10,size=[300,120],center"
        );

        setDefaultButton(btnOk);

        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                btnOk.setEnabled(!text.getText().isEmpty());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                btnOk.setEnabled(!text.getText().isEmpty());
            }
        });

        btnOk.setEnabled(false);
        btnOk.addActionListener(e -> {
            result = OK;
            name = text.getText();
            close();
        });
        btnCancel.addActionListener(e -> close());
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    public String getFileName() {
        return name;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        CsvGeneratorNameDialog dlg = new CsvGeneratorNameDialog(null, "test", "test");
        dlg.setVisible(true);
        System.out.println(dlg.getResult() + " " + dlg.getFileName());
    }
}
