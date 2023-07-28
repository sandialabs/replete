package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class ClearMapCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "cl";
    }
    @Override
    public String getDescription() {
        return "Removes all key-value pairs in a map.";
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
        checkKeyPathExists(context, P, part);

        // Get and check value
        Object V = context.getW().getByPath(P);
        checkIsNonTerminal(V, part);

        // Edit map
        NonTerminal M = (NonTerminal) V;
        M.clear();
    }
}
