package finio.ui.actions.change;

import java.util.List;

import finio.core.managed.ManagedNonTerminal;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.plugins.extpoints.NonTerminalManager;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class AddKeyValuePairManagedActions extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();


        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(NonTerminalManager.class);
        if(exts.size() != 0) {

            map.createAction("add-mgd-sib", new AActionValidator(ac))
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("change/add")
                        .setText("Managed Map As Sibling")
                        .setIcon(FinioImageModel.NT_MANAGED_MAP))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("change/add")
                        .setText("Managed Map As Sibling")
                        .setIcon(FinioImageModel.NT_MANAGED_MAP));

            for(ExtensionPoint ext : exts) {
                final NonTerminalManager manager = (NonTerminalManager) ext;
                String id = PluginManager.getExtensionId(manager);

                // Add Managed ___ As Sibling
                UIActionListener listener = new FWorkerActionListener(ac) {
                    @Override
                    public FWorker create() {
                        ManagedNonTerminal G = manager.createManagedNonTerminal();
                        return new AddKeyValuePairWorker(ac, ac.getSelectedWorld(),
                            "Add Managed Map As Sibling", G, true);
                    }
                };

                AActionValidator validator = new AActionValidator(ac)
                    .setWorldAllowed(false)
                    .setSelectionReverseDepth(2)
                ;

                map.createAction("add-mgd-sib-" + id, listener, validator)
                    .addDescriptor(
                        new MenuBarActionDescriptor()
                            .setPath("change/add/add-mgd-sib")
                            .setText(manager.getName())
                            .setIcon(manager.getIcon()))
                    .addDescriptor(
                        new PopupMenuActionDescriptor()
                            .setPath("change/add/add-mgd-sib")
                            .setText(manager.getName())
                            .setIcon(manager.getIcon()));
            }

            map.createAction("add-mgd-ch", new AActionValidator(ac))
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("change/add")
                        .setText("Managed Map As Child")
                        .setIcon(FinioImageModel.NT_MANAGED_MAP))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("change/add")
                        .setText("Managed Map As Child")
                        .setIcon(FinioImageModel.NT_MANAGED_MAP));

            for(ExtensionPoint ext : exts) {
                final NonTerminalManager manager = (NonTerminalManager) ext;
                String id = PluginManager.getExtensionId(manager);

                // Add Managed ___ As Sibling
                UIActionListener listener = new FWorkerActionListener(ac) {
                    @Override
                    public FWorker create() {
                        ManagedNonTerminal G = manager.createManagedNonTerminal();
                        return new AddKeyValuePairWorker(ac, ac.getSelectedWorld(),
                            "Add Managed Map As Child", G, false);
                    }
                };

                AActionValidator validator = new AActionValidator(ac)
                    .setTerminalAllowed(false)
                    .setSelectionReverseDepth(2)
                ;

                map.createAction("add-mgd-ch-" + id, listener, validator)
                    .addDescriptor(
                        new MenuBarActionDescriptor()
                            .setPath("change/add/add-mgd-ch")
                            .setText(manager.getName())
                            .setIcon(manager.getIcon()))
                    .addDescriptor(
                        new PopupMenuActionDescriptor()
                            .setPath("change/add/add-mgd-ch")
                            .setText(manager.getName())
                            .setIcon(manager.getIcon()));
            }
        }

    }

}
