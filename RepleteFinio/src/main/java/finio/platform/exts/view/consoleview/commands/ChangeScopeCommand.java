package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class ChangeScopeCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "cs";
    }
    @Override
    public String getDescription() {
        return "Changes the working scope.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(1);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {

        // Get and check path
        String part = parts.length == 0 ? null : parts[0];
        KeyPath P = resolveKeyPath(context, part);
        checkKeyPathExists(context, P, part);

        // Edit data model
        context.setWorkingScope(P);
    }
}
