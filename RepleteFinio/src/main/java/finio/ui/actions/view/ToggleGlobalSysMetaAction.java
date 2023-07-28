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

public class ToggleGlobalSysMetaAction extends DefaultFinioUiAction {

    private AppContext ac;

    @Override
    public void register(final AppContext ac) {
        this.ac = ac;

        final UIActionMap map = ac.getActionMap();

        UIActionListener listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                ac.getConfig().setShowNodeMeta(
                    !ac.getConfig().isShowNodeMeta());
            }
        };

        AActionValidator validator = new AActionValidator(ac);

        map.createAction("initial-sysmeta", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("view")
                    .setText("Default Show SysMeta")
                    .setIcon(FinioImageModel.METAMAP_TOGGLE))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("other")
                    .setToggle(true)
                    .setToolTipText("Default Show SysMeta")
                    .setIcon(FinioImageModel.METAMAP_TOGGLE));

        ac.getConfig().addPropertyChangeListener(new AppStateChangeListener() {
            @Override
            public void stateChanged(AppStateChangeEvent e) {
                if(e.getName().equals("showNodeMeta")) {
                    UIAction nodeInfo = map.getAction("initial-sysmeta");
                    ToolBarActionDescriptor desc =
                        (ToolBarActionDescriptor) nodeInfo.getDescriptor(ToolBarActionDescriptor.class);
                    AbstractButton btn = desc.getComponent();
                    btn.setSelected(ac.getConfig().isShowNodeMeta());
                }
            }
        });
    }

    @Override
    public void init(FActionMap map) {
        UIAction nodeInfo = map.getAction("initial-sysmeta");
        ToolBarActionDescriptor desc =
            (ToolBarActionDescriptor) nodeInfo.getDescriptor(ToolBarActionDescriptor.class);
        AbstractButton btn = desc.getComponent();
        btn.setSelected(ac.getConfig().isShowNodeMeta());
    }

}
