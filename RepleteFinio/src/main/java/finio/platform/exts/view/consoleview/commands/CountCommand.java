package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class CountCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "count";
    }
    @Override
    public String getDescription() {
        return "Counts the number of keys in a map to the console.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser();
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        if(parts.length == 0) {
            handlePart(context, null, parts.length);
            return;
        }
        for(String part : parts) {
            try {
                handlePart(context, part, parts.length);
            } catch(CommandException e) {
                context.appendlnStdErr(this, e.getMessage());
            }
        }
    }
    private void handlePart(ConsoleContext context, String part, int partCount) throws CommandException {

        // Get and check path
        KeyPath P = resolveKeyPath(context, part);
        checkKeyPathExists(context, P, part);

        // Get and check value
        Object V = context.getW().getByPath(P);
        checkIsNonTerminal(V, part);

        // Output to console
        if(partCount > 1) {
            context.appendlnStdOut(part + ":");
        }
        NonTerminal M = (NonTerminal) V;
        context.appendlnStdOut(M.size());
    }
}
