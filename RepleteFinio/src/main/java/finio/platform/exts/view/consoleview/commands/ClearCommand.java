package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class ClearCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "clear";
    }
    @Override
    public String getDescription() {
        return "Clears the text in the console.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(0);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        context.clearText();
    }
}
