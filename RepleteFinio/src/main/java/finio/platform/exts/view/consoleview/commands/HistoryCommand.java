package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class HistoryCommand extends Command {


    ////////////
    // FIELDS //
    ////////////

    private Option optClear;


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "hist";
    }
    @Override
    public String getDescription() {
        return "Prints the previous command line commands.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        CommandLineParser parser = new CommandLineParser(0);
        optClear = parser.addBooleanOption('c');
        optClear.setHelpParamName("clear history");
        optClear.setHelpDescription("description of option...");
        return parser;
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        if(options.containsKey(optClear)) {
            Object[] vals = options.get(optClear);
            if(vals[0].equals(true)) {
                context.getHistory().clear();
                return;
            }
        }
        
        int size = context.getHistory().size();
        int w = (size + "").length();
        for(int h = 0; h < size; h++) {
            context.appendlnStdOut(String.format("%" + w + "d  %s", h + 1, context.getHistory().get(h)));
        }
    }
}
