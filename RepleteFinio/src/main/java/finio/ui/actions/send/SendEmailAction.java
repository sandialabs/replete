package finio.ui.actions.send;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.actions.validation.SpecificTypeValueActionValidator;
import finio.ui.app.AppContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class SendEmailAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new SendEmailWorker(ac, ac.getSelectedWorld(), "Send E-mail");
            }
        };

        AActionValidator validator = new SpecificTypeValueActionValidator(ac, String.class)
            .setWorldAllowed(false)
            .setNonTerminalAllowed(false)
        ;

        map.createAction("send-email", listener, validator)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("send")
                    .setText("&Send E-mail...")
                    .setIcon(CommonConcepts.E_MAIL))
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("send")
                    .setText("&Send E-mail...")
                    .setIcon(CommonConcepts.E_MAIL));

    }

}
