package finio.platform.exts.view.consoleview.commands;

import java.util.Date;
import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class DateCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "date";
    }
    @Override
    public String getDescription() {
        return "Prints the current date and time.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(0);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        context.appendlnStdOut(new Date().toString());
    }
}
