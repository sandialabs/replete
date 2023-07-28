package finio.platform.exts.view.consoleview.ui;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.events.MapListener;
import finio.platform.exts.view.consoleview.commands.Command;

public interface ConsoleContext {

    // Data model
    public NonTerminal getW();

    // Printing things to console in various forms
    public void appendlnMgmt(String string);
    public void appendln(String string, Color color);
    public void appendPrompt(String string);
    public void appendlnStdOut(Object obj);
    public void appendlnStdOut(String string);
    public void appendStdOut(String string);
    public void appendlnStdErr(String string);
    public void appendlnStdErr(Command cmd, String message);
    public void showPrompt();

    // Retrieving console state
    public Map<String, Command> getCommands();
    public Map<String, String> getEnvironment();
    public Map<String, String> getAliases();
    public List<String> getHistory();

    // Misc
    public void clearText();
    public void requestExit();
    public MapListener createMapListener(ListenType type, KeyPath P);
    public KeyPath getWorkingScope();
    public boolean setWorkingScope(KeyPath P);
}
