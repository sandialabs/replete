package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class RemoveCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "rm";
    }
    @Override
    public String getDescription() {
        return "Removes a non-terminal or terminal from its parent non-terminal.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser();
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        if(parts.length == 0) {
            handlePart(context, null);
            return;
        }
        for(String part : parts) {
            try {
                handlePart(context, part);
            } catch(CommandException e) {
                context.appendlnStdErr(this, e.getMessage());
            }
        }
    }
    private void handlePart(ConsoleContext context, String part) throws CommandException {

        // Get and check path
        KeyPath P = resolveKeyPath(context, part);
        checkIsNotRoot(P, "Cannot remove the root scope.");
        checkKeyPathExists(context, P, part);

        // Separate last key and get parent map
        Object Klast = P.removeLast();
        NonTerminal Mparent = (NonTerminal) context.getW().getByPath(P);

        // Edit map
        Mparent.removeByKey(Klast);
    }
}
