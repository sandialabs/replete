
package replete.hash;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import javax.swing.JOptionPane;

import replete.errors.RuntimeConvertedException;
import replete.io.FileUtil;
import replete.ui.fc.RFileChooser;



/**
 * Convenience methods for dealing with MD5.  This class
 * is supposed to make developers' lives easier.  To that end
 * these methods do not throw exceptions, which developers would
 * be forced to catch.  They return false or null when a
 * appropriate and set the thrown exception to a public static
 * reference that can be accessed after the method call.
 * This is only implemented as such with the assumption that
 * the errors are infrequent.  This is a debatable assumption
 * but often in file I/O processing, the developer has high
 * confidence that a given operation will be successful, based
 * on the state of the File objects in question (i.e. how they
 * were constructed, etc.).  In practice this tends to make
 * coding of I/O code much more simple, without taking away
 * the knowledge that an operation failed.  This design is
 * thread safe as the exceptions are kept in a thread to
 * exception map.
 *
 * @author Derek Trumbo
 */

public class Md5Util {


    ////////////////
    // PRINCIPALS //
    ////////////////

    /**
     * IOException assumed to be infrequent.
     * The null return value uniquely identifies
     * that there was one.
     */

    public static String getMd5(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }

        InputStream is = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            is = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int read = 0;
            while((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            return digest2hex(digest);

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getMd5(byte[] bytes) {
        if(bytes == null) {
            throw new IllegalArgumentException("bytes cannot be null.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            return digest2hex(digest);

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    ////////////////////////
    // SUPPORTING METHODS //
    ////////////////////////

    private static String digest2hex(MessageDigest digest) {
        byte[] md5sum = digest.digest();
        StringBuilder hexString = new StringBuilder(32);
        for(int i = 0; i < md5sum.length; i++) {
            hexString.append(hexDigit(md5sum[i]));
        }
        return hexString.toString();
    }

    private static char[] hexDigit(byte x) {

        // First nibble.
        char c1 = (char) ((x >> 4) & 0xf);
        if(c1 > 9) {
            c1 = (char) ((c1 - 10) + 'a');
        } else {
            c1 = (char) (c1 + '0');
        }

        // Second nibble.
        char c2 = (char) (x & 0xf);
        if(c2 > 9) {
            c2 = (char) ((c2 - 10) + 'a');
        } else {
            c2 = (char) (c2 + '0');
        }

        return new char[] {c1, c2};
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

        File srcPath;
        File md5Path;

        // If command line arguments weren't supplied, get paths with
        // dialog boxes.
        if(args.length != 2) {
            RFileChooser chooser = RFileChooser.getChooser("Choose Source File");

            if(!chooser.showOpen(null)) {
                return;
            }

            srcPath = chooser.getSelectedFile();

            chooser = RFileChooser.getChooser("Choose MD5 Validation File (cancel just generates MD5, no validation)");
            if(chooser.showOpen(null)) {
                md5Path = chooser.getSelectedFile();
            } else {
                md5Path = null;
            }

        } else {
            srcPath = new File(args[0]);
            md5Path = new File(args[1]);
        }

        // If a previous MD5 path was supplied, read and validate.
        if(md5Path != null) {

            String md5Old = FileUtil.getTextContent(md5Path).trim();

            System.out.println("MD5 from File: " + md5Old);

            String md5New = getMd5(srcPath);

            System.out.println("Calculated MD5: " + md5New);

            boolean doesVerify = md5New.equals(md5Old);

            System.out.println("MD5 hashes equal?: " + doesVerify);

        // Else just show or create file of MD5 of source file.
        } else {

            int result = JOptionPane.showConfirmDialog(null, "Generate .md5 file? (No just sends MD5 to standard out)", "To File?", JOptionPane.YES_NO_OPTION);

            if(result == JOptionPane.NO_OPTION) {
                String md5New = getMd5(srcPath);
                System.out.println("Calculated MD5: " + md5New);
            } else {
                RFileChooser chooser = RFileChooser.getChooser("Choose MD5 File Destination");

                if(!chooser.showSave(null)) {
                    return;
                }

                File outPath = chooser.getSelectedFile();

                String md5Src = getMd5(srcPath);

                FileUtil.writeTextContent(outPath, md5Src);
            }
        }
    }
}
