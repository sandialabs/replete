package finio.platform.exts.view.consoleview.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.events.MapListener;
import finio.platform.exts.view.consoleview.commands.Command;
import finio.platform.exts.view.consoleview.commands.CommandException;
import finio.plugins.extpoints.View;
import finio.ui.FontConstants;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.view.ViewPanel;
import finio.ui.worlds.WorldContext;
import replete.cli.CommandLineParser;
import replete.cli.bash.BashCommandLineParseException;
import replete.cli.bash.BashCommandLineParser;
import replete.cli.bash.ParseResult;
import replete.cli.bash.ParseState;
import replete.cli.errors.CommandLineParseException;
import replete.cli.options.Option;
import replete.errors.ExceptionUtil;
import replete.event.ChangeNotifier;
import replete.event.rnotif.RChangeEvent;
import replete.event.rnotif.RChangeListener;
import replete.text.StringUtil;
import replete.ui.button.IconButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.text.RTextField;
import replete.ui.text.RTextPane;
import replete.ui.uiaction.UIActionPopupMenu;
import replete.ui.windows.Dialogs;

public class ConsolePanel extends ViewPanel implements ConsoleContext {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static final String PATH_SEGMENT_SEPARATOR = "/";
    public static final String ROOT_NAME = "/";
    public static final String PARENT_SEGMENT = "..";  // Or KeyPath.PARENT
    public static final String CURRENT_SEGMENT = ".";  // Or KeyPath.CURRENT
    private static final Color MGMT_COLOR = Lay.clr("FF9B4C");
    private static final Color PROMPT_COLOR = Lay.clr("191DFF");
    private static final Font INPUT_FONT = new Font("Courier New", Font.BOLD, 14);

    // UI

    private int fontSize = 12;
    private Font consoleFontPlain = new Font("Courier New", Font.PLAIN, fontSize);
    private Font consoleFontBold = new Font("Courier New", Font.BOLD, fontSize);
    private RTextPane txtConsole;
    private RTextField txtInput;
    private int lastPromptPosition;      // Not used yet

    private BashCommandLineParser parser = new BashCommandLineParser();
    private Map<String, Command> commands = new TreeMap<>();
    private boolean suppressDuplicateShowWS = false;

    private boolean suppressSelectAllOnce = false;

    private Map<String, String> aliases = new HashMap<>();
    private List<String> history = new ArrayList<>();
    private int showHistory = -1;
    private String historyPrefix = null;

    private NonTerminal Mcontext;
    private JLabel lblWorkingScope;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ConsolePanel(AppContext ac, WorldContext wc, Object K, Object V, View view) {
        super(ac, wc, K, V, view);

        Mcontext = (NonTerminal) V;

        history.add("abc1");
        history.add("abc2");
        history.add("abcxyz1");
        history.add("abcxyz2");
        history.add("abc3");
        history.add("abc4");
        history.add("abcxyz3");
        history.add("abcxyz4");
        history.add("abc5");
        history.add("abc6");
        showHistory = history.size();

        ac.addConsoleOutputListener(new ConsoleOutputListener() {
            public void output(ConsoleOutputEvent event) {
                appendlnStdOut("");
                appendlnStdOut(event.getOutput());
                showPrompt();
            }
        });

        IconButton btnExecute;
        IconButton btnClear;
        Lay.BLtg(this,
            "N", Lay.BL(
                "W", Lay.lb("Working Scope: ", FinioImageModel.WORKING_SCOPE, "fg=992F2C,font=Verdana,bold"),
                "C", lblWorkingScope = Lay.lb("", "font=Verdana,fg=E24741,bold"),
                "bg=FFF7F7,eb=1"
            ),

//            "N", new TitlePanel("Console", ImageLib.get(CommonConcepts.CONSOLE)),
//            "C", Lay.BL(
                "C", Lay.sp(txtConsole = Lay.txp()),
                "S", Lay.BL(
                    "C", Lay.p(txtInput = Lay.tx("")),
                    "E", Lay.FL("L", "hgap=0,vgap=0",
                        btnExecute = new IconButton(CommonConcepts.RUN, "Execute", 2),
                        btnClear = new IconButton(CommonConcepts.CLEAR, "Clear Console", 2)
                    )
                )
//            )
        );

        txtInput.setFont(INPUT_FONT);
        txtInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if(!suppressSelectAllOnce) {
                    txtInput.selectAll();
                } else {
                    suppressSelectAllOnce = false;
                }
            }
        });

        // Set up tab key recognition for input text field.
        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_TAB){
                    doAutoComplete();

                } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                    if(history.size() != 0 && showHistory > 0) {

                        // If there is no prefix yet and we are just starting
                        // to cycle through the history...
                        if(historyPrefix == null && showHistory == history.size()) {

                            // If there is some text typed in the input field,
                            // make that the history prefix we're searching for.
                            String cur = txtInput.getText().trim();
                            if(!cur.equals("")) {
                                historyPrefix = cur;
                            } else {
                                // No need to set prefix b/c blank
                            }
                        }

                        // If the history prefix is still unset, then simply
                        // move one position up in the history.
                        if(historyPrefix == null) {
                            showHistory--;

                        // Else search backwards through the history for that
                        // prefix.
                        } else {
                            for(int h = showHistory - 1; h >= 0; h--) {
                                if(history.get(h).startsWith(historyPrefix)) {
                                    showHistory = h;
                                    break;
                                }
                            }
                        }

                        txtInput.setText(history.get(showHistory));
                        if(historyPrefix != null) {
                            txtInput.setCaretPosition(historyPrefix.length());
                        }
                    }

                } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if(history.size() != 0 && showHistory < history.size()) {
                        if(historyPrefix == null) {
                            showHistory++;
                        } else {
                            boolean found = false;
                            for(int h = showHistory + 1; h < history.size(); h++) {
                                if(history.get(h).startsWith(historyPrefix)) {
                                    showHistory = h;
                                    found = true;
                                    break;
                                }
                            }
                            if(!found) {
                                showHistory = history.size();
                            }
                        }
                        if(showHistory < history.size()) {
                            txtInput.setText(history.get(showHistory));
                            if(historyPrefix != null) {
                                txtInput.setCaretPosition(historyPrefix.length());
                            }
                        } else {
                            txtInput.setText("");
                            historyPrefix = null;
                        }
                    }
                }
            }
        });
        txtInput.setFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        txtInput.addActionListener(executeListener);

        btnExecute.addActionListener(executeListener);
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearText();
                showPrompt();
            }
        });

        txtConsole.setEditable(false);
        txtConsole.setFont(consoleFontPlain);
        txtConsole.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fireSelectedNotifier();
            }
        });
        txtConsole.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                txtConsole.focus();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                showContextMenu(e);
            }
        });
        txtInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fireSelectedNotifier();
            }
        });
        btnExecute.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fireSelectedNotifier();
            }
        });
        btnClear.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fireSelectedNotifier();
            }
        });

        addWorkingScopeListener(wsListener);

        for(Command cmd : CommandList.COMMANDS) {
            commands.put(cmd.getName(), cmd);
        }
        showPrompt();
        updateWorkingScopeErrorIcon();
    }
    public void updateWorkingScopeErrorIcon() {
        KeyPath scope = getWorkingScope();
        String path = scope.toUnixString();
        lblWorkingScope.setText(path);
    }

    protected void showContextMenu(MouseEvent e) {
        if(e.isPopupTrigger()) {
            JPopupMenu mnuPopup = createPopupMenu(e);
            if(mnuPopup != null) {
                mnuPopup.show(this, e.getX(), e.getY());
            }
        }
    }

    protected JPopupMenu createPopupMenu(MouseEvent e) {
        JPopupMenu mnuPopup = new UIActionPopupMenu(ac.getActionMap());
//        JMenuItem mnuAnchor = ac.getActionMap().getPopupMenuComponent("toggle-anchor");
//        if(mnuAnchor != null) {
//            mnuAnchor.setText(true/*shouldUnanchor()*/ ? "(NOTIMPL)Un&anchor" : "&Anchor");
//        }
//        JMenuItem mnuPause = ac.getActionMap().getPopupMenuComponent("toggle-pause");
//        if(mnuPause != null) {
//            mnuPause.setText(true/*shouldUnpause()*/ ? "(NOTIMPL)Un&pause" : "&Pause");
//        }
        return mnuPopup;
    }
    private RChangeListener wsListener = new RChangeListener() {
        public void handle(RChangeEvent e) {
            if(!suppressDuplicateShowWS) {
                showPrompt();
            }
            updateWorkingScopeErrorIcon();
        }
    };


    ///////////////////
    // AUTO COMPLETE //
    ///////////////////

    protected void doAutoComplete() {
        ParseResult result = parser.parseWithResult(txtInput.getText());

        // Incomplete first argument
        // $ abc'123|
        if(result.getArguments().length == 0 && !result.isComplete()) {
            appendCommandNamesAutoComplete(result);

        // Zero or more space characters
        // $ |
        // $   |
        } else if(result.getArguments().length == 0 && result.isComplete()) {
            appendCommandNamesAutoComplete(result);

        // Cursor still on first argument.
        // $ dat|
        } else if(result.getArguments().length == 1 && result.getState() == ParseState.FREE) {
            appendCommandNamesAutoComplete(result);

        // Command-driven auto completion
        // $ echo arg|
        // $ cmd arg1 arg2 arg"3|
        // $ pr "ar|
        } else {
            String[] parts = result.getArguments();
            String cmdStr = parts[0];
            AliasReplacementResult aliasResult = new AliasReplacementResult(cmdStr);
            if(aliasResult.isChanged()) {
                cmdStr = aliasResult.getChangedCommandString();
            }
            if(commands.containsKey(cmdStr)) {
                Command cmd = commands.get(cmdStr);
//System.out.println(txtInput.getCaretPosition());
                AutoCompleteState acs = cmd.getAutoCompleteState(this, result);
                if(acs != null) {
                    appendAutoCompleteMatching(result, acs);
                }
            }
        }
    }

    private void appendCommandNamesAutoComplete(ParseResult result) {
        List<String> matching = new ArrayList<String>();
        List<String> possibleNames = new ArrayList<String>();
        for(String name : commands.keySet()) {
            possibleNames.add(name);
        }
        for(String alias : aliases.keySet()) {
            possibleNames.add(alias);
        }
        for(String name : possibleNames) {
            if(name.startsWith(result.getArgumentPrefix())) {
                matching.add(name);
            }
        }
        AutoCompleteState acs = new AutoCompleteState(
            null, result.getArgumentPrefix(), " ", matching, "Possible Commands:");
        appendAutoCompleteMatching(result, acs);
    }

    private void appendAutoCompleteMatching(ParseResult result, AutoCompleteState acs) {
        List<String> matching = acs.getMatching();
        if(matching.size() == 1) {
            String m = matching.get(0);
            txtInput.append(StringUtil.snip(m, acs.getMatchedPrefix()));  // TODO: ERROR HERE when append have spaces -- need to add quotes
            if(result.getState().equals(ParseState.SINGLE_QUOTES)) {
                txtInput.append("'");
            } else if(result.getState().equals(ParseState.DOUBLE_QUOTES)) {
                txtInput.append("\"");
            }
            txtInput.append(acs.getSingleMatchSep());
        } else if(matching.size() > 1) {
            appendlnStdOut("");
            int pos = acs.getMatchedPrefix().length();
            String append = "";
            while(true) {
                boolean allHaveSameChar = true;
                char ch = 0;
                for(int j = 0; j < matching.size(); j++) {
                    String mm = matching.get(j);
                    if(mm.length() == pos) {
                        allHaveSameChar = false;
                        break;
                    }
                    if(j == 0) {
                        ch = mm.charAt(pos);
                    }
                    if(mm.charAt(pos) != ch) {
                        allHaveSameChar = false;
                        break;
                    }
                }
                if(allHaveSameChar) {
                    append += ch;
                    pos++;
                } else {
                    break;
                }
            }
            txtInput.append(append);  // TODO: ERROR HERE when append have spaces -- need to add quotes
            if(acs.getUserMessage() != null) {
                appendlnMgmt(acs.getUserMessage());
            }
            for(String match : matching) {
                appendlnStdOut(match);
            }
            showPrompt();
        }
    }


    //////////////
    // LISTENER //
    //////////////

    ActionListener executeListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            suppressDuplicateShowWS = true;

            String cmdStr = txtInput.getText();

            // Append typed text
            appendCommand(cmdStr);

            // Parse command line into parts
            Command cmd = null;
            try {

                // Look up aliases
                AliasReplacementResult aliasResult = new AliasReplacementResult(cmdStr);
                if(aliasResult.isChanged()) {
                    appendlnMgmt(
                        "Alias change: [" + aliasResult.getFoundAlias() + "] => [" +
                        aliasResult.getFoundReplacement() +
                        "]; Command: [" + aliasResult.getChangedCommandString() + "]"
                    );
                    cmdStr = aliasResult.getChangedCommandString();
                }

                String[] parts = parser.parse(cmdStr);
                if(parts.length != 0) {
                    history.add(cmdStr);
                    showHistory = history.size();
                    historyPrefix = null;
                    if(commands.containsKey(parts[0])) {
                        cmd = commands.get(parts[0]);
                        String[] arguments = Arrays.copyOfRange(parts, 1, parts.length);
                        CommandLineParser clp = cmd.getCommandLineParser();
                        String[] nonOptionArgs;
                        Map<Option, Object[]> clOpts;
                        if(clp != null) {
                            clp.parse(arguments);
                            nonOptionArgs = clp.getNonOptionArguments();
                            clOpts = clp.getOptionValuesMap();
                        } else {
                            nonOptionArgs = arguments;
                            clOpts = null;
                        }
                        cmd.execute(ConsolePanel.this, nonOptionArgs, clOpts);
                    } else {
                        appendlnStdErr("Unrecognized command: " + parts[0]);
                    }
                }

            } catch(BashCommandLineParseException ex) {
                appendlnStdErr(ex.getMessage());

            } catch(CommandException ex) {
                appendlnStdErr(cmd, ex.getMessage());

            } catch(CommandLineParseException ex) {
                appendlnStdErr(cmd, ex.getMessage());

            } catch(Exception ex) {
                String text = ExceptionUtil.toCompleteString(ex, 4);
                appendlnStdErr(cmd, text.trim());
                Dialogs.showDetails(ConsolePanel.this,
                    "An unexpected error has occurred with the command.",
                    "Command Error", ex);

            } finally {
                showPrompt();
                suppressDuplicateShowWS = false;
                txtInput.requestFocusInWindow();
                txtInput.setText("");
            }

            fireAnyActionNotifier();
        }
    };


    /////////////
    // CONTEXT //
    /////////////

//    public void replacePrompt(String string) {
//        int len = txtConsole.getDocument().getLength() - lastPromptPosition;
//        try {
//            txtConsole.getDocument().remove(lastPromptPosition, len);
//        } catch(BadLocationException e) {
//            e.printStackTrace();
//        }
//    }
    public void showPrompt() {
        String path = getWorkingScope().toUnixString();
        appendPrompt(path + "$ ");
    }
    private void ensureNewLine() {
        String content = txtConsole.getText();
        int len = content.length();
        if(len != 0 && content.charAt(len - 1) != '\n') {
            txtConsole.append("\n", consoleFontBold, PROMPT_COLOR);
        }
    }
    public void appendPrompt(String string) {
        ensureNewLine();
        txtConsole.append(string, consoleFontBold, PROMPT_COLOR);
        lastPromptPosition = txtConsole.getDocument().getLength();
        txtConsole.setCaretPosition(lastPromptPosition);
    }
    public void appendCommand(String string) {
        txtConsole.append(string + "\n", consoleFontBold, Color.black);
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }
    public void appendlnStdOut(Object obj) {
        appendlnStdOut(obj.toString());
    }
    public void appendlnStdOut(String string) {
        txtConsole.append(string + "\n", consoleFontPlain, Color.black);
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }
    public void appendStdOut(String string) {
        txtConsole.append(string, consoleFontPlain, Color.black);
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }
    public void appendlnStdErr(String string) {
        txtConsole.append(string + "\n", consoleFontBold, Color.red);
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }
    public void appendlnStdErr(Command cmd, String message) {
        appendlnStdErr(cmd.getName() + ": " + message);
    }

    public void appendlnMgmt(String string) {
        ensureNewLine();
        txtConsole.append(string + "\n", consoleFontBold, MGMT_COLOR);
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }
    public void appendln(String string, Color color) {
        txtConsole.append(string + "\n", consoleFontBold, color);
        txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
    }

    public void clearText() {
        txtConsole.setText("");
    }
    public NonTerminal getW() {
        return Mcontext;
    }
    @Override
    public Map<String, String> getEnvironment() {
        return parser.getEnvironment();
    }
    @Override
    public Map<String, String> getAliases() {
        return aliases;
    }
    @Override
    public List<String> getHistory() {
        return history;
    }
    @Override
    public Map<String, Command> getCommands() {
        return commands;
    }

    @Override
    public void focus() {
        txtInput.requestFocusInWindow();
    }

    public void addSelectedKeyPaths(KeyPath[] selectedPaths, boolean insertSpaceAtBeginning) {
        String curLine = txtInput.getText();
        for(KeyPath path : selectedPaths) {
            if(insertSpaceAtBeginning) {
                if(curLine.length() == 0 || !Character.isWhitespace(curLine.charAt(curLine.length() - 1))) {
                    curLine += " ";
                }
            } else {
                if(curLine.length() != 0 && !Character.isWhitespace(curLine.charAt(curLine.length() - 1))) {
                    curLine += " ";
                }
            }
            String pathStr = path.toUnixString();
            if(pathStr.matches("^.*\\s.*$")) {
                pathStr = "'" + pathStr + "'";
            }
            curLine += pathStr;
        }
        txtInput.setText(curLine);
    }

    public void focusInput() {
        txtInput.requestFocusInWindow();
    }

    @Override
    public void requestExit() {
        ac.requestExit();
    }

    private ChangeNotifier anyActionNotifier = new ChangeNotifier(this);
    @Override
    public void addAnyActionListener(ChangeListener listener) {
        anyActionNotifier.addListener(listener);
    }
    private void fireAnyActionNotifier() {
        anyActionNotifier.fireStateChanged();
    }

    public void focusInputAndHighlightCommand() {
        suppressSelectAllOnce = true;
        focusInput();
        txtInput.setCaretPosition(0);
        for(Command cmd : CommandList.COMMANDS) {
            if(txtInput.getText().matches("^" + cmd.getName() + "\\s.*$")) {
                txtInput.setSelectionStart(0);
                txtInput.setSelectionEnd(cmd.getName().length());
                break;
            }
        }
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class AliasReplacementResult {
        public boolean changed;
        public String cmdStr;
        public String foundAlias;
        public String foundReplacement;

        public AliasReplacementResult(String cmdStrOrig) {
            cmdStr = cmdStrOrig;
            changed = false;
            foundAlias = null;
            foundReplacement = null;

            for(String alias : aliases.keySet()) {
                if(cmdStrOrig.matches("^\\s*" + alias + "$") || cmdStrOrig.matches("^\\s*" + alias + "\\s.*$")) {
                    foundAlias = alias;
                    foundReplacement = aliases.get(alias);  // Surround in single quotes?
                    int startAlias = cmdStrOrig.indexOf(alias.charAt(0));
                    cmdStr = cmdStrOrig.substring(0, startAlias) +
                                    foundReplacement +
                             cmdStrOrig.substring(alias.length() + startAlias);
                    changed = true;
                    break;
                }
            }
        }

        public boolean isChanged() {
            return changed;
        }
        public String getFoundAlias() {
            return foundAlias;
        }
        public String getFoundReplacement() {
            return foundReplacement;
        }
        public String getChangedCommandString() {
            return cmdStr;
        }
    }

    @Override
    public MapListener createMapListener(ListenType type, KeyPath P) {
        return new ConsoleMapSuperListener(this, type, P);
    }

    @Override
    public void increaseFont() {
        fontSize += FontConstants.INC_DEC_AMOUNT;
        updateStyles();
    }
    @Override
    public void decreaseFont() {
        fontSize -= FontConstants.INC_DEC_AMOUNT;
        if(fontSize < FontConstants.MIN_SIZE) {
            fontSize = FontConstants.MIN_SIZE;
        }
        updateStyles();
    }
    private void updateStyles() {
        Element rootElem = txtConsole.getDocument().getDefaultRootElement();
        DefaultStyledDocument doc = (DefaultStyledDocument) rootElem.getDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        attrs.addAttribute(StyleConstants.FontSize, fontSize);
        doc.setCharacterAttributes(0, Integer.MAX_VALUE, attrs, false);

        consoleFontPlain = new Font("Courier New", Font.PLAIN, fontSize);
        consoleFontBold = new Font("Courier New", Font.BOLD, fontSize);
    }

    @Override
    public void init() {

    }

    @Override
    public SelectionContext[] getSelectedValues(int reverseDepth) {
        KeyPath P = getWorkingScope();
        SelectionContext C = new SelectionContext();
        for(int i = 0; P != null && i < reverseDepth; i++) {
            Object K = P.isEmpty() ? null : P.last();
            Object V = Mcontext.getByPath(P);
            C.addSegment(new ConsolePanelSelectionContextSegment(K, V, P));
            if(P.isEmpty()) {
                P = null;
            } else {
                P = P.withoutLast();
            }
        }
        return new SelectionContext[] {C};
    }

    @Override
    public void addSelection(SelectRequest selectionRequest) {
        if(selectionRequest.getAction() == SelectAction.ROOT) {
            setWorkingScope(new KeyPath());

        } else {
            ConsolePanelSelectionContextSegment S =
                (ConsolePanelSelectionContextSegment)
                    selectionRequest.getContext().getSegment(0);
            KeyPath P = S.getP();

            switch(selectionRequest.getAction()) {
                case SELF:
                    setWorkingScope(P);
                    break;

                case CHILD:
                    Object K = selectionRequest.getArgs()[0];
                    setWorkingScope(P.appended(K));
                    break;

//                case CHILDREN:    (Not supported by FConsolePanel)
//                    break;

                case SIBLING:
                    K = selectionRequest.getArgs()[0];
                    setWorkingScope(P.withoutLast().appended(K));
                    break;

                case PARENT:
                    ConsolePanelSelectionContextSegment Sgp =
                        (ConsolePanelSelectionContextSegment)
                            selectionRequest.getContext().getSegment(2);
                    KeyPath Pgrandparent = Sgp.getP();
                    Object Kparent = selectionRequest.getArgs()[0];
                    setWorkingScope(Pgrandparent.appended(Kparent));
                    break;

                default:
                    // Won't happen
                    break;
            }
        }
    }

    private ChangeNotifier selectedNotifier = new ChangeNotifier(this);
    @Override
    public void addSelectedListener(ChangeListener listner) {
        selectedNotifier.addListener(listner);
    }
    @Override
    public void removeSelectedListener(ChangeListener listener) {
        selectedNotifier.removeListener(listener);
    }
    private void fireSelectedNotifier() {
        selectedNotifier.fireStateChanged();
    }
}
