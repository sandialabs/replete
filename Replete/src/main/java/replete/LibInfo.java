package replete;



/**
 * Used just to display an information message if the
 * user happens to execute the JAR as in a double-click
 * from the GUI.
 *
 * @author Derek Trumbo
 */

public class LibInfo {

    // TODO: This app needs class not found error protection
    // (silent failure for jtattoo, warnings for XStream / Mail).

    private static String MSG =
        "<html><u>Replete</u><br><br>" +
        "This library contains generic code that could " +
        "be useful in many Java applications.  Some " +
        "code is related to Java desktop applications " +
        "where a UI is involved and some code can be " +
        "used in any Java application, with or without " +
        "a UI.<br><br><i>Derek Trumbo</i></html>";

    private static String TITLE = "Replete Library Information";

    private static LibInfoFrame frame;

    public static void main(String[] args) {

        frame = new LibInfoFrame(TITLE, MSG);
        frame.setVisible(true);

//        LafManager.setNeedToRebootListener(new RebootFramesListener() {
//            @Override
//            public void reboot() {
//                frame.dispose();
//                frame = new LibInfoFrame(TITLE, MSG);
//                frame.setVisible(true);
//            }
//            @Override
//            public boolean allowReboot() {
//                return true;
//            }
//        });
    }
}
