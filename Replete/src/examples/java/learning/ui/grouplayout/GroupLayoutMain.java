package learning.ui.grouplayout;
import java.io.IOException;

public class GroupLayoutMain {
    public static void main (String[] args) throws IOException {
//        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread t, Throwable e) {
//                System.out.println(t + " " + e);
//            }
//        });

        GroupLayoutFrame frame = new GroupLayoutFrame();
        frame.setVisible(true);
    }
}
