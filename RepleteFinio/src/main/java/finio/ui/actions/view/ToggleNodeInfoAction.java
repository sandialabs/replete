package finio.ui.actions.view;

import java.awt.event.ActionEvent;

import javax.swing.AbstractButton;

import finio.appstate.AppStateChangeEvent;
import finio.appstate.AppStateChangeListener;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FActionMap;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ToggleNodeInfoAction extends DefaultFinioUiAction {

    private AppContext ac;

    @Override
    public void register(final AppContext ac) {
        this.ac = ac;

        final UIActionMap map = ac.getActionMap();

        UIActionListener listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                ac.getConfig().setNodeInfoEnabled(
                    !ac.getConfig().isNodeInfoEnabled());
            }
        };

        map.createAction("node-info-enabled", listener, new AActionValidator(ac))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("Enable/Disable Node Information")
                    .setIcon(FinioImageModel.NODE_INFO))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("other")
                    .setToggle(true)
                    .setToolTipText("Enable/Disable Node Information")
                    .setIcon(FinioImageModel.NODE_INFO));

        ac.getConfig().addPropertyChangeListener(new AppStateChangeListener() {
            @Override
            public void stateChanged(AppStateChangeEvent e) {
                if(e.getName().equals("nodeInfoEnabled")) {
                    System.out.println("nie");
                    UIAction nodeInfo = map.getAction("node-info-enabled");
                    ToolBarActionDescriptor desc =
                        (ToolBarActionDescriptor) nodeInfo.getDescriptor(ToolBarActionDescriptor.class);
                    AbstractButton btn = desc.getComponent();
                    btn.setSelected(ac.getConfig().isNodeInfoEnabled());
                }
            }
        });
    }

    @Override
    public void init(FActionMap map) {
        UIAction nodeInfo = map.getAction("node-info-enabled");
        ToolBarActionDescriptor desc =
            (ToolBarActionDescriptor) nodeInfo.getDescriptor(ToolBarActionDescriptor.class);
        AbstractButton btn = desc.getComponent();
        btn.setSelected(ac.getConfig().isNodeInfoEnabled());
    }
}
