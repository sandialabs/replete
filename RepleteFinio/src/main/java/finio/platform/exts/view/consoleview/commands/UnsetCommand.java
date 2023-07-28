package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class UnsetCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "unalias";
    }
    @Override
    public String getDescription() {
        return "Removes an alias.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(1);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        context.getAliases().remove(parts[0]);
    }
}
