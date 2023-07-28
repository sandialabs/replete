package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class SetCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "set";
    }
    @Override
    public String getDescription() {
        return "Sets an environment variable.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(2, 2);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        context.getEnvironment().put(parts[0], parts[1]);
    }
}
