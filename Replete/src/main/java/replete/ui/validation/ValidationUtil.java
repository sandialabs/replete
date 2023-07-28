package replete.ui.validation;

import java.awt.Window;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ValidationUtil {
    private static Map<Thread, ValidationContext> threadContexts =
        Collections.synchronizedMap(new HashMap<>());

    public static void registerThread(ValidationContext context) {
        threadContexts.put(Thread.currentThread(), context);
    }
    public static void deregisterThread() {
        threadContexts.remove(Thread.currentThread());
    }

    public static ValidationContext get() {
        return threadContexts.get(Thread.currentThread());
    }


    //////////////////////////////
    // VALIDATION CHECK DIALOGS //
    //////////////////////////////

    // Provides common, if basic, functionality for ValidationCheckDialog usage.
    // These methods do not attempt to completely consolidate all uses of
    // ValidationCheckDialog but rather just some of the more common use cases.
    // To consolidate ALL uses of ValidationCheckDialog at the time of this
    // writing, client code would also need the ability to provide custom
    //   1) ValidationContext
    //   2) Title
    //   3) Additional Message
    //   4) Close Button Text
    // That would either entail creating a lot more overloaded methods or
    // employing another common pattern we use where we create a "check validation
    // dialog request" object of sorts where client code sets onto this bean
    // options that dictate how it wants the dialog shown.  There's nothing
    // wrong with such a pattern, just that it does take time to create and
    // that it does add yet another thin layer to code clarity and understandability.

    public static boolean checkValidationPass(Window parent, Validatable validatable) {
        return checkValidationPass(parent, validatable, MessageType.INFO);
    }
    public static boolean checkValidationPass(Window parent, Validatable validatable, MessageType minMessageThreshold) {
        ValidationContext vContext = validatable.validateInput();

        boolean showDialog =
            minMessageThreshold == MessageType.INFO  && vContext.hasMessage(true,  true,  true) ||
            minMessageThreshold == MessageType.WARN  && vContext.hasMessage(false, true,  true) ||
            minMessageThreshold == MessageType.ERROR && vContext.hasMessage(false, false, true);

        if(showDialog) {
            ValidationCheckDialog dlg =
                new ValidationCheckDialog(
                    parent, vContext, false, "Input", null);   // "Input" is just a good generic title prefix here
            dlg.setCloseButtonText("&Return && Review");       // This text is at least applicable to the cases this method is used
            dlg.setVisible(true);
            return dlg.getResult() == ValidationCheckDialog.CONTINUE;
        }

        return true;
    }
}
