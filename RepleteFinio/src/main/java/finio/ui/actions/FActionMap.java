package finio.ui.actions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeListener;

import finio.core.NonTerminal;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.plugins.extpoints.FinioUIAction;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.event.ChangeNotifier;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.UIActionMap;
import replete.ui.windows.Dialogs;
import replete.ui.windows.notifications.msg.NotificationInfo;
import replete.xstream.XStreamWrapper;

public class FActionMap extends UIActionMap {

    private ChangeNotifier anyActionNotifier = new ChangeNotifier(this);
    public void addAnyActionListener(ChangeListener listener) {
        anyActionNotifier.addListener(listener);
    }
    public void fireAnyActionNotifier() {
        anyActionNotifier.fireStateChanged();
    }

    private AppContext ac;
    public FActionMap(AppContext ac) {
        this.ac = ac;
    }


    //////////////////////////////////////////////////////////////
    // FILE OPERATIONS NOT REMOVED YET DUE TO DEPENDENCY ISSUES //
    //////////////////////////////////////////////////////////////

    public void openWorld(File file) {
        try {
            NonTerminal W =      // Unfortunately this still happens on UI thread.
                (NonTerminal) XStreamWrapper.loadTarget(file);
            WorldContext wc = new WorldContext(ac)
                .setW(W)
                .setSource(file)
                .setDirty(false);
            ac.addWorld(wc);
            ac.setSelectedWorldIndex(ac.getWorldCount() - 1);
        } catch(Exception ex) {
            Dialogs.showDetails(ac.getWindow(),
                "An error occurred loading this world.", "Open Error", ex);
        }
    }

    public boolean checkSave(String message, String title) {
        if(ac.getSelectedWorld() != null && ac.getSelectedWorld().isDirty()) {
            int answer = Dialogs.showMulti(
                ac.getWindow(),
                message, title,
                new String[] {"Yes", "No", "Cancel"},
                JOptionPane.QUESTION_MESSAGE);
            if(answer == -1 || answer == 2) {
                return false;
            }
            if(answer == 0) {
                if(!save(ac.getSelectedWorld().getSource())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean save(File file) {
        if(file == null) {
            final RFileChooser fc = RFileChooser.getChooser("Save World As");
            RFilterBuilder builder = new RFilterBuilder(fc, false);
            builder.append("World Files (*.world)", "world");
            if(!fc.showSave(ac.getWindow())) {
                return false;
            }
            file = fc.getSelectedFile();
        }
        final NonTerminal W = ac.getSelectedWorld().getW();
        final File saveFile = file;
        ac.getSelectedWorld().setSource(saveFile);

        final Runnable action = new Runnable() {
            @Override
            public void run() {
                try {
                    XStreamWrapper.writeToFile(W, saveFile);
                    ac.getSelectedWorld().setDirty(false);
                    NotificationInfo info = new NotificationInfo()
                        .setTitle("Saved to " + saveFile.getAbsolutePath() + ".")
                        .setIcon(CommonConcepts.SAVE);
                    ac.getWindow().setShowNotificationArea(true);
                    ac.getWindow().getNotificationModel().getInfos().add(info);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        };

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                action.run();
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    fireAnyActionNotifier();
                } catch(Exception e) {
                    Dialogs.showDetails(
                        ac.getWindow(),
                        "An error has occurred with this operation.", "Error", e);
                }
            }
        };
        worker.execute();

        return true;
    }

    public void init() {
        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(FinioUIAction.class);
        if(exts.size() != 0) {
            for(ExtensionPoint ext : exts) {
                DefaultFinioUiAction action = (DefaultFinioUiAction) ext;
                action.init(this);
            }
        }
    }

}
