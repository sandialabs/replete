package replete.ui.table;

import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JLabel;

import replete.ui.button.RButton;
import replete.ui.button.RCheckBox;
import replete.ui.button.RRadioButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.text.RegexTestDialog;
import replete.ui.windows.escape.EscapeDialog;
import replete.util.OsUtil;

public class RTableFilterOptionsDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int SET = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;

    private RCheckBox chkMatchCase;
    private RRadioButton optMatchModeNormal;
    private RRadioButton optMatchModeRegex;
    private RButton btnSet;
    private RButton btnCancel;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTableFilterOptionsDialog(Window parent, RTableFilterOptions options) {
        super(parent, "Filter Options", true);
        setIcon(CommonConcepts.OPTIONS);

        JLabel lblDocs, lblTest;
        Lay.BLtg(this,
            "N", Lay.lb("<html><i>Set the filter options for this table:</i></html>", "eb=5lrt15b"),
            "C", Lay.BxL(
                chkMatchCase = Lay.chk("Match Case", "alignx=0"),
                Box.createVerticalStrut(10),
                Lay.lb("Match Mode", "underline"),
                optMatchModeNormal = Lay.opt("Normal", "alignx=0"),
                Lay.FL("L",
                    optMatchModeRegex = Lay.opt("Regular Expression"),
                    Lay.lb("<html><i>(</i></html>"),
                    lblDocs = Lay.lb("<html><font color='blue'><u>Documentation</u></font></html>", "cursor=hand"),
                    Lay.lb("<html><i>, </i></html>"),
                    lblTest = Lay.lb("<html><font color='blue'><u>Test</u></font></html>", "cursor=hand"),
                    Lay.lb("<html><i>)</i></html>"),
                    "nogap,alignx=0"
                ),
                Box.createVerticalGlue(),
                "eb=15l5r"
            ),
            "S", Lay.FL("R",
                btnSet    = Lay.btn("&Set",    CommonConcepts.ACCEPT),
                btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL),
                "bg=100,mb=[1t,black]"
            ),
            "size=[320,225],center"
        );

        Lay.grp(optMatchModeNormal, optMatchModeRegex);

        lblDocs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                OsUtil.openSystemBrowser(RegexTestDialog.JAVA_REGEX_DOCUMENTATION_URL);
            }
        });
        lblTest.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                RegexTestDialog dlg = new RegexTestDialog(
                    RTableFilterOptionsDialog.this, null, null);
                dlg.setVisible(true);
            }
        });
        btnSet.addActionListener(e -> {
            result = SET;
            close();
        });
        btnCancel.addActionListener(e -> close());

        readFromSource(options);
    }


    ////////////
    // RESULT //
    ////////////

    public int getResult() {
        return result;
    }
    public RTableFilterOptions getOptions() {
        RTableFilterOptions options = new RTableFilterOptions();
        options.setMatchCase(chkMatchCase.isSelected());
        options.setMatchMode(optMatchModeNormal.isSelected() ? MatchMode.NORMAL : MatchMode.REGEX);
        return options;
    }


    //////////
    // SAVE //
    //////////

    private void readFromSource(RTableFilterOptions options) {
        chkMatchCase.setSelected(options.isMatchCase());
        optMatchModeNormal.setSelected(options.getMatchMode() == MatchMode.NORMAL);
        optMatchModeRegex.setSelected(options.getMatchMode() == MatchMode.REGEX);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        RTableFilterOptions options = new RTableFilterOptions()
            .setMatchCase(true)
            .setMatchMode(MatchMode.NORMAL)
        ;
        RTableFilterOptionsDialog dlg = new RTableFilterOptionsDialog(null, options);
        dlg.setVisible(true);
    }
}
