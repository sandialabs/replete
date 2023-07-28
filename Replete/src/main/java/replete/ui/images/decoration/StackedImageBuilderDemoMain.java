package replete.ui.images.decoration;

import java.lang.Thread.UncaughtExceptionHandler;

import replete.ui.windows.Dialogs;
import replete.util.DemoMain;

public class StackedImageBuilderDemoMain extends DemoMain {


    //////////
    // MAIN //
    //////////

    public static void main(String[] args) {
        StackedImageBuilderTestFrame test = new StackedImageBuilderTestFrame();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                Dialogs.showDetails(test, "There was an unexpected error in thread " + t, "Error", e);
            }
        });
        test.setVisible(true);
    }
}
