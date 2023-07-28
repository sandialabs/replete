package finio.platform.exts.view.consoleview.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.errors.KeyPathException;
import finio.platform.exts.view.consoleview.ui.AutoCompleteState;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import finio.platform.exts.view.consoleview.ui.ConsolePanel;
import replete.cli.CommandLineParser;
import replete.cli.bash.ParseResult;
import replete.cli.options.Option;


public abstract class Command {


    //////////////
    // ABSTRACT //
    //////////////

    public abstract String getName();
    public abstract String getDescription();
    public void execute(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        try {
            executeInner(context, parts, options);
        } catch(KeyPathException ex) {
            throw new CommandException(ex.getMessage());
        }
    }
    protected abstract void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException;
    public CommandLineParser getCommandLineParser() {   // Intended to be overridden
        return null;   // Implies NO restrictions on command line arguments
    }
    public AutoCompleteState getAutoCompleteState(ConsoleContext context, ParseResult result) {  // Intended to be overridden
        return getDefaultAutoCompletePaths(context, result.getArgumentPrefix());
    }


    ////////////
    // HELPER //
    ////////////

    // Helper method for all commands
    public KeyPath resolveKeyPath(ConsoleContext context, String arg) throws KeyPathException {
        KeyPath P;
        boolean abs;
        if(arg == null) {
            P = KeyPath.KP(KeyPath.CURRENT);
            abs = false;
        } else {
            if(arg.startsWith(ConsolePanel.ROOT_NAME)) {
                arg = arg.substring(ConsolePanel.ROOT_NAME.length());
                if(arg.equals("")) {
                    P = KeyPath.KP();
                } else {
                    P = KeyPath.KP(arg, ConsolePanel.PATH_SEGMENT_SEPARATOR);
                }
                abs = true;
            } else {
                P = KeyPath.KP(arg, ConsolePanel.PATH_SEGMENT_SEPARATOR);
                abs = false;
            }
        }
        KeyPath pathContext = abs ? null : context.getWorkingScope();
        KeyPath pathNew = P.resolve(pathContext);
        return pathNew;
    }
    private KeyPath stringPathToKeyPathWithoutResolution(String arg) {
        if(arg.startsWith(ConsolePanel.ROOT_NAME)) {
            arg = arg.substring(ConsolePanel.ROOT_NAME.length());
            if(arg.equals("")) {
                return KeyPath.KP();
            }
        }
        return KeyPath.KP(arg, ConsolePanel.PATH_SEGMENT_SEPARATOR);
    }
    protected AutoCompleteState getDefaultAutoCompletePaths(ConsoleContext context, String prefix) {
        KeyPath temp = stringPathToKeyPathWithoutResolution(prefix);
        String Klast;
        KeyPath P;

        // Auto complete on root alone.
        if(temp.isEmpty()) {
            P = temp;
            Klast = "";

        } else {

            // We don't want active (exception-throwing) resolution
            // with the pseudo keys.  Each will fail in different
            // way.
            if((temp.last().equals(ConsolePanel.PARENT_SEGMENT) ||
               temp.last().equals(ConsolePanel.CURRENT_SEGMENT)) &&
               !prefix.endsWith(ConsolePanel.PATH_SEGMENT_SEPARATOR)) {
                P = temp;
                Klast = (String) P.removeLast();

            // Normal paths here...
            } else {
                try {
                    P = resolveKeyPath(context, prefix);
                } catch(Exception e) {
                    return null;
                }
                if(prefix.endsWith(ConsolePanel.PATH_SEGMENT_SEPARATOR)) {
                    Klast = "";
                } else {
                    Klast = (String) P.removeLast();
                }
            }
        }

        NonTerminal W = context.getW();
        if(W.hasPath(P)) {
            List<String> matches = new ArrayList<>();
            NonTerminal M = (NonTerminal) W.getByPath(P);
            Object Ksave = null;
            for(Object K : M.K()) {
                if(((String) K).startsWith(Klast)) {
                    matches.add((String) K);
                    Ksave = K;
                }
            }
            if(ConsolePanel.PARENT_SEGMENT.startsWith(Klast)) {   // Get rid of magic strings
                matches.add(ConsolePanel.PARENT_SEGMENT);
                Ksave = ConsolePanel.PARENT_SEGMENT;
            }
            if(ConsolePanel.CURRENT_SEGMENT.startsWith(Klast)) {
                matches.add(ConsolePanel.CURRENT_SEGMENT);
                Ksave = ConsolePanel.CURRENT_SEGMENT;
            }
            String singleMatchSep = null;
            if(matches.size() == 1) {
                if(Ksave.equals(ConsolePanel.PARENT_SEGMENT) ||
                   Ksave.equals(ConsolePanel.CURRENT_SEGMENT)) {
                    singleMatchSep = ConsolePanel.PATH_SEGMENT_SEPARATOR;
                } else {
                    Object V = M.get(Ksave);
                    if(V instanceof NonTerminal) {
                        singleMatchSep = ConsolePanel.PATH_SEGMENT_SEPARATOR;
                    } else {
                        singleMatchSep = " ";
                    }
                }
            }
            if(matches.size() != 0) {
                int splitIndex = prefix.length() - Klast.length();
                String fixedPrefix = prefix.substring(0, splitIndex);
                String matchedPrefix = prefix.substring(splitIndex);
                return new AutoCompleteState(
                    fixedPrefix,
                    matchedPrefix,
                    singleMatchSep,
                    matches,
                    "Possible Keys:");
            }
            return null;
        }
        return null;
    }

    // Not root check

    protected void checkIsNotRoot(KeyPath P) throws CommandException {
        checkIsNotRoot(P, "Operation not valid on the root map");
    }
    protected void checkIsNotRoot(KeyPath P, String message) throws CommandException {
        if(P.size() == 0) {
            throw new CommandException(message);
        }
    }

    // Key path exists

    // Duplicate in DataModel
    protected void checkKeyPathExists(ConsoleContext context, KeyPath P, String source) throws CommandException {
        if(!context.getW().hasPath(P)) {
            throw new CommandException("Key path does not exist" +
                (source != null ? ": " + source : ""));
        }
    }

    // Non-terminal check

    // duplicate in DataModel
    protected void checkIsNonTerminal(Object V) throws CommandException {
        checkIsNonTerminal(V, "Operation valid only on non-terminal values", null);
    }
    protected void checkIsNonTerminal(Object V, String source) throws CommandException {
        checkIsNonTerminal(V, "Operation valid only on non-terminal values", source);
    }
    protected void checkIsNonTerminal(Object V, String message, String source) throws CommandException {
        if(!(V instanceof NonTerminal)) {
            throw new CommandException(message +
                (source != null ? ": " + source : ""));
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return getName();
    }
}
