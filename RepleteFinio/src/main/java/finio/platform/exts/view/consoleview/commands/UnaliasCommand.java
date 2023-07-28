package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class UnaliasCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "unset";
    }
    @Override
    public String getDescription() {
        return "Unsets an environment variable.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(1);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        context.getEnvironment().remove(parts[0]);
    }
}
