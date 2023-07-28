package replete.util;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import replete.ui.fc.RFileChooser;



/**
 * Convenience methods for dealing with ZIP files.
 *
 * @author Derek Trumbo
 */

public class ZipUtil {

    public static boolean isGZIPFormat(File f) {
        try {
            InputStream is = new GZIPInputStream(new FileInputStream(f));
            is.close();
            return true;
        } catch(IOException e) {
            // Exception's message should be "Not in GZIP format".
            return false;
        }
    }

    public static boolean isZIPFormat(File f) {
        try {
            ZipFile z = new ZipFile(f);
            z.close();
            return true;
        } catch(ZipException e) {
            // Haven't seen this happen, but signal not ZIP format anyway.
            return false;
        } catch(IOException e) {
            // Exception's message should be "error in opening zip file".
            return false;
        }
    }

    // Above methods used for CC.  Below methods still need cleaning up.

    public static boolean isZipFile(File file) {
        ZipInputStream zipinputstream;
        ZipEntry entry = null;

        try {
            zipinputstream = new ZipInputStream(new FileInputStream(file));

            entry = zipinputstream.getNextEntry();
        } catch (Exception e) {
            return false;
        }

        return entry != null;
    }

    public static final void zipDirectory(File directory, File zip) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
        zip(directory, directory, zos);
        zos.close();
    }


    private static final void zip(File directory, File base, ZipOutputStream zos)
                    throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[8192];
        int read = 0;
        for(int i = 0, n = files.length; i < n; i++) {
            if(files[i].isDirectory()) {
                zip(files[i], base, zos);
            } else {
                FileInputStream in = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath().substring(
                                base.getPath().length() + 1));
                zos.putNextEntry(entry);
                while(-1 != (read = in.read(buffer))) {
                    zos.write(buffer, 0, read);
                }
                in.close();
            }
        }
    }

    public static final void zipSingleFile(File file, File zipFile) throws IOException {
        int read = 0;
        byte[] buffer = new byte[8192];
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        FileInputStream in = new FileInputStream(file);
        ZipEntry entry = new ZipEntry(file.getName());
        zos.putNextEntry(entry);
        while(-1 != (read = in.read(buffer))) {
            zos.write(buffer, 0, read);
        }
        zos.closeEntry();
        in.close();

        zos.close();
    }

    public static final void unzip(File zip, File extractTo) throws IOException {
        ZipFile archive = new ZipFile(zip);
        Enumeration e = archive.entries();
        while(e.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) e.nextElement();

            String entryName = entry.getName();

            //Java Zip library will save file names with native file separator
            //for the system.  This means that zip files built in Windows
            //may not load correctly in Unix because of the use of \ as a
            //file separator.  Here we find uses of \ as a separator and replace
            //it with the correct file system separator to ensure cross-compatibility
            entryName = entryName.replace('\\', File.separatorChar);

            File file = new File(extractTo, entryName);

            if(entry.isDirectory()) {
                if(!file.exists()) {
                    file.mkdirs();
                }
            } else {
                if(!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                InputStream in = archive.getInputStream(entry);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

                byte[] buffer = new byte[8192];
                int read;

                while(-1 != (read = in.read(buffer))) {
                    out.write(buffer, 0, read);
                }

                in.close();
                out.close();
            }
        }

        archive.close();
    }

    //////////
    // TEST //
    //////////

    // Run this class as a Java application to have dialog boxes guide you
    // through selecting the files on which you want the above ZipUtility
    // methods to be performed. You can change the 'startDir' variable
    // for your convenience. The dialog boxes don't care about file
    // extensions so you can choose a *.ziwx file as your source ZIP
    // file for example.
    public static void main(String[] args) throws IOException {

        String startDir = null;

        RFileChooser chooser = RFileChooser.getChooser();

        if(startDir != null) {
            File startDirFile = new File(startDir);
            if(startDirFile.exists()) {
                chooser.setCurrentDirectory(startDirFile);
            }
        }

        int zipUnzipResult =
            JOptionPane.showConfirmDialog(null, "Yes for ZIP, No for UNZIP, Cancel to CANCEL");

        // Zip
        if(zipUnzipResult == JOptionPane.YES_OPTION) {

            int fileDirResult =
                JOptionPane.showConfirmDialog(null,
                    "ZIP: Yes for SINGLE FILE, No for DIRECTORY, Cancel to CANCEL");

            if(fileDirResult == JOptionPane.YES_OPTION) {

                // Get source file
                chooser.setDialogTitle("Choose Source File");
                if(!chooser.showOpen(null)) {
                    return;
                }
                File srcFile = chooser.getSelectedFile();

                // Get destination file
                chooser.setDialogTitle("Choose Destination File");
                if(!chooser.showSave(null)) {
                    return;
                }
                File dstFile = chooser.getSelectedFile();

                zipSingleFile(srcFile, dstFile);

            } else if(fileDirResult == JOptionPane.NO_OPTION) {

                // Get source directory
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Choose Source Directory");
                if(!chooser.showOpen(null)) {
                    return;
                }
                File srcDir = chooser.getSelectedFile();

                // Get destination file
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setDialogTitle("Choose Destination File");
                if(!chooser.showSave(null)) {
                    return;
                }
                File dstFile = chooser.getSelectedFile();

                zipDirectory(srcDir, dstFile);

            }

        // Unzip
        } else if(zipUnzipResult == JOptionPane.NO_OPTION) {

            // Get source file
            chooser.setDialogTitle("Choose Source File");
            if(!chooser.showOpen(null)) {
                return;
            }
            File srcFile = chooser.getSelectedFile();

            // Get source directory
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Choose Destination Directory");
            if(!chooser.showOpen(null)) {
                return;
            }
            File dstDir = chooser.getSelectedFile();

            unzip(srcFile, dstDir);

            System.out.println("Unzipped");

            // Intermittently the Java process will not die along
            // this path because for some reason a handle to the
            // input ZIP file is being kept open. The unzip
            // method was probably successful however and you can
            // just click stop in Eclipse. The mere presence
            // of the above print statement seems to rectify this
            // problem...

        }
    }

}
