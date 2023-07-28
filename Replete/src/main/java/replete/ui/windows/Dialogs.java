package replete.ui.windows;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import replete.errors.ExceptionUtil;
import replete.text.StringUtil;
import replete.ui.GuiUtil;


/**
 * Convenient Dialog Boxes
 *
 * @author Derek Trumbo
 */

public class Dialogs {


    ////////////
    // FIELDS //
    ////////////

    // The only integer fields that the user might have to
    // reference from a dialog result.
    public static final int YES_OPTION = JOptionPane.YES_OPTION;
    public static final int NO_OPTION = JOptionPane.NO_OPTION;
    public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;

    public static final String DFLT_MSG_TITLE  = "Notice";
    public static final String DFLT_WARN_TITLE = "Warning";
    public static final String DFLT_ERR_TITLE  = "Error";
    public static final String DFLT_CONF_TITLE = "Question";


    /////////////////////
    // MESSAGE DIALOGS //
    /////////////////////

    // Private utility methods (allow arbitrary message object)
    private static void showMsgDialog(Component parent, Object msgObj, String title, int messageType) {
        showMsgDialog(parent, msgObj, title, messageType, null);
    }
    private static void showMsgDialog(Component parent, Object msgObj, String title, int messageType, Icon icon) {
        Object message = toMsgStr(msgObj);
        JOptionPaneSpecial.showOptionDialogSpecial(parent, message, title,
            JOptionPane.DEFAULT_OPTION, messageType, icon, null, null, null);
    }

    // Full-parameter (allow arbitrary message object)
    public static void show(Component parent, Object msgObj, String title, int messageType, Icon icon) {
        showMsgDialog(parent, msgObj, title, messageType, icon);
    }
    // Show message
    public static void showMessage(Component parent, String msg) {
        showMsgDialog(parent, msg, DFLT_MSG_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }
    public static void showMessage(Component parent, String msg, String title) {
        showMsgDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
    // Show warning
    public static void showWarning(Component parent, String msg) {
        showMsgDialog(parent, msg, DFLT_WARN_TITLE, JOptionPane.WARNING_MESSAGE);
    }
    public static void showWarning(Component parent, String msg, String title) {
        showMsgDialog(parent, msg, title, JOptionPane.WARNING_MESSAGE);
    }
    // Show error
    public static void showError(Component parent, String msg) {
        showMsgDialog(parent, msg, DFLT_ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }
    public static void showError(Component parent, String msg, String title) {
        showMsgDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE);
    }
    // Show question
    public static void showQuestion(Component parent, String msg) {               // Useful for dialogs presenting 'Help' information
        showMsgDialog(parent, msg, DFLT_ERR_TITLE, JOptionPane.QUESTION_MESSAGE);
    }
    public static void showQuestion(Component parent, String msg, String title) {
        showMsgDialog(parent, msg, title, JOptionPane.QUESTION_MESSAGE);
    }


    ////////////////////
    // OPTION DIALOGS //
    ////////////////////

    // Collector method that can also take an object as the message.
    private static boolean showConfirmDialog(Component parent, Object msg, String title, boolean yn, int msgType) {
        Object message = toMsgStr(msg);
        int optionType;
        if(title == null) {
            title = DFLT_CONF_TITLE;
        }
        if(yn) {
            optionType = JOptionPane.YES_NO_OPTION;
        } else {
            optionType = JOptionPane.OK_CANCEL_OPTION;
        }
        int result = JOptionPaneSpecial.showOptionDialogSpecial(parent, message,
            title, optionType, msgType, null, null, null, null);
        return
            result == JOptionPane.YES_OPTION ||
            result == JOptionPane.OK_OPTION;
    }
    // Confirm message
    public static boolean showConfirm(Component parent, String msg) {
        return showConfirm(parent, msg, null, false);
    }
    public static boolean showConfirm(Component parent, String msg, boolean yn) {
        return showConfirm(parent, msg, null, yn);
    }
    public static boolean showConfirm(Component parent, String msg, String title) {
        return showConfirm(parent, msg, title, false);
    }
    // Confirm message collector method that can also take an object as the message.
    public static boolean showConfirm(Component parent, Object msg, String title, boolean yn) {
        return showConfirmDialog(parent, msg, title, yn, JOptionPane.QUESTION_MESSAGE);
    }
    // Confirm warning
    public static boolean showConfirmWarning(Component parent, String msg) {
        return showConfirmWarning(parent, msg, null, false);
    }
    public static boolean showConfirmWarning(Component parent, String msg, boolean yn) {
        return showConfirmWarning(parent, msg, null, yn);
    }
    public static boolean showConfirmWarning(Component parent, String msg, String title) {
        return showConfirmWarning(parent, msg, title, false);
    }
    // Confirm warning collector method that can also take an object as the message.
    public static boolean showConfirmWarning(Component parent, Object msg, String title, boolean yn) {
        return showConfirmDialog(parent, msg, title, yn, JOptionPane.WARNING_MESSAGE);
    }
    // Confirm error
    public static boolean showConfirmError(Component parent, String msg) {
        return showConfirmError(parent, msg, null, false);
    }
    public static boolean showConfirmError(Component parent, String msg, boolean yn) {
        return showConfirmError(parent, msg, null, yn);
    }
    public static boolean showConfirmError(Component parent, String msg, String title) {
        return showConfirmError(parent, msg, title, false);
    }
    // Confirm error collector method that can also take an object as the message.
    public static boolean showConfirmError(Component parent, Object msg, String title, boolean yn) {
        return showConfirmDialog(parent, msg, title, yn, JOptionPane.ERROR_MESSAGE);
    }


    //////////////////
    // MULTI-OPTION //
    //////////////////

    public static int showMulti(Component parent, String msg) {
        return showMulti(parent, msg, null, null, -1, null);
    }
    public static int showMulti(Component parent, String msg, int messageType) {
        return showMulti(parent, msg, null, null, messageType, null);
    }
    public static int showMulti(Component parent, String msg, String[] vals) {
        return showMulti(parent, msg, null, vals, -1, null);
    }
    public static int showMulti(Component parent, String msg, String[] vals, int messageType) {
        return showMulti(parent, msg, null, vals, messageType, null);
    }
    public static int showMulti(Component parent, String msg, String title, String[] vals) {
        return showMulti(parent, msg, title, vals, -1, null);
    }
    public static int showMulti(Component parent, Object msg, String title, String[] vals, int messageType) {
        return showMulti(parent, msg, title, vals, messageType, null);
    }
    // Collector method that can also take an object as the message.
    public static int showMulti(Component parent, Object msg, String title, String[] vals, int messageType, Map<String, Icon> buttonIcons) {
        Object message = toMsgStr(msg);

        if(title == null) {
            title = DFLT_CONF_TITLE;
        }

        if(messageType == -1) {
            messageType = JOptionPane.QUESTION_MESSAGE;
        }

        if(vals == null) {
            return JOptionPaneSpecial.showOptionDialogSpecial(parent, message, title,
                            JOptionPane.YES_NO_CANCEL_OPTION, messageType,
                            null, null, null, buttonIcons);
        }

        return JOptionPaneSpecial.showOptionDialogSpecial(parent, message, title,
                        JOptionPane.DEFAULT_OPTION, messageType,
                        null, vals, vals[0], buttonIcons);
    }


    ///////////////////
    // INPUT DIALOGS //
    ///////////////////

    public static String showInput(Component parent, String msg) {
        return showInput(parent, msg, null, null);
    }
    public static String showInput(Component parent, String msg, String title) {
        return showInput(parent, msg, title, null);
    }
    // Collector method that can also take an object as the message.
    public static String showInput(Component parent, Object msg, String title, String initVal) {
        Object msgObj = toMsgStr(msg);
        if(title == null) {
            title = DFLT_CONF_TITLE;
        }
        return (String) JOptionPaneSpecial.showInputDialogSpecial(parent, msgObj, title,
                        JOptionPane.QUESTION_MESSAGE, null, null, initVal);
    }


    ////////////////////
    // DETAIL DIALOGS //
    ////////////////////

    // Right now just to be used with errors, but could be expanded.

    // String
    public static void showDetails(Component parent, Throwable t) {
        showDetails(parent, null, null, t);
    }
    public static void showDetails(Component parent, String msg, String detailsMessage) {
        showDetails(parent, msg, null, detailsMessage);
    }
    public static void showDetails(Component parent, Object msg, String title, String detailsMessage) {
        showDetails(parent,
            new ExceptionDetails()
                .setMessage(msg)
                .setTitle(title)
                .setDetailsMessage(detailsMessage));
    }

    // Throwable
    public static void showDetails(Component parent, String msg, Throwable t) {
        showDetails(parent, msg, null, t);
    }
    public static void showDetails(Component parent, String msg, String title, Throwable t) {
        showDetails(parent,
            new ExceptionDetails()
                .setMessage(msg)
                .setTitle(title)
                .setError(t));
    }

    // Collector method that can also take an object as the message.
    public static void showDetails(Component parent, ExceptionDetails details) {
        if(details.getError() != null || details.isForceLargeDialog()) {
            Window win = GuiUtil.win(parent);
            ExceptionDialog dlg = new ExceptionDialog(win, details);
            dlg.setVisible(true);
            return;
        }

        Object message = toMsgStr(details.getMessage());
        String title = details.getTitle();

        if(title == null) {
            title = DFLT_ERR_TITLE;
        }

        if(details.isPrintStackTrace() && details.getError() != null) {
            details.getError().printStackTrace();
        }

        String detailsMessage = "";
        if(details.getDetailsMessage() != null) {
            detailsMessage += details.getDetailsMessage();
        }
        if(details.getError() != null) {
            if(!detailsMessage.isEmpty()) {
                detailsMessage += "\n";
            }
            detailsMessage +=
                ExceptionUtil.toCompleteString(details.getError(), 4).trim();
        }

        File ws = new File("C:\\Users\\dtrumbo\\work\\eclipse-main");
        details
            .addSourceDir(new File(ws, "Avondale\\src"))
            .addSourceDir(new File(ws, "Cortext\\src"))
            .addSourceDir(new File(ws, "Finio\\src"))
            .addSourceDir(new File(ws, "Finio\\plugins"))
            .addSourceDir(new File(ws, "Reframe\\src"))
            .addSourceDir(new File(ws, "Reframe\\test"))
            .addSourceDir(new File(ws, "Replete\\src"))
            .addSourceDir(new File(ws, "RepleteExternals\\src"))
            .addSourceDir(new File(ws, "Orbweaver\\src"))
            .addSourceDir(new File(ws, "WebComms\\src"));


        JOptionPaneSpecial.showDetailsDialog(parent, message, title,
            JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
            new String[] {"OK", JOptionPaneSpecial.DetailsInfo.SHOW_DETAILS},
            "OK", detailsMessage, details);
    }


    //////////
    // MISC //
    //////////

    private static Object toMsgStr(Object msg) {
        if(msg == null) {
            return "An error has occurred.";
        } else if(msg instanceof Component) {
            return msg;
        }
        return msg.toString();
    }

    /** NO ONE USING THIS?
     * Formats the input message by adding newlines so it does not contain extremely long lines.
     * This should make dialogs printing the message easier to use.
     * @param original format this message
     * @return new string containing the same information as the original string but with
     *  newlines added to prevent long lines in visual dialogs.
     */
    public static String formatMessage(final String original) {
        StringBuffer formatted = new StringBuffer();

        int begin = 0;
        final int count = 110;
        Pattern pattern = Pattern.compile("\\s+");

        while(begin < original.length()) {
            // Find the first whitespace character after the substring ends.  If one is found within
            // a certain amount of characters then use it instead since it will prevent breaking up
            // words.
            int end = Math.min(begin + count, original.length());
            Matcher matcher = pattern.matcher(original.substring(end));
            if(matcher.find() && matcher.start() < 35) {
                end += matcher.start();
            }

            // Add in the new portion of the message.  Only append up to the last newline.
            String toAppend = original.substring(begin, end);

            // Find the last newline character in the substring
            int lastNL = toAppend.lastIndexOf("\n");
            lastNL = Math.max(lastNL, toAppend.lastIndexOf("\r"));
            lastNL = Math.max(lastNL, toAppend.lastIndexOf("\r\n"));

            // If there was a newline then only append up to the last newline
            if(lastNL >= 0) {
                end = begin + lastNL + 1;
                formatted.append(original.substring(begin, end));
            }

            // Otherwise, append the full line followed by a newline.
            else {
                formatted.append(toAppend.trim());
                formatted.append("\n");
            }

            begin = end;
        }

        return formatted.toString();
    }

    public static void debug() {
        debug(null);
    }
    public static void debug(Component parent) {
        showMessage(parent, "Debug Message");
    }
    public static void notImpl() {
        notImpl(null, null);
    }
    public static void notImpl(String feature) {
        notImpl(null, feature);
    }
    public static void notImpl(Component parent) {
        notImpl(parent, null);
    }
    public static void notImpl(Component parent, String feature) {
        if(!StringUtil.isBlank(feature)) {
            showWarning(parent, "Sorry the feature '" + feature + "' is not implemented", "Not Implemented");
        } else {
            showWarning(parent, "Sorry this feature is not implemented", "Not Implemented");
        }
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        Dialogs.showDetails(null,
            new ExceptionDetails()
                .setMessage("hi")
                .setTitle("TITLE")
//                .setDetailsMessage("asdlfkj asflkdaskf jlaskf lksafj\nasldkf jaslkdf jalskfd jaslkfj ")
                .setError(new RuntimeException())
                .setPrintStackTrace(true)
                .addSourceDir(new File(
                    "C:\\Users\\dtrumbo\\work\\eclipse-main\\Replete\\src"))
        );
    }
}
