package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class ListenersCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "listeners";
    }
    @Override
    public String getDescription() {
        return "Prints all of a map's listeners to the console.";
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
        printListeners(context, M.getMapChangedListeners(), "Map Changed Listeners");
        printListeners(context, M.getMapClearedListeners(), "Map Cleared Listeners");
        printListeners(context, M.getKeyAddedListeners(), "Key Added Listeners");
        printListeners(context, M.getKeyRemovedListeners(), "Key Removed Listeners");
        printListeners(context, M.getKeyChangedListeners(), "Key Changed Listeners");
        printListeners(context, M.getValueChangedListeners(), "Value Changed Listeners");
    }
    private void printListeners(ConsoleContext context, Object[] listeners, String string) {
        if(listeners.length != 0) {
            context.appendlnStdOut(string);
            for(Object listener : listeners) {
                context.appendlnStdOut(" -- " + listener);
            }
        }
    }
}
