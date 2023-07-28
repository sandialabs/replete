package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class ExitCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "exit";
    }
    @Override
    public String getDescription() {
        return "Exits the application.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(0);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        context.requestExit();
    }
}
