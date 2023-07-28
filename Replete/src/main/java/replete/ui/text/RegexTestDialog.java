package replete.ui.text;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import replete.text.StringUtil;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.menu.RCheckBoxMenuItem;
import replete.ui.table.DefaultUiHintedTableModel;
import replete.ui.text.editor.REditor;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeDialog;
import replete.util.OsUtil;

public class RegexTestDialog extends EscapeDialog {


    ///////////
    // ENUMS //
    ///////////

    private enum PatternOption {
        CANON_EQ("Enable canonical equivalence.", Pattern.CANON_EQ),
        CASE_INSENSITIVE("Enable case-insensitive matching.", Pattern.CASE_INSENSITIVE),
        COMMENTS("Permit whitespace and comments in pattern.", Pattern.COMMENTS),
        DOTALL("Enable dotall mode.", Pattern.DOTALL),
        LITERAL("Enable literal parsing of the pattern.", Pattern.LITERAL),
        MULTILINE("Enable multiline mode.", Pattern.MULTILINE),
        UNICODE_CASE("Enable Unicode-aware case folding.", Pattern.UNICODE_CASE),
//        UNICODE_CHARACTER_CLASS("Enables the Unicode version of Predefined character classes and POSIX character classes.", Pattern.UNICODE_CHARACTER_CLASS),
        UNIX_LINES("Enable Unix lines mode.", Pattern.UNIX_LINES);

        String name;
        private int flag;

        private PatternOption(String name, int flag) {
            this.name = name;
            this.flag = flag;
        }
    }


    ////////////
    // FIELDS //
    ////////////

    // Constant
    public static final String JAVA_REGEX_DOCUMENTATION_URL =
        "https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html";

    // UI
    private ResultsTableModel mdl;
    private RTextField txtPattern;
    private REditor edTarget;
    private JButton btnPatternOptions;
    private JLabel lblStatus;
    private List<PatternOptionMenuItem> optionMenus;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RegexTestDialog(Window parent, String initRegex, String initTarget) {
        super(parent, "Regular Expression Testing", true);
        setIcon(RepleteImageModel.REGEX);

        if(initRegex == null) {
            initRegex = "";
        }

        JButton btnTest, btnDoc, btnClose;
        Lay.BLtg(this,
            "N", Lay.lb(
                "<html>Enter a regular expression, specify target text block, and then click 'Test'.  Both <u>matches</u> and their contained <u>capture groups</u> will be displayed together in the 'Results' table.<html>",
                "fg=white,bg=100,eb=5,augb=mb(1b,black)"
            ),
            "C", Lay.BL(
                "N", Lay.BL(
                    "N", Lay.lb("<html><u>P</u>attern:</html>", "fg=21007F"),
                    "C", txtPattern = Lay.tx(initRegex, "selectall,size=14,eb=5,augb=mb(1,black)"),
                    "E", Lay.BL(
                        "W", Lay.p(btnPatternOptions = Lay.btn(CommonConcepts.OPTIONS, 2, "ttt=Pattern-Options"), "eb=5l"),
                        "E", Lay.p(Lay.btn(CommonConcepts.ACCEPT, 2, "ttt=Test", (ActionListener) e -> performMatch()), "eb=5l")
                    ),
                    "eb=10tlr5b"
                ),
                "C", Lay.GL(2, 1,
                    Lay.BL(
                        "N", Lay.lb("<html>Ta<u>r</u>get:</html>", "fg=21007F"),
                        "C", edTarget = Lay.ed(StringUtil.cleanNull(initTarget), "ruler"),
                        "eb=10lr5b"
                    ),
                    Lay.BL(
                        "N", Lay.FL("L",
                            Lay.lb("Results:", "fg=21007F"),
                            lblStatus = Lay.lb("", "eb=5l"),
                            "nogap"
                        ),
                        "C", Lay.sp(Lay.tbl(mdl = new ResultsTableModel(), "rh=20")),
                        "eb=10lrb"
                    )
                )
            ),
            "S", Lay.BL(
                "W", Lay.FL(
                    btnTest = Lay.btn("&Test", CommonConcepts.ACCEPT),
                    btnDoc = Lay.btn("Launch &Documentation", CommonConcepts.INTERNET)
                ),
                "E", Lay.FL(btnClose = Lay.btn("&Close", CommonConcepts.CANCEL)),
                "bg=100,chtransp,mb=[1t,black]"
            ),
            "size=[750,650],center"
        );

        optionMenus = new ArrayList<>();
        for(PatternOption option : PatternOption.values()) {
            PatternOptionMenuItem mnuItem = new PatternOptionMenuItem(option);
            optionMenus.add(mnuItem);
        }

        btnPatternOptions.addActionListener(e -> showOptions());
        btnTest.addActionListener(e -> performMatch());
        btnDoc.addActionListener(e -> OsUtil.openSystemBrowser(JAVA_REGEX_DOCUMENTATION_URL));
        btnClose.addActionListener(e -> close());
        setDefaultButton(btnTest);
    }

    private void showOptions() {
        JPopupMenu mnuOptions = new JPopupMenu();
        for(PatternOptionMenuItem mnu : optionMenus) {
            mnuOptions.add(mnu);
        }
        mnuOptions.show(btnPatternOptions, 0, btnPatternOptions.getHeight());
    }

    private void performMatch() {
        String pattern = txtPattern.getText();
        if(pattern.isEmpty()) {
            txtPattern.focus();
            Dialogs.showWarning(this, "Please specify a pattern.", "Warning");
            return;
        }
        int flags = 0;
        for(PatternOptionMenuItem mnuOption : optionMenus) {
            if(mnuOption.isSelected()) {
                flags += mnuOption.option.flag;
            }
        }
        Pattern p;
        try {
            p = Pattern.compile(pattern, flags);
        } catch(Exception e) {
            txtPattern.focus();
            Dialogs.showDetails(this, "An error has occurred parsing this regular expression.", e);
            return;
        }
        List<MatchResult> matches = new ArrayList<>();
        Matcher m = p.matcher(edTarget.getText());
        int gc = 0;
        while(m.find()) {
            MatchResult result = m.toMatchResult();
            matches.add(result);
            gc += result.groupCount();
        }
        mdl.setMatches(matches);
        if(matches.isEmpty()) {
            lblStatus.setText(" (NONE)");
            Lay.hn(lblStatus, "fg=red");
        } else {
            int sz = matches.size();
            String extraGc = gc == 0 ? "" : ", " + gc + " Capture Group" + StringUtil.s(gc);
            lblStatus.setText(" (" + sz + " Match" + StringUtil.es(sz) + extraGc + ")");
            Lay.hn(lblStatus, "fg=36B500");
        }
        txtPattern.focus();
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getRegex() {
        return txtPattern.getText();
    }


    //////////
    // MISC //
    //////////

    @Override
    protected JRootPane createRootPane() {
        JRootPane rp = super.createRootPane();

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK);
        rp.registerKeyboardAction(e -> txtPattern.focus(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK);
        rp.registerKeyboardAction(e -> edTarget.focus(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        return rp;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class ResultsTableModel extends DefaultUiHintedTableModel {
        private List<MatchItem> items = new ArrayList<>();

        public void setMatches(List<MatchResult> matches) {
            List<MatchItem> items = new ArrayList<>();
            for(MatchResult match : matches) {
                MatchItem item = new MatchItem(
                    false,
                    match.start(),
                    match.end(),
                    match.groupCount(),
                    match.group()
                );
                items.add(item);
                for(int i = 1; i <= match.groupCount(); i++) {
                    MatchItem subitem = new MatchItem(
                        true,
                        match.start(i),
                        match.end(i),
                        -1,
                        match.group(i)
                    );
                    items.add(subitem);
                }
            }
            this.items = items;
            fireTableDataChanged();
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        protected void init() {
            addColumn("" ,      Object.class, new int[] {23, 23, 23});
            addColumn("#",      String.class, new int[] {30, 30, 50});
            addColumn("Start",  String.class, new int[] {55, 55, 90});  // Strings for alignment reasons
            addColumn("End",    String.class, new int[] {55, 55, 90});
            addColumn("Groups", String.class, new int[] {55, 55, 90});
            addColumn("Match",  String.class, new int[] {-1, -1, -1});
        }
        @Override
        public int getRowCount() {
            if(items == null) {
                return 0;
            }
            return items.size();
        }
        @Override
        public Object getValueAt(int row, int col) {
            MatchItem result = items.get(row);
            switch(col) {
                case 0: return "";
                case 1: return row + 1;
                case 2: return result.start;
                case 3: return result.end;
                case 4: return result.group ? "-" : result.groupCount;
                case 5: return result.match;
            }
            return null;
        }
        @Override
        public Insets getInsets(int row, int col) {
            return new Insets(0, 2, 0, 2);
        }
        @Override
        public Icon getIcon(int row, int col) {
            if(col == 0) {
                MatchItem result = items.get(row);
                return result.group ?
                    ImageLib.get(RepleteImageModel.MATCH_GROUP) :
                    ImageLib.get(RepleteImageModel.MATCH);
            }
            return super.getIcon(row, col);
        }
        @Override
        public Color getBackgroundColor(int row, int col) {
            MatchItem result = items.get(row);
            return result.group ? Lay.clr("225,255,225") : Color.white;
        }
        @Override
        public int getAlignment(int row, int col) {
            return col <= 4? SwingConstants.RIGHT : SwingConstants.LEFT;
        }
        @Override
        public Boolean isBold(int row, int col) {
            MatchItem result = items.get(row);
            return !result.group;
        }
    }

    private class MatchItem {
        boolean group;
        int start;
        int end;
        int groupCount;
        String match;
        public MatchItem(boolean group, int start, int end, int groupCount, String match) {
            this.group = group;
            this.start = start;
            this.end = end;
            this.groupCount = groupCount;
            this.match = match;
        }
    }

    private class PatternOptionMenuItem extends RCheckBoxMenuItem {
        PatternOption option;
        public PatternOptionMenuItem(PatternOption option) {
            super(option.name);
            this.option = option;
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        RegexTestDialog dlg = new RegexTestDialog((Window) null, null, null);
        dlg.setVisible(true);
    }
}
