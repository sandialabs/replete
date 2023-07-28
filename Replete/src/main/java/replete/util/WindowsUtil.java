package replete.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Convenience methods related to the Windows operating system.
 *
 * @author Derek Trumbo
 */

public class WindowsUtil {

    /**
     * Attempts to create a Windows shortcut with the
     * specified descriptor.  Returns boolean indicating
     * success.
     */

    public static boolean createShortcut(WindowsShortcutDescriptor descriptor) {

        // Perform the required parameter validation.

        if(descriptor == null) {
            throw new IllegalArgumentException("Shortcut descriptor is null.");
        }
        if(descriptor.text == null || descriptor.text.trim().equals("")) {
            throw new IllegalArgumentException("Shortcut descriptor text is null or blank.");
        }
        if(descriptor.target == null || descriptor.target.trim().equals("")) {
            throw new IllegalArgumentException("Shortcut descriptor target is null or blank.");
        }
        if(descriptor.destDir == null || !descriptor.destDir.exists()) {
            throw new IllegalArgumentException("Shortcut descriptor destination dir is null or non-existent.");
        }

        // Does not attempt to create a shortcut unless running
        // under windows.
        if(!OsUtil.isWindows()) {
            return false;
        }

        try {

            // Create the VBScript code.

            StringBuffer buf = new StringBuffer();

            buf.append("Option Explicit\r\n");
            buf.append("Dim objShell, objLink\r\n");
            buf.append("Dim strAppPath, strWorkDir, strIconPath\r\n");
            buf.append("Set objShell = CreateObject(\"WScript.Shell\")\r\n");

            File linkFile = new File(descriptor.destDir, descriptor.text + ".lnk");
            buf.append("Set objLink = objShell.CreateShortcut(\"" + linkFile.getAbsolutePath() + "\")\r\n");

            buf.append("objLink.Description = \"" + descriptor.text + "\"\r\n");

            buf.append("strAppPath = \"" + descriptor.target.replaceAll("\"", "\"\"") + "\"\r\n");
            buf.append("objLink.TargetPath = strAppPath\r\n");

            if(descriptor.iconFile != null) {
                buf.append("strIconPath = \"" + descriptor.iconFile.getAbsolutePath() + "\"\r\n");
                buf.append("objLink.IconLocation = strIconPath\r\n");
            }

            if(descriptor.workingDir != null) {
                buf.append("strWorkDir = \"" + descriptor.workingDir.getAbsolutePath() + "\"\r\n");
                buf.append("objLink.WorkingDirectory = strWorkDir\r\n");
            }

            if(descriptor.arguments != null) {
                buf.append("objLink.Arguments = \"" + descriptor.arguments.replaceAll("\"", "\"\"") + "\"\r\n");
            }

            buf.append("objLink.WindowStyle = " + descriptor.state.getValue() + "\r\n");
            buf.append("objLink.Save\r\n");
            buf.append("WScript.Quit\r\n");

            // Write script to file.

            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            File iconScriptFile = new File(tmpDir, "icon.vbs");
            BufferedWriter out = new BufferedWriter(new FileWriter(iconScriptFile));
            out.write(buf.toString());
            out.close();

            // The flag /b should prevent the console window from
            // appearing and disappearing.

            String cmd = "cmd /c start /b \"\" \"" + iconScriptFile.getAbsolutePath() + "\"";

            // The working directory must be specified here in
            // case the JAR is located on a server - the working
            // directory of 'cmd' cannot be a UNC path
            // (\\server\file.jar).  This is just a rule in
            // Windows.
            Process pp = Runtime.getRuntime().exec(cmd, null, tmpDir);

            try {
                pp.waitFor();

                // Check exit value.
                if(pp.exitValue() != 0) {
                    return false;
                }

                // Check for a message on the error stream.
                InputStream s = pp.getErrorStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(s));
                String line;
                String errorText = "";
                while((line = read.readLine()) != null) {
                    errorText += line + "\n";
                }
                if(!errorText.equals("")) {
                    return false;
                }

            } catch(InterruptedException e) {
                return false;
            }

            // It is not important for the icon script file
            // to be deleted from the temp directory.  It
            // is not deleted because many times it would be
            // removed before the above command gets around
            // to reading the file, making the desktop
            // shortcut not to show up.

            return true;

        } catch(IOException e) {
            return false;

        }
    }

    ///////////////
    // Test Main //
    ///////////////

    public static void main(String[] args) {

        // Creates a basic shortcut on the desktop.
        WindowsShortcutDescriptor d = new WindowsShortcutDescriptor();
        d.setArguments("/k echo Hello World");
        System.out.println(createShortcut(d));
    }
}
