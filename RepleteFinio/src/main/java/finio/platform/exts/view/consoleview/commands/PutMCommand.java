package finio.platform.exts.view.consoleview.commands;

import finio.core.impl.FMap;
import replete.cli.CommandLineParser;

public class PutMCommand extends PutCommand {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "putm";
    }
    @Override
    public String getDescription() {    // TODO: change this
        return "Adds a key-value pair to a non-terminal or changes the value that corresponds to a key.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(1);
    }
    @Override
    protected Object getNewValue(String[] parts) {
        return FMap.A();
    }
}
