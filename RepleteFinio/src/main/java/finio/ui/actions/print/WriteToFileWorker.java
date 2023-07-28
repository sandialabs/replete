package finio.ui.actions.print;

import java.io.File;

import finio.renderers.map.StandardAMapRenderer;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.io.FileUtil;
import replete.ui.fc.RFileChooser;
import replete.ui.windows.notifications.msg.NotificationInfo;

public class WriteToFileWorker extends FWorker<File, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WriteToFileWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected File gather() {
        RFileChooser chooser = RFileChooser.getChooser("Write To File");
        return chooser.showSave(ac.getWindow()) ? chooser.getSelectedFile() : null;
    }

    @Override
    protected boolean proceed(File gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(File gathered) throws Exception {
        StandardAMapRenderer renderer = new StandardAMapRenderer();
        renderer.setRenderSysMeta(true);
        StringBuilder buffer = new StringBuilder();
        for(SelectionContext C : getValidSelected()) {
            buffer.append(renderer.render(null, C.getV()));
            buffer.append('\n');
        }
        buffer.deleteCharAt(buffer.length() - 1);
        FileUtil.writeTextContent(gathered, buffer.toString());
        return null;
    }

    @Override
    public String getActionVerb() {
        return "writing map to a file";
    }

    @Override
    protected void completeInner(Void result) {
        NotificationInfo info = new NotificationInfo()
            .setTitle("<html>Map written to <u>" + gathered + "</u></html>");

        ac.getWindow().
            getNotificationModel().
                getInfos().add(info);
    }
}
