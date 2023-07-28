package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class PutCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "put";
    }
    @Override
    public String getDescription() {
        return "Adds a key-value pair to a non-terminal or changes the value that corresponds to a key.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(2, 2);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {

        // Get and check path
        KeyPath P = resolveKeyPath(context, parts.length == 0 ? null : parts[0]);
        checkIsNotRoot(P, "Cannot change the root scope's value.  It is always the same, mutable map.");
        // Doesn't have to exist

        // Separate last key and get parent map
        Object Klast = P.removeLast();
        // P may not exist either, we can choose to put an empty map at the path
        if(!context.getW().hasPath(P)) {
            throw new CommandException("put failed due to parent path " + P + " not existing");
        }
        Object Vparent = context.getW().getByPath(P);
// TODO: more clean up
        if(Vparent instanceof NonTerminal) {  // V should always be a map if we put one there.
            NonTerminal Mparent = (NonTerminal) Vparent;
            Object Vnew = getNewValue(parts);
            Mparent.putByKey(Klast, Vnew);
        } else {
            throw new CommandException("put failed due to destination path not being a map ");
        }
    }
    protected Object getNewValue(String[] parts) {
        return parts[1];
    }
}
