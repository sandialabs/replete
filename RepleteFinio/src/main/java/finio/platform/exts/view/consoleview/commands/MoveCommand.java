package finio.platform.exts.view.consoleview.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import finio.core.KeyPath;
import finio.core.errors.FMapCompositeException;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class MoveCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "mv";
    }
    @Override
    public String getDescription() {
        return "Removes a non-terminal or terminal from its parent non-terminal and places it at another location.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(2, Integer.MAX_VALUE);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {

        try {

            // Convert the string for the destination key path into
            // a key path object
            String partDst = parts[parts.length - 1];
            KeyPath Pdst = resolveKeyPath(context, partDst);

            List<KeyPath> Psources = new ArrayList<KeyPath>();
            for(int p = 0; p < parts.length - 1; p++) {
                try {

                    // Convert the string for the source key path
                    // into a key path object and add to list.
                    String partSource = parts[p];
                    KeyPath Psource = resolveKeyPath(context, partSource);
                    Psources.add(Psource);
                } catch(Exception e) {
                    context.appendlnStdErr(this, e.getMessage());
                }
            }

            // Execute the move command on the data model.
            KeyPath[] PsourcesArray = Psources.toArray(new KeyPath[0]);
            context.getW().move(PsourcesArray, Pdst, -1, false);

        } catch(FMapCompositeException compEx) {
            for(Exception e : compEx.getExceptions()) {
                context.appendlnStdErr(this, e.getMessage());
            }
        }
    }
}
