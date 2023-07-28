package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;
import replete.text.StringUtil;

public class ShowCommandsCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "cmd";
    }
    @Override
    public String getDescription() {
        return "Prints all known commands to the console.";
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        int max = StringUtil.maxLength(context.getCommands().keySet());
        for(String name : context.getCommands().keySet()) {
            Command cmd = context.getCommands().get(name);
            if(cmd.getDescription() == null) {
                context.appendlnStdOut(name);
            } else {
                String fmt = String.format("%-" + max + "s %s", name, ": " + cmd.getDescription());
                context.appendlnStdOut(fmt);
            }
        }
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(0);
    }
}
