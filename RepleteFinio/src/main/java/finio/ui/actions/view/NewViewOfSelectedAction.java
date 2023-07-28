package finio.ui.actions.view;

import java.util.List;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.plugins.extpoints.View;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.SelectionContext;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class NewViewOfSelectedAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(View.class);
        if(exts.size() != 0) {

            map.createAction("view-sel", new AActionValidator(ac))
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("view")
                        .setText("Selected As")
                        .setIcon(FinioImageModel.SELECTED))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("view")
                        .setText("Selected As")
                        .setIcon(FinioImageModel.SELECTED));

            for(ExtensionPoint ext : exts) {
                final View view = (View) ext;
                String id = PluginManager.getExtensionId(view);

                // View As ___
                UIActionListener listener = new FWorkerActionListener(ac) {
                    @Override
                    public FWorker create() {
                        return new OpenViewFromSelectedWorker(
                            ac, ac.getSelectedWorld(),
                            view.getName(), view);
                    }
                };

                AActionValidator validator = new AActionValidator(ac) {
                    @Override
                    protected boolean accept(AppContext ac, SelectionContext C) {
                        if(super.accept(ac, C)) {
                            Object V = C.getV();
                            return view.canView(V);
                        }
                        return false;
                    }
                };

                map.createAction("view-sel-" + id, listener, validator)
                    .addDescriptor(
                        new MenuBarActionDescriptor()
                            .setPath("view/view-sel")
                            .setText(view.getName())
                            .setIcon(view.getIcon()))
                    .addDescriptor(
                        new PopupMenuActionDescriptor()
                            .setPath("view/view-sel")
                            .setText(view.getName())
                            .setIcon(view.getIcon()));
            }
        }
    }

}
