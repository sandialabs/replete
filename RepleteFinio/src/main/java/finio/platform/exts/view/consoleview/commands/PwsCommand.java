package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class PwsCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "pws";
    }
    @Override
    public String getDescription() {
        return "Prints the current working scope to the console.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(0);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        KeyPath P = resolveKeyPath(context, null);
        context.appendlnStdOut("Working Scope: " + P);
    }
}
