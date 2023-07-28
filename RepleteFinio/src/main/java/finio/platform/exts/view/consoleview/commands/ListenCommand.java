package finio.platform.exts.view.consoleview.commands;

import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.events.KeyAddedListener;
import finio.core.events.KeyChangedListener;
import finio.core.events.KeyRemovedListener;
import finio.core.events.MapChangedListener;
import finio.core.events.MapClearedListener;
import finio.core.events.ValueChangedListener;
import finio.platform.exts.view.consoleview.ui.ConsoleContext;
import finio.platform.exts.view.consoleview.ui.ListenType;
import replete.cli.CommandLineParser;
import replete.cli.options.Option;

public class ListenCommand extends Command {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "listen";
    }
    @Override
    public String getDescription() {
        return "Listen to events for a map.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(1, Integer.MAX_VALUE);
    }
    @Override
    protected void executeInner(ConsoleContext context, String[] parts, Map<Option, Object[]> options) throws CommandException {
        String typeId = parts[0];
        ListenType foundType = null;
        for(ListenType type : ListenType.values()) {
            if(typeId.equals(type.getId())) {
                foundType = type;
            }
        }
        if(foundType == null) {
            throw new CommandException("Listen type not recognized: " + typeId);
        }
        if(parts.length == 1) {
            listenTo(context, foundType, null);
            return;
        }
        for(int p = 1; p < parts.length; p++) {
            try {
                listenTo(context, foundType, parts[p]);
            } catch(CommandException e) {
                context.appendlnStdErr(this, e.getMessage());
            }
        }
    }
    private void listenTo(ConsoleContext context, ListenType type, String part) throws CommandException {

        // Get and check path
        KeyPath P = resolveKeyPath(context, part);
        checkKeyPathExists(context, P, part);

        // Get and check value
        Object V = context.getW().getByPath(P);
        checkIsNonTerminal(V, part);

        // Attach listener
        NonTerminal M = (NonTerminal) V;
        switch(type) {
            case MAP_CHANGED:
                M.addMapChangedListener(
                    (MapChangedListener) context.createMapListener(ListenType.MAP_CHANGED, P));
                break;
            case MAP_CLEARED:
                M.addMapClearedListener(
                    (MapClearedListener) context.createMapListener(ListenType.MAP_CLEARED, P));
                break;
            case KEY_ADDED:
                M.addKeyAddedListener(
                    (KeyAddedListener) context.createMapListener(ListenType.KEY_ADDED, P));
                break;
            case VALUE_CHANGED:
                M.addValueChangedListener(
                    (ValueChangedListener) context.createMapListener(ListenType.VALUE_CHANGED, P));
                break;
            case KEY_REMOVED:
                M.addKeyRemovedListener(
                    (KeyRemovedListener) context.createMapListener(ListenType.KEY_REMOVED, P));
                break;
            case KEY_CHANGED:
                M.addKeyChangedListener(
                    (KeyChangedListener) context.createMapListener(ListenType.KEY_CHANGED, P));
                break;
        }
    }
}
