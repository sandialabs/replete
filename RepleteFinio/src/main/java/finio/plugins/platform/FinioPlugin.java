package finio.plugins.platform;

import javax.swing.ImageIcon;

import finio.SoftwareVersion;
import finio.extractors.jo.DefaultKeyValueRegistrar;
import finio.extractors.jo.DefaultPopulateParamsCreator;
import finio.extractors.jo.FMapHostNonTerminalCreator;
import finio.extractors.jo.FieldReflectionKeyValueIteratorCreator;
import finio.extractors.jo.FieldReflectionKeyValueRegistrar;
import finio.extractors.jo.HostNonTerminalCreator;
import finio.extractors.jo.IdentityObjectResolver;
import finio.extractors.jo.KeyValueIteratorCreator;
import finio.extractors.jo.KeyValueRegistrar;
import finio.extractors.jo.NoOpPostFieldsModifier;
import finio.extractors.jo.NonTerminalExpansionDecider;
import finio.extractors.jo.NonTerminalLikeDefaultPopulateParamsCreator;
import finio.extractors.jo.NonTerminalLikeKeyValueIteratorCreator;
import finio.extractors.jo.NonTerminalLikeNonTerminalExpansionDecider;
import finio.extractors.jo.ObjectResolver;
import finio.extractors.jo.PostFieldsModifier;
import finio.extractors.jo.ReflectionDefaultPopulateParamsCreator;
import finio.extractors.jo.ReflectionNonTerminalExpansionDecider;
import finio.platform.exts.editor.FileObjectEditor;
import finio.platform.exts.editor.ManagedValueManagerEditor;
import finio.platform.exts.editor.RectangleJavaObjectEditor;
import finio.platform.exts.editor.StringObjectEditor;
import finio.platform.exts.editor.UnknownNativeObjectEditor;
import finio.platform.exts.editor.UrlObjectEditor;
import finio.platform.exts.manager.orientdb.OrientDbFileMapManager;
import finio.platform.exts.manager.trivial.TrivialNonTerminalManager;
import finio.platform.exts.manager.xstream.XStreamFileMapManager;
import finio.platform.exts.sc.BaseScPostFieldsModifier;
import finio.platform.exts.sc.FieldResultResolver;
import finio.platform.exts.stringmatch.BinaryStringMatcher;
import finio.platform.exts.stringmatch.IpV4StringMatcher;
import finio.platform.exts.stringmatch.Md5StringMatcher;
import finio.platform.exts.stringmatch.RomanNumeralStringMatcher;
import finio.platform.exts.stringmatch.UrlStringMatcher;
import finio.platform.exts.view.consoleview.ConsoleView;
import finio.platform.exts.view.tabs.TabbedView;
import finio.platform.exts.view.textonly.TextOnlyView;
import finio.platform.exts.view.treeview.TreeView;
import finio.platform.exts.view.treeview.ui.actions.ActionSelectedAction;
import finio.platform.exts.view.treeview.ui.actions.CollapseAction;
import finio.platform.exts.view.treeview.ui.actions.CollapseAllAction;
import finio.platform.exts.view.treeview.ui.actions.ExpandAction;
import finio.platform.exts.view.treeview.ui.actions.ExpandAllAction;
import finio.platform.exts.view.treeview.ui.actions.TogglePauseAction;
import finio.platform.exts.view.treeview.ui.actions.ToggleSimplifiedAction;
import finio.platform.exts.view.treeview.ui.actions.ToggleSysMetaAction;
import finio.platform.exts.view.treeview.ui.actions.TransferSelectedAction;
import finio.platform.exts.view.treeview.ui.actions.TreeAction;
import finio.platform.exts.views.split.SplitViews;
import finio.platform.exts.views.split.ui.actions.ConsoleOnlyAction;
import finio.platform.exts.views.split.ui.actions.TreeConsoleHorizAction;
import finio.platform.exts.views.split.ui.actions.TreeConsoleVertAction;
import finio.platform.exts.views.split.ui.actions.TreeOnlyAction;
import finio.platform.exts.views.tabs.TabbedViews;
import finio.platform.exts.worlds.tabs.ui.actions.SetWorldTabPlacementBottomAction;
import finio.platform.exts.worlds.tabs.ui.actions.SetWorldTabPlacementLeftAction;
import finio.platform.exts.worlds.tabs.ui.actions.SetWorldTabPlacementRightAction;
import finio.platform.exts.worlds.tabs.ui.actions.SetWorldTabPlacementTopAction;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.NonTerminalManager;
import finio.plugins.extpoints.StringPatternMatcher;
import finio.plugins.extpoints.View;
import finio.plugins.extpoints.Views;
import finio.plugins.extpoints.Worlds;
import finio.ui.actions.change.AddAction;
import finio.ui.actions.change.AddKeyValuePairLChAction;
import finio.ui.actions.change.AddKeyValuePairLSibAction;
import finio.ui.actions.change.AddKeyValuePairMChAction;
import finio.ui.actions.change.AddKeyValuePairMSibAction;
import finio.ui.actions.change.AddKeyValuePairManagedActions;
import finio.ui.actions.change.AddKeyValuePairTChAction;
import finio.ui.actions.change.AddKeyValuePairTSibAction;
import finio.ui.actions.change.ChangeAction;
import finio.ui.actions.change.ClearAction;
import finio.ui.actions.change.DescribeValueAction;
import finio.ui.actions.change.EditKeyValueNoShiftAction;
import finio.ui.actions.change.EditKeyValueShiftAction;
import finio.ui.actions.change.PromoteValueAction;
import finio.ui.actions.change.RemoveAction;
import finio.ui.actions.edit.CopyAction;
import finio.ui.actions.edit.CutAction;
import finio.ui.actions.edit.EditAction;
import finio.ui.actions.edit.RedoAction;
import finio.ui.actions.edit.UndoAction;
import finio.ui.actions.help.AboutAction;
import finio.ui.actions.help.HelpAction;
import finio.ui.actions.help.JavaEvalAction;
import finio.ui.actions.help.PluginsAction;
import finio.ui.actions.help.SystemDetailsAction;
import finio.ui.actions.help.SystemDetailsActionAlt;
import finio.ui.actions.imprt.ImportAction;
import finio.ui.actions.imprt.ImportDirAction;
import finio.ui.actions.imprt.ImportExampleDataAction;
import finio.ui.actions.imprt.ImportExampleJavaMapAsMapAction;
import finio.ui.actions.imprt.ImportExampleJavaMapAsObjectAction;
import finio.ui.actions.imprt.ImportExcelAction;
import finio.ui.actions.imprt.ImportFileAction;
import finio.ui.actions.imprt.ImportRawPdfAction;
import finio.ui.actions.imprt.ImportScrutinizationAction;
import finio.ui.actions.imprt.ImportTextCsvAction;
import finio.ui.actions.imprt.ImportTextJsonAction;
import finio.ui.actions.imprt.ImportTextXmlAction;
import finio.ui.actions.imprt.ImportUrlAction;
import finio.ui.actions.imprt.ImportWordAction;
import finio.ui.actions.mark.MarkAction;
import finio.ui.actions.mark.SetImageAction;
import finio.ui.actions.mark.SetWorkingScopeAction;
import finio.ui.actions.mark.ToggleAnchorAction;
import finio.ui.actions.mgmt.EditManagedParametersAction;
import finio.ui.actions.mgmt.LoadManagedNonTerminalAction;
import finio.ui.actions.mgmt.ManagementAction;
import finio.ui.actions.mgmt.UnloadManagedNonTerminalAction;
import finio.ui.actions.misc.ImportUiHierarchyAction;
import finio.ui.actions.misc.MiscAction;
import finio.ui.actions.navigate.NavigateAction;
import finio.ui.actions.navigate.SelectChildrenAction;
import finio.ui.actions.navigate.SelectParentsAction;
import finio.ui.actions.navigate.SelectRootAction;
import finio.ui.actions.navigate.SelectSiblingsAction;
import finio.ui.actions.navigate.ZoomInAction;
import finio.ui.actions.navigate.ZoomOutAction;
import finio.ui.actions.print.PrintAction;
import finio.ui.actions.print.WriteToConsoleAction;
import finio.ui.actions.print.WriteToFileAction;
import finio.ui.actions.send.SendAction;
import finio.ui.actions.send.SendEmailAction;
import finio.ui.actions.tools.HttpRequestOptionsAction;
import finio.ui.actions.tools.ToolsAction;
import finio.ui.actions.transform.CombineMapsAction;
import finio.ui.actions.transform.ConvertBytesToStringAction;
import finio.ui.actions.transform.ConvertJsonToMapAction;
import finio.ui.actions.transform.ConvertMapToStringAction;
import finio.ui.actions.transform.CreateAlternatesMapAction;
import finio.ui.actions.transform.DescribeObjectAction;
import finio.ui.actions.transform.DiffMapsAction;
import finio.ui.actions.transform.ExpandObjectMapAwareAction;
import finio.ui.actions.transform.ExpandObjectReflectionAction;
import finio.ui.actions.transform.ExpandObjectUberAction;
import finio.ui.actions.transform.ExpandStringAction;
import finio.ui.actions.transform.ExtractFileBytesAction;
import finio.ui.actions.transform.FlattenAction;
import finio.ui.actions.transform.FlattenChildrenAction;
import finio.ui.actions.transform.SplitStringAction;
import finio.ui.actions.transform.TransformAction;
import finio.ui.actions.view.CloseAllViewsAction;
import finio.ui.actions.view.CloseViewAction;
import finio.ui.actions.view.DecreaseFontAction;
import finio.ui.actions.view.FocusConsoleAction;
import finio.ui.actions.view.FocusTreeAction;
import finio.ui.actions.view.IncreaseFontAction;
import finio.ui.actions.view.NewViewAction;
import finio.ui.actions.view.NewViewOfSelectedAction;
import finio.ui.actions.view.RefreshAction;
import finio.ui.actions.view.RenameViewAction;
import finio.ui.actions.view.SwitchDesktopAction;
import finio.ui.actions.view.SwitchTabsAction;
import finio.ui.actions.view.ToggleExpandSingleAction;
import finio.ui.actions.view.ToggleGlobalSysMetaAction;
import finio.ui.actions.view.ToggleNodeInfoAction;
import finio.ui.actions.view.ToggleNotifAreaAction;
import finio.ui.actions.view.TogglePanelConfigAction;
import finio.ui.actions.view.ToggleStatusBarAction;
import finio.ui.actions.view.ViewAction;
import finio.ui.actions.window.FullScreenAction;
import finio.ui.actions.window.NewWindowAction;
import finio.ui.actions.window.WindowAction;
import finio.ui.actions.world.CloseAllWorldsAction;
import finio.ui.actions.world.CloseWorldAction;
import finio.ui.actions.world.ExitAction;
import finio.ui.actions.world.NewWorldAction;
import finio.ui.actions.world.OpenWorldAction;
import finio.ui.actions.world.RenameWorldAction;
import finio.ui.actions.world.SaveWorldAction;
import finio.ui.actions.world.SaveWorldAsAction;
import finio.ui.actions.world.WorldAction;
import finio.ui.multidlg.ByteInputSourcePanelCreator;
import finio.ui.multidlg.FileInputSourcePanelCreator;
import finio.ui.multidlg.InputSourcePanelCreator;
import finio.ui.multidlg.TextInputSourcePanelCreator;
import finio.ui.multidlg.WebInputSourcePanelCreator;
import replete.plugins.ExtensionPoint;
import replete.plugins.Plugin;

public class FinioPlugin implements Plugin {

    public String getName() {
        return "Finio Platform Plug-in";
    }
    public String getVersion() {
        return SoftwareVersion.get().getFullVersionString();
    }
    public String getProvider() {
        return "Derek Trumbo";
    }
    public ImageIcon getIcon() {
        return null;
    }
    public String getDescription() {
        return "This plug-in provides the base Finio platform extension points and basic default extensions.";
    }

    @SuppressWarnings("unchecked")
    public Class<? extends ExtensionPoint>[] getExtensionPoints() {
        return new Class[] {
            View.class,          // I wonder if ExtensionPoint should have methods...
            Views.class,
            Worlds.class,
            NonTerminalManager.class,
            DefaultFinioUiAction.class,
            StringPatternMatcher.class,
            JavaObjectEditor.class,
            InputSourcePanelCreator.class,

            // Customizable NT-ization
            HostNonTerminalCreator.class,
            KeyValueIteratorCreator.class,
            ObjectResolver.class,
            KeyValueRegistrar.class,
            PostFieldsModifier.class,
            NonTerminalExpansionDecider.class,
            DefaultPopulateParamsCreator.class
        };
    }

    public ExtensionPoint[] getExtensions() {
        return new ExtensionPoint[] {

            new FinioSoftwareVersionLookup(),

            new FileInputSourcePanelCreator(),
            new WebInputSourcePanelCreator(),
            new TextInputSourcePanelCreator(),
            new ByteInputSourcePanelCreator(),

            //////// CUSTOMIZABLE NT-IZATION ////////

            new FMapHostNonTerminalCreator(),
            new NonTerminalLikeKeyValueIteratorCreator(),
            new FieldReflectionKeyValueIteratorCreator(),
            new IdentityObjectResolver(),
            new FieldResultResolver(),
            new DefaultKeyValueRegistrar(),
            new FieldReflectionKeyValueRegistrar(),
            new NoOpPostFieldsModifier(),
            new BaseScPostFieldsModifier(),
            new ReflectionNonTerminalExpansionDecider(),
            new NonTerminalLikeNonTerminalExpansionDecider(),
            new ReflectionDefaultPopulateParamsCreator(),
            new NonTerminalLikeDefaultPopulateParamsCreator(),

            //////// VIEWS ////////

            new TabbedViews(),
            new SplitViews(),
            new TreeView(),     // I wonder if Extension should have methods...
            new TabbedView(),
            new ConsoleView(),
            new TextOnlyView(),

            //////// MAP MANAGERS ////////

            new OrientDbFileMapManager(),
            new TrivialNonTerminalManager(),
            new XStreamFileMapManager(),

            //////// PATTERN MATCHERS ////////

            new Md5StringMatcher(),
            new IpV4StringMatcher(),
            new UrlStringMatcher(),
            new BinaryStringMatcher(),
            new RomanNumeralStringMatcher(),  // One day should provide what the number was as an integer

            //////// POJO EDITORS ////////

            new UnknownNativeObjectEditor(),
            new StringObjectEditor(),
            new RectangleJavaObjectEditor(),
            new FileObjectEditor(),
            new UrlObjectEditor(),
            new ManagedValueManagerEditor(),

            //////// ACTIONS ////////

            // World
            new WorldAction(),
            new NewWorldAction(),
            new OpenWorldAction(),
            new SaveWorldAction(),
            new SaveWorldAsAction(),
            new RenameWorldAction(),
            new CloseWorldAction(),
            new CloseAllWorldsAction(),
            new ExitAction(),

            // Edit
            new EditAction(),
            new UndoAction(),
            new RedoAction(),
            new CutAction(),
            new CopyAction(),

            // View
            new ViewAction(),
            new ToggleStatusBarAction(),
            new ToggleNotifAreaAction(),
            new SwitchTabsAction(),
            new SwitchDesktopAction(),
            new ToggleExpandSingleAction(),
            new SetWorldTabPlacementTopAction(),
            new SetWorldTabPlacementLeftAction(),
            new SetWorldTabPlacementRightAction(),
            new SetWorldTabPlacementBottomAction(),
            new NewViewAction(),
            new NewViewOfSelectedAction(),
            new RenameViewAction(),
            new CloseViewAction(),
            new CloseAllViewsAction(),
            new TogglePanelConfigAction(),
            new TreeOnlyAction(),
            new TreeConsoleVertAction(),
            new TreeConsoleHorizAction(),
            new ConsoleOnlyAction(),
            new FocusTreeAction(),
            new FocusConsoleAction(),
            new IncreaseFontAction(),
            new DecreaseFontAction(),
            new RefreshAction(),
            new ToggleNodeInfoAction(),
            new ToggleGlobalSysMetaAction(),

            // Change
            new ChangeAction(),
            new AddAction(),
            new AddKeyValuePairMSibAction(),
            new AddKeyValuePairMChAction(),
            new AddKeyValuePairTSibAction(),
            new AddKeyValuePairTChAction(),
            new AddKeyValuePairLSibAction(),
            new AddKeyValuePairLChAction(),
            new AddKeyValuePairManagedActions(),
            new EditKeyValueNoShiftAction(),
            new EditKeyValueShiftAction(),
            new RemoveAction(),
            new ClearAction(),
            new DescribeValueAction(),
            new PromoteValueAction(),

            // Import
            new ImportAction(),
            new ImportFileAction(),
            new ImportDirAction(),
            new ImportUrlAction(),
            new ImportTextCsvAction(),
            new ImportTextJsonAction(),
            new ImportTextXmlAction(),
            new ImportWordAction(),
            new ImportExcelAction(),
            new ImportExampleDataAction(),
            new ImportExampleJavaMapAsObjectAction(),
            new ImportExampleJavaMapAsMapAction(),
            new ImportRawPdfAction(),
            new ImportScrutinizationAction(),

            // Transform
            new TransformAction(),
            new ExpandObjectReflectionAction(),
            new ExpandObjectMapAwareAction(),
            new ExpandObjectUberAction(),
            new ExpandStringAction(),
            new DescribeObjectAction(),
            new CreateAlternatesMapAction(),
            new SplitStringAction(),
            new ConvertMapToStringAction(),
            new ConvertJsonToMapAction(),
            new CombineMapsAction(),
            new FlattenAction(),
            new FlattenChildrenAction(),
            new ExtractFileBytesAction(),
            new ConvertBytesToStringAction(),

            new DiffMapsAction(),

            // Tree - These actions, along with the view could one day be placed into a new plug-in.
            new TreeAction(),
            new CollapseAction(),
            new CollapseAllAction(),
            new ExpandAction(),
            new ExpandAllAction(),
            new TogglePauseAction(),
            new ToggleSimplifiedAction(),
            new ToggleSysMetaAction(),
            new ActionSelectedAction(),
            new TransferSelectedAction(),

            // Print
            new PrintAction(),
            new WriteToConsoleAction(),
            new WriteToFileAction(),

            // Send
            new SendAction(),
            new SendEmailAction(),

            // Navigate
            new NavigateAction(),
            new SelectRootAction(),
            new SelectChildrenAction(),
            new SelectParentsAction(),
            new SelectSiblingsAction(),
            new ZoomInAction(),
            new ZoomOutAction(),

            // Mark
            new MarkAction(),
            new SetWorkingScopeAction(),
            new ToggleAnchorAction(),
            new SetImageAction(),

            // Management
            new ManagementAction(),
            new EditManagedParametersAction(),
            new LoadManagedNonTerminalAction(),
            new UnloadManagedNonTerminalAction(),

            // Misc
            new MiscAction(),
            new ImportUiHierarchyAction(),

            // Tools
            new ToolsAction(),
            new HttpRequestOptionsAction(),

            // Window
            new WindowAction(),
            new NewWindowAction(),
            new FullScreenAction(),

            // Help
            new HelpAction(),
            new SystemDetailsAction(),
            new SystemDetailsActionAlt(),
            new PluginsAction(),
            new JavaEvalAction(),
            new AboutAction()

        };
    }

    @Override
    public void start() {}
}
