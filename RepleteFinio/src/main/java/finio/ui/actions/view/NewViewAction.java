package finio.ui.actions.view;

import java.util.List;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.plugins.extpoints.View;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class NewViewAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(View.class);
        if(exts.size() != 0) {

            AActionValidator worldValidator = new AActionValidator(ac)
                .setViewRequired(false)
            ;

            map.createAction("view-new", worldValidator)
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("view")
                        .setText("New")
                        .setIcon(CommonConcepts.NEW))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("view")
                        .setText("New")
                        .setIcon(CommonConcepts.NEW));

            for(ExtensionPoint ext : exts) {
                final View view = (View) ext;
                String id = PluginManager.getExtensionId(view);

                // View As ___
                UIActionListener listener = new FWorkerActionListener(ac) {
                    @Override
                    public FWorker create() {
                        return new OpenNewViewWorker(
                            ac, ac.getSelectedWorld(),
                            view.getName(), view);
                    }
                };

                AActionValidator validator = new AActionValidator(ac)
                    .setViewRequired(false)
                ;

                map.createAction("view-new-" + id, listener, validator)
                    .addDescriptor(
                        new MenuBarActionDescriptor()
                            .setPath("view/view-new")
                            .setText(view.getName())
                            .setIcon(view.getIcon()))
                    .addDescriptor(
                        new PopupMenuActionDescriptor()
                            .setPath("view/view-new")
                            .setText(view.getName())
                            .setIcon(view.getIcon()));
            }
        }
    }

}
