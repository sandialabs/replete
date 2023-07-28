package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class ListKeysCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "ls";
    }
    @Override
    public String getDescription() {
        return "Prints the keys in a map to the console.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser();
    }

    // MINOR BUG:
    //
    // /$ ls Realm0-JSONLiteral/Colorado/ Realm0-JSONLiteral/@@a-meta
    // Realm0-JSONLiteral/Colorado/:    <-- Why so inconsistent?  (/)
    // capital
    // admission
    // Realm0-JSONLiteral/@@a-meta:     <-- Why so inconsistent?  (no /)
    // source
    // time

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
        for(Object K : M.K()) {
            context.appendlnStdOut(K);
        }
    }
}
