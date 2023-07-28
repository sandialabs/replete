package finio.ui.actions.world;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import finio.core.NonTerminal;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;
import replete.ui.windows.Dialogs;
import replete.xstream.XStreamWrapper;

public class OpenWorldAction extends DefaultFinioUiAction {

    @Override
    public void register(final AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                RFileChooser fc = RFileChooser.getChooser("Open World");
                RFilterBuilder builder = new RFilterBuilder(fc, false);
                builder.append("World Files (*.world)", "world");
                if(fc.showOpen(ac.getWindow())) {
                    File loadFile = fc.getSelectedFile();
                    try {
                        NonTerminal W =      // Unfortunately this still happens on UI thread.
                            (NonTerminal) XStreamWrapper.loadTarget(loadFile);
                        WorldContext wc = new WorldContext(ac)
                            .setW(W)
                            .setSource(loadFile)
                            .setDirty(false);
                        ac.addWorld(wc);
                        ac.setSelectedWorldIndex(ac.getWorldCount() - 1);
                    } catch(Exception ex) {
                        Dialogs.showDetails(ac.getWindow(),
                            "An error occurred loading this world.", "Open Error", ex);
                    }
                }
            }
        };
//        listener = new AWorkerActionListener(ac) {
//            @Override
//            public AWorker create() {
//                return new OpenWorldWorker(ac, ac.getCurrentWorld(), "Open World");
//            }
//        };

        map.createAction("open-world", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("world")
                    .setText("&Open World...")
                    .setIcon(CommonConcepts.OPEN)
                    .setSepGroup("world-new-open")
                    .setAccKey(KeyEvent.VK_O)
                    .setAccCtrl(true))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("world")
                    .setToolTipText("Open World...")
                    .setIcon(CommonConcepts.OPEN));

    }

}
