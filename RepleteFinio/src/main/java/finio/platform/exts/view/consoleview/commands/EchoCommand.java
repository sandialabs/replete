package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.options.Option;

public class EchoCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "echo";
    }
    @Override
    public String getDescription() {
        return "Prints command line arguments to the console.";
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        for(int i = 0; i < parts.length; i++) {
            if(i == parts.length - 1) {
                context.appendlnStdOut(parts[i]);
            } else {
                context.appendStdOut(parts[i] + " ");
            }
        }
    }
}
