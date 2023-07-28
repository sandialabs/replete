package replete.ui.cli;

import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;

import replete.cli.bash.ArgumentRangeMap;
import replete.cli.bash.BashCommandLineParser;
import replete.cli.bash.ParseResult;
import replete.text.StringUtil;
import replete.ui.GuiUtil;
import replete.ui.form.RFormPanel;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextPane;
import replete.ui.text.validating.ValidatingTextField;
import replete.ui.text.validating.Validator;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;

public class BashTestFrame extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    private ResultPanel pnlResult;
    private JButton btnParse;
    private JButton btnClose;
    RTextPane txtLine;
    ValidatingTextField txtEnv;

    public BashTestFrame() {
        super("Example Frame for BashTestFrame");

        Validator validator = (txt, text) -> {
            try {
                parseEnv();
                return true;
            } catch(Exception e) {
                return false;
            }
        };

        Lay.BLtg(this,
            "N", Lay.BL(
                "N", Lay.lb("Command Line:"),
                "C", Lay.sp(txtLine = Lay.txp("", "size=14,font=Courier-New")),
                "S", Lay.BL(
                    "W", Lay.lb("Environment Map:", "eb=5r"),
                    "C", txtEnv = Lay.tx("", validator),
                    "eb=5t"
                ),
                "prefh=100,eb=5,augb=mb(1b,black)"
            ),
            "C", pnlResult = new ResultPanel(),
            "S", Lay.FL("R",
                btnParse = Lay.btn("&Parse", (ActionListener) e -> parse()),
                btnClose = Lay.btn("&Close", (ActionListener) e -> close()),
                "bg=100,mb=[1t,black]"
            ),
            "size=[800,600],center"
        );

        txtLine.getDocument().addDocumentListener((DocumentChangeListener) e -> parse());

        GuiUtil.safe(() -> parse());
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private void parse() {
        try {
            Map<String, String> env = parseEnv();
            BashCommandLineParser parser = new BashCommandLineParser(env);
            ParseResult result = parser.parseWithResult(txtLine.getText());
            pnlResult.set(result);
        } catch(Exception e) {
            Dialogs.showDetails(this, "adf", "adsfs", e);
        }
    }

    private Map<String, String> parseEnv() {
        String env = txtEnv.getText().trim();
        Map<String, String> map = new LinkedHashMap<>();
        if(!env.isEmpty()) {
            String delim = ";";
            if(env.length() >= 2 && env.charAt(0) != '\'' && env.charAt(0) != '"' && env.charAt(1) == '>') {
                delim = env.charAt(0) + "";
                env = env.substring(2).trim();
            }
            String[] kvs = env.split("\\s*\\Q" + delim + "\\E\\s*");
            for(String kv : kvs) {
                String[] parts = kv.split("\\s*=\\s*", 2);
                if(parts.length != 2) {
                    throw new IllegalArgumentException("invalid kv");
                }
                if(parts[0].isEmpty()) {
                    throw new IllegalArgumentException("invalid k");
                }
                if(parts[1].isEmpty()) {
                    throw new IllegalArgumentException("invalid v");
                }
                parts[0] = check(parts[0]);
                parts[1] = check(parts[1]);
                map.put(parts[0], parts[1]);
            }
        }
        return map;
    }

    private String check(String str) {
        if(!str.isEmpty()) {
            if(str.startsWith("'")) {
                if(str.length() == 1 || !str.endsWith("'")) {
                    throw new IllegalArgumentException("env key/values must have matching end quotes");
                }
                str = str.substring(1, str.length() - 1);
            }
            if(str.startsWith("\"")) {
                if(str.length() == 1 || !str.endsWith("\"")) {
                    throw new IllegalArgumentException("env key/values must have matching end quotes");
                }
                str = str.substring(1, str.length() - 1);
            }
        }
        return str;
    }


    private class ResultPanel extends RFormPanel {
        JLabel lblOrigLine;
        JLabel lblResult;
        JLabel lblCurArg;
        JLabel lblFreeArg;
        JLabel lblQuoteArg;
        JLabel lblComplete;
        JLabel lblArguments;
        JLabel lblArgRangeMap;
        JLabel lblTlArgMap;

        public ResultPanel() {
            init();
        }

        public void set(ParseResult result) {
            lblOrigLine.setText(wrap(result.getOriginalLine()));
            lblResult.setText(result.getState().toString() + " (" + result.getState().getEscapeChars() + ")");
            lblFreeArg.setText(wrap(result.getCurrentArgument()));
            lblCurArg.setText(wrap(result.getFreeArgument()));
            lblQuoteArg.setText(wrap(result.getQuoteArgument()));
            lblComplete.setText(StringUtil.yesNo(result.isComplete()));
            lblArguments.setText(wrap(result.getArguments()));
            lblArgRangeMap.setText(wrap(result.getTopLevelArgumentRanges()));
            lblTlArgMap.setText(wrap(result.getTopLevelArgumentMap()));
        }

        private String wrap(Map<String, String[]> map) {
            if(map == null) {
                return "<html><font color='blue'><i>(NULL)</i></font></html>";
            }
            if(map.isEmpty()) {
                return "<html><font color='blue'><i>(EMPTY)</i></font></html>";
            }
            StringBuilder buffer = new StringBuilder("<html>");
            for(String key : map.keySet()) {
                buffer.append(wrapNoHtml(key) + "=");
                String[] val = map.get(key);
                if(val == null) {
                    buffer.append("<font color='blue'><i>(NULL)</i></font>");
                } else {
                    for(String v : val) {
                        buffer.append(wrapNoHtml(v));
                    }
                }
                buffer.append(" ");
            }
            return buffer.toString().trim() + "</html>";
        }

        private String wrap(ArgumentRangeMap map) {
            if(map == null) {
                return "<html><font color='blue'><i>(NULL)</i></font></html>";
            }
            if(map.isEmpty()) {
                return "<html><font color='blue'><i>(EMPTY)</i></font></html>";
            }
            return map.toString().replaceAll("\\];", ");").replaceAll("\\]$", ")");
        }

        String wrap(String[] args) {
            if(args == null) {
                return "<html><font color='blue'><i>(NULL)</i></font></html>";
            }
            if(args.length == 0) {
                return "<html><font color='blue'><i>(EMPTY)</i></font></html>";
            }
            StringBuilder buffer = new StringBuilder("<html>");
            for(String arg : args) {
                buffer.append(wrapNoHtml(arg));
                buffer.append(" ");
            }
            return buffer.toString().trim() + "</html>";
        }
        String wrap(Object a) {
            if(a == null) {
                return "<html><font color='blue'><i>(NULL)</i></font></html>";
            }
            return "<html><font color='red'>[</font>" + a + "<font color='red'>]</font></html>";
        }
        String wrapNoHtml(String a) {
            if(a == null) {
                return "<font color='blue'><i>(NULL)</i></font>";
            }
            return "<font color='red'>[</font>" + a + "<font color='red'>]</font>";
        }
        @Override
        protected void addFields() {
            addField("Main", "Orig Line",   lblOrigLine    = Lay.lb("", "font=Courier-New"), 60);
            addField("Main", "State",       lblResult      = Lay.lb("", "font=Courier-New"), 30);
            addField("Main", "Cur Arg",     lblCurArg      = Lay.lb("", "font=Courier-New"), 35);
            addField("Main", "Free Arg",    lblFreeArg     = Lay.lb("", "font=Courier-New"), 35);
            addField("Main", "Quote Arg",   lblQuoteArg    = Lay.lb("", "font=Courier-New"), 35);
            addField("Main", "Complete?",   lblComplete    = Lay.lb("", "font=Courier-New"), 35);
            addField("Main", "Arguments",   lblArguments   = Lay.lb("", "font=Courier-New"), 60);
            addField("Main", "Arg Ranges",  lblArgRangeMap = Lay.lb("", "font=Courier-New"), 60);
            addField("Main", "TL Arg Map*", lblTlArgMap    = Lay.lb("", "font=Courier-New"), 60);
        }
        @Override
        protected boolean showCancelButton() {
            return false;
        }
        @Override
        protected boolean showSaveButton() {
            return false;
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        BashTestFrame frame = new BashTestFrame();
        frame.setVisible(true);
    }
}
