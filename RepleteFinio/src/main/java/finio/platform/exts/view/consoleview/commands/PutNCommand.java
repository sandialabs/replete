package finio.platform.exts.view.consoleview.commands;

import replete.cli.CommandLineParser;

public class PutNCommand extends PutCommand {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "putn";
    }
    @Override
    public String getDescription() {   // TODO: change this
        return "Adds a key-value pair to a non-terminal or changes the value that corresponds to a key.";
    }
    @Override
    public CommandLineParser getCommandLineParser() {
        return new CommandLineParser(1);
    }
    @Override
    protected Object getNewValue(String[] parts) {
        return null;
    }
}
