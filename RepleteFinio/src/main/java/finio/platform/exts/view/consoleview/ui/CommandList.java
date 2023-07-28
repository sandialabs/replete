package finio.platform.exts.view.consoleview.ui;

import finio.platform.exts.view.consoleview.commands.AliasCommand;
import finio.platform.exts.view.consoleview.commands.ChangeKeyCommand;
import finio.platform.exts.view.consoleview.commands.ChangeScopeCommand;
import finio.platform.exts.view.consoleview.commands.ClearCommand;
import finio.platform.exts.view.consoleview.commands.ClearMapCommand;
import finio.platform.exts.view.consoleview.commands.Command;
import finio.platform.exts.view.consoleview.commands.CountCommand;
import finio.platform.exts.view.consoleview.commands.DateCommand;
import finio.platform.exts.view.consoleview.commands.EchoCommand;
import finio.platform.exts.view.consoleview.commands.EnvCommand;
import finio.platform.exts.view.consoleview.commands.ExitCommand;
import finio.platform.exts.view.consoleview.commands.HistoryCommand;
import finio.platform.exts.view.consoleview.commands.ListKeysCommand;
import finio.platform.exts.view.consoleview.commands.ListenCommand;
import finio.platform.exts.view.consoleview.commands.ListenersCommand;
import finio.platform.exts.view.consoleview.commands.MoveCommand;
import finio.platform.exts.view.consoleview.commands.PrintCommand;
import finio.platform.exts.view.consoleview.commands.PutCommand;
import finio.platform.exts.view.consoleview.commands.PutMCommand;
import finio.platform.exts.view.consoleview.commands.PutNCommand;
import finio.platform.exts.view.consoleview.commands.PwsCommand;
import finio.platform.exts.view.consoleview.commands.RemoveCommand;
import finio.platform.exts.view.consoleview.commands.SetCommand;
import finio.platform.exts.view.consoleview.commands.ShowCommandsCommand;
import finio.platform.exts.view.consoleview.commands.TestCommand;
import finio.platform.exts.view.consoleview.commands.UnaliasCommand;
import finio.platform.exts.view.consoleview.commands.UnlistenCommand;
import finio.platform.exts.view.consoleview.commands.UnsetCommand;

public class CommandList {
    public static Command[] COMMANDS = new Command[] {
         new AliasCommand(),
         new ChangeKeyCommand(),
         new ChangeScopeCommand(),
         new ClearCommand(),
         new ClearMapCommand(),
         new CountCommand(),
         new DateCommand(),
         new EchoCommand(),
         new EnvCommand(),
         new ExitCommand(),
         new HistoryCommand(),
         new ListenCommand(),
         new ListenersCommand(),
         new ListKeysCommand(),
         new MoveCommand(),
         new PrintCommand(),
         new PutCommand(),
         new PutMCommand(),
         new PutNCommand(),
         new PwsCommand(),
         new RemoveCommand(),
         new SetCommand(),
         new ShowCommandsCommand(),
         new TestCommand(),
         new UnaliasCommand(),
         new UnlistenCommand(),
         new UnsetCommand()
    };
}
