package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.options.Option;

public class TestCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "test";
    }
    @Override
    public String getDescription() {
        return "Prints the recognized command line arguments to the console.";
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        context.appendlnStdOut("# args: " + parts.length);
        for(int i = 0; i < parts.length; i++) {
            context.appendlnStdOut("  " + i + ": <" + parts[i] + ">");
        }
    }
}
