package finio.ui.actions.world;

import java.io.File;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.windows.notifications.msg.NotificationInfo;
import replete.xstream.XStreamWrapper;

public class SaveWorldWorker extends FWorker<Void, Void> {


    ///////////
    // FIELD //
    ///////////

    private File targetFile;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SaveWorldWorker(AppContext ac, WorldContext wc, String name, File targetFile) {
        super(ac, wc, name);
        this.targetFile = targetFile;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void gather() {
        if(targetFile == null) {
            RFileChooser fc = RFileChooser.getChooser("Save World As");
            RFilterBuilder builder = new RFilterBuilder(fc, false);
            builder.append("World Files (*.world)", "world");
            if(fc.showSave(ac.getWindow())) {
                targetFile = fc.getSelectedFile();
            }
        }
        return null;
    }

    @Override
    protected boolean proceed(Void gathered) {
        return targetFile != null;
    }

    @Override
    protected Void background(Void gathered) throws Exception {
//        ac.setFile(targetFile);
        NonTerminal W = wc.getW();
        XStreamWrapper.writeToFile(W, targetFile);
        return null;
    }

    @Override
    public String getActionVerb() {
        return "loading this world";
    }

    @Override
    protected void completeInner(Void result) {
        NotificationInfo info = new NotificationInfo()
            .setTitle("<html>Saved to <u>" + targetFile.getAbsolutePath() + "</u></html>")
            .setIcon(CommonConcepts.SAVE);

        ac.getWindow().
            getNotificationModel().
                getInfos().add(info);
    }
}
