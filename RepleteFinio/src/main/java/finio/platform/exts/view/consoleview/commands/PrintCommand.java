package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.syntax.FMapSyntaxLibrary;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import finio.renderers.map.FMapRenderer;
import finio.renderers.map.StandardAMapRenderer;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class PrintCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "pr";
    }
    @Override
    public String getDescription() {
        return "Prints a map in JSON format to the console.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        CommandLineParser parser = new CommandLineParser();
        parser.addStringOption('x', "syntax");
        return parser;
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        if(parts.length == 0) {
            handlePart(context, null, parts.length);
            return;
        }
        for(String part : parts) {
            try {
                handlePart(context, part, parts.length);
            } catch(CommandException e) {
                context.appendlnStdErr(this, e.getMessage());
            }
        }
    }
    private void handlePart(ConsoleContext context, String part, int partCount) throws CommandException {

        // Get and check path
        KeyPath P = resolveKeyPath(context, part);
        checkKeyPathExists(context, P, part);

        // Get value
        Object V = context.getW().getByPath(P);

        // Choose renderer
        String syntax = null;  // TODO: read syntax from options?
        FMapRenderer R = syntax != null ?
            new StandardAMapRenderer(FMapSyntaxLibrary.getSyntax(syntax)) :
                new StandardAMapRenderer();

        // Output to console
        if(partCount > 1) {
            context.appendlnStdOut(part + ":");
        }
        context.appendlnStdOut(R.renderValue(V));
    }
}
