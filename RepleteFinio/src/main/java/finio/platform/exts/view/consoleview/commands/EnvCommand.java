package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class EnvCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "env";
    }
    @Override
    public String getDescription() {
        return "Prints the environment variables to the console.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(0);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        for(String var : context.getEnvironment().keySet()) {
            context.appendlnStdOut(var + "=" + context.getEnvironment().get(var));
        }
    }
}
