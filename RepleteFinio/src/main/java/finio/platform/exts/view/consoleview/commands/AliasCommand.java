package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class AliasCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "alias";
    }
    @Override
    public String getDescription() {
        return "Displays aliases or creates an alias for a command and its arguments.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(0, 2);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        if(parts.length == 0) {
            for(String alias : context.getAliases().keySet()) {
                printAlias(context, alias, context.getAliases().get(alias));
            }
        } else if(parts.length == 1) {
            if(context.getAliases().containsKey(parts[0])) {
                printAlias(context, parts[0], context.getAliases().get(parts[0]));
            } else {
                context.appendlnStdErr(this, "Alias not found: " + parts[0]);
            }
        } else {
            context.getAliases().put(parts[0], parts[1]);
        }
    }
    private void printAlias(ConsoleContext context, String alias, String cmd) {
        context.appendlnStdOut(alias + " = '" + cmd + "'");
    }
}
