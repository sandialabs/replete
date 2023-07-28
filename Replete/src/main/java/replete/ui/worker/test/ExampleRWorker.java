package replete.ui.worker.test;

import java.io.File;
import java.util.concurrent.ExecutionException;

import replete.ttc.TransparentTaskStopException;
import replete.ui.fc.RFileChooser;
import replete.ui.windows.Dialogs;
import replete.ui.worker.RWorker;

public class ExampleRWorker extends RWorker<File, String> {


    ////////////
    // FIELDS //
    ////////////

    private RWorkerTestFrame parent;
    private boolean causeError;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExampleRWorker(RWorkerTestFrame parent, boolean causeError) {
        super(true, true);
        this.parent = parent;
        this.causeError = causeError;
    }

    @Override
    protected File gather() {
        RFileChooser fc = RFileChooser.getChooser("Select File");
        return fc.showOpen(parent) ? fc.getSelectedFile() : null;
    }

    @Override
    protected boolean proceed(File gathered) {
        return gathered != null;
    }

//    @Override
//    protected void declined() {}

    @Override
    protected String background(File gathered) throws Exception {
        return LongTask.task(ttContext, gathered.toString(), 5000, causeError);
    }

//    @Override
//    protected void processProgress(List<ProgressMessage> chunks) {
//        System.out.println("process " + chunks + " [" + SwingUtilities.isEventDispatchThread() + "]");
//    }

    @Override
    protected void complete() {
        try {
            String result = getResult();
            parent.appendln(result);

        } catch(TransparentTaskStopException e) {
            // Do nothing

        } catch(ExecutionException e) {
            Dialogs.showDetails(parent,
                "An error has occurred.", "Import File Test", e.getCause());

        } catch(Exception e) {
            Dialogs.showDetails(parent,
                "An error has occurred.", "Import File Test", e);
        }
    }
}
