package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import finio.platform.exts.view.consoleview.ui.ConsolePanel;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class ChangeKeyCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "chg";
    }
    @Override
    public String getDescription() {
        return "Replaces a key with another but does not change the value the key is pointing to.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(2, 2);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {

        // Get and check path
        KeyPath P = resolveKeyPath(context, parts[0]);
        checkIsNotRoot(P, "Cannot change the root scope.");
        checkKeyPathExists(context, P, parts[0]);

        // Separate last key and get parent map
        Object Klast = P.removeLast();
        NonTerminal Mparent = (NonTerminal) context.getW().getByPath(P);

        // Validate new key
        String Knew = parts[1];
        if(Knew.contains(ConsolePanel.PATH_SEGMENT_SEPARATOR)) {
            throw new CommandException("Key cannot contain a path separator.  Second chg parameter is a simple name." );
        }

        // Edit map
        Mparent.changeKey(Klast, Knew);
    }
}
