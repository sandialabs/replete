package replete.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import replete.collections.RLinkedHashMap;
import replete.errors.RuntimeConvertedException;
import replete.text.NewlineType;
import replete.text.StringUtil;
import replete.util.OsUtil;

/**
 * Convenience methods for dealing with files.  This class
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

public class FileUtil {


    //////////
    // FIND //
    //////////

//    File parent = new File("C:\\Users\\dtrumbo\\Desktop");
//    List<File> allFiles = find(parent);
//    List<File> allFiles = find(parent, new FileFilter() {
//        public boolean accept(File file) {
//            return
//                file.isDirectory() ||      // Directory recurse rule (all directories)
//                (file.isFile() && file.getName().endsWith(".ini"));   // File match rule
//        }
//    });
//    List<File> allFiles = find(parent, new FileFilter() {
//        public boolean accept(File file) {
//            return
//                (file.isDirectory() && file.getName().equals("Browsers")) ||  // Directory recurse rule
//                (file.isFile() && file.getName().endsWith(".ini"));           // File match rule
//        }
//    });
//    for(File file : allFiles) {
//        System.out.println(file);
//    }

    public static List<File> find(File parent) {
        List<File> parents = new ArrayList<>();
        parents.add(parent);
        return find(parents, null);
    }
    public static List<File> find(File parent, FileFilter filter) {
        List<File> parents = new ArrayList<>();
        parents.add(parent);
        return find(parents, filter);
    }

    public static List<File> find(List<File> parents) {
        return find(parents, null);
    }
    public static List<File> find(List<File> parents, FileFilter filter) {
        List<File> found = new ArrayList<>();
        for(File parent : parents) {
            if(isReadableDir(parent)) {
                find(parent, filter, found);
            }
        }
        return found;
    }

    private static void find(File parent, FileFilter filter, List<File> found) {
        for(File child : parent.listFiles()) {
            if(filter == null || filter.accept(child)) {
                if(child.isDirectory()) {
                    find(child, filter, found);
                } else {
                    found.add(child);
                }
            }
        }
    }

    // Copies src file to dst file.  If the dst file
    // does not exist, it is created
    public static void copy(File srcFile, File dstPath) {
        try {
            File dstFile;

            if(dstPath.isDirectory()) {
                dstFile = new File(dstPath, srcFile.getName());
            } else {
                dstFile = dstPath;
            }

            InputStream in = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(dstFile);

            // Transfer bytes from in to out.
            byte[] buf = new byte[8192];
            int numRead;
            while((numRead = in.read(buf)) != -1) {
                out.write(buf, 0, numRead);
            }

            in.close();
            out.close();

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    /**
     * Completely delete a directory and all of its
     * contents.
     */
    public static boolean deleteDirectory(File dir) {
        if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(int f = 0; f < files.length; f++) {
                if(files[f].isDirectory()) {
                    deleteDirectory(files[f]);
                } else {
                    files[f].delete();
                }
            }

            return dir.delete();
        }

        // File object is not a directory.
        return false;
    }

    /**
     * Removes all files from a directory.  Leaves
     * subdirectories.
     */

    public static void clearDirectory(String dirName) {
        File tmpDir = new File(dirName);
        if(!tmpDir.exists()) {
            return;
        }

        String[] children = tmpDir.list();
        for(int i = 0; i < children.length; i++) {
            File delFile = new File(tmpDir, children[i]);
            if(!delFile.isDirectory()) {
                delFile.delete();
            }
        }
    }

    public static void ensureDirectoryExists(String dirName) {
        File tmpDir = new File(dirName);
        if(!tmpDir.exists()) {
            tmpDir.mkdir();
        }
    }

    /**
     * Convert the Windows pathname, which may include mapped network drives,
     * to a full UNC pathname. This help stored filenames be consistent when
     * viewed and used by different users.
     *
     * @param pathname String full name of the Windows path
     * @return String the full UNC pathname
     */
    public static String getUncPathname(String pathname) {
        if(!OsUtil.isWindows()) {
            return pathname;
        }

        String uncPathname = pathname;

        //find drive letter on pathname
        if (pathname.length() > 1 && pathname.charAt(1) == ':') {
            String drivename = pathname.substring(0, 2);

            //find out whether the drive letter is mapped to a network drive.
            //use the "net use" command in windows to get the mapped drive info.
            String[] command = new String[3];
            command[0] = "C:\\WINDOWS\\system32\\cmd.exe";
            command[1] = "/C";
            command[2] = "net use";
            try {
                Process proc;
                proc = Runtime.getRuntime().exec(command);

                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                ArrayList<String> cmdOutput = new ArrayList<String>();
                String line = null;
                while ( (line = reader.readLine()) != null) {
                    cmdOutput.add(line);
                }

                try {
                    proc.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("interrupted exception on process reader");
                }

                for(int i = 0; i < cmdOutput.size(); i++) {
                    String currentLine = cmdOutput.get(i);
                    int pos = currentLine.indexOf(drivename);

                    if (pos > -1) {
                        String restOfLine = currentLine.substring(pos+2);

                        //find next non-space character
                        while (restOfLine.charAt(0) == ' ') {
                            restOfLine = restOfLine.substring(1);
                        }

                        //find next space character
                        int spacePos = restOfLine.indexOf(' ');
                        String uncLeader = restOfLine.substring(0, spacePos);
                        uncPathname = uncLeader + pathname.substring(2);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Problem accessing shared drive info from Windows.");
            }
        }
        return uncPathname;
    }

    // Change to File object someday.
    public static String getUncPath(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }
        String uncPathname = getUncPathname(file.getAbsolutePath());
        return uncPathname.substring(0, uncPathname.lastIndexOf("\\") + 1);
    }

    /**
     * Return the entire contents of a file in a single string.
     * If any error occurs, null will be returned.  This is
     * justified over having this method throw an IOException because
     * the likelihood of an exception is low, and null cannot
     * be returned any other way (even a blank file is "").
     * An exception will be thrown, however, if the input parameter
     * is null.
     */
    public static String getTextContent(File file) {

        // Read in the file content but default to using
        // only newline (\n) characters (Unix style).
        return getTextContent(file, false, NewlineType.LF);
    }
    public static String getTextContent(File file, boolean keepNewlines) {
        return getTextContent(file, keepNewlines, NewlineType.LF);
    }
    public static String getTextContent(File file, boolean keepNewlines, NewlineType nlType) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            return getTextContent(reader, keepNewlines, nlType);

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(Exception e) {
                    throw new RuntimeConvertedException(e);
                }
            }
        }
    }
    public static String getTextContent(InputStream stream) {

        // Read in the file content but default to using
        // only newline (\n) characters (Unix style).
        return getTextContent(stream, false, NewlineType.LF);
    }
    public static String getTextContent(InputStream stream, boolean keepNewlines) {
        return getTextContent(stream, keepNewlines, NewlineType.LF);
    }
    public static String getTextContent(InputStream stream, boolean keepNewlines, NewlineType nlType) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            return getTextContent(reader, keepNewlines, nlType);

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(Exception e) {
                    throw new RuntimeConvertedException(e);
                }
            }
        }
    }
    private static String getTextContent(BufferedReader reader, boolean keepNewlines, NewlineType nlType)
            throws IOException {
        StringBuffer lines = new StringBuffer();
        if(keepNewlines || nlType == NewlineType.AUTO) {
            char[] buf = new char[8192];     // Initial buffer
            int numRead;
            while((numRead = reader.read(buf)) != -1) {
                lines.append(buf, 0, numRead);
            }
        } else {
            String line;
            String nlChars = "";
            while((line = reader.readLine()) != null) {
                lines.append(line);
                switch(nlType) {
                    case NONE: case MIXED: nlChars = NewlineType.LF.image; break;
                    default: nlChars = nlType.image;
                }
                lines.append(nlChars);
            }
//            lines.delete(lines.length() - nlChars.length(), lines.length());
        }
        return lines.toString();
    }

    public static int getTextLineCount(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int lineCount = 0;
            while(reader.readLine() != null) {
                lineCount++;
            }

            reader.close();
            return lineCount;

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    /**
     * Because these are supposed to be convenient for the
     * developer, they do not throw exceptions.   Indication
     * of the failure is returned in the boolean return
     * value.  When the methods return false the exception
     * can be retrieved from getLastException.  In the future
     * this method will probably use the newline type not
     * only for the final newline, but to convert the text
     * content to that newline type before writing.
     */

    public static boolean writeTextContent(File dstPath, String content) {
        return writeTextContent(dstPath, content, false, false, NewlineType.AUTO);
    }
    public static boolean writeTextContent(File dstPath, String content, NewlineType finalNL) {
        return writeTextContent(dstPath, content, false, false, finalNL);
    }
    public static boolean writeTextContent(File dstPath, String content, boolean append) {
        return writeTextContent(dstPath, content, append, false, NewlineType.AUTO);
    }
    public static boolean writeTextContent(File dstPath, String content, boolean append, NewlineType finalNL) {
        return writeTextContent(dstPath, content, append, false, finalNL);
    }
    public static boolean writeTextContent(File dstPath, String content, boolean append, boolean suppressNewline) {
        return writeTextContent(dstPath, content, append, suppressNewline, NewlineType.AUTO);
    }
    public static boolean writeTextContent(File dstPath, String content, boolean append, boolean suppressNewline, NewlineType finalNL) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(dstPath, append));
            out.write(content);
            if(!suppressNewline) {
                String nlToUse;
                switch(finalNL) {
                    case AUTO:
                        NewlineType detected = StringUtil.getFirstNewline(content);
                        switch(detected) {
                            case NONE: nlToUse = NewlineType.LF.image; break;
                            default: nlToUse = detected.image; break;
                        }
                        break;

                    case CR:
                    case LF:
                    case CRLF: nlToUse = finalNL.image;          break;
                    case NONE: nlToUse = "";                     break;
                    default:   nlToUse = NewlineType.LF.image;   break;     // MIXED cases
                }
                out.write(nlToUse);
            }
            return true;
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);

        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch(IOException e) {
                    throw new RuntimeConvertedException(e);
                }
            }
        }
    }

    /**
     * Null parameters throw an exception because there is
     * no way to both have a return value of null indicate
     * an error and indicate lack of a period in the name.
     * These are the different cases it works on:
     *   C:\dir\file            null
     *   C:\dir\file.           ""
     *   C:\dir\file.txt        "txt"
     *   C:\dir\file.new.txt    "txt"
     */
    public static String getExtension(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }

        String name = file.getName();
        int lastDot = name.lastIndexOf(".");

        // If there is no "." use null instead of "" so that
        // "" can mean this:  "C:\dir\myFile."
        if(lastDot == -1) {
            return null;
        }

        return name.substring(lastDot + 1);
    }

    /**
     * Null parameter throws an exception to be consistent
     * with getExtension.
     * These are the different cases it works on:
     *   C:\dir\file            "file"
     *   C:\dir\file.           "file"
     *   C:\dir\file.txt        "file"
     *   C:\dir\file.new.txt    "file.new"
     */
    public static String getNameWithoutExtension(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }
        return getNameWithoutExtension(file.getName());
    }
    public static String getNameWithoutExtension(String fileNameOrPath) {
        if(fileNameOrPath == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }
        int lastDot = fileNameOrPath.lastIndexOf(".");

        if(lastDot == -1) {
            return fileNameOrPath;
        }

        return fileNameOrPath.substring(0, lastDot);
    }

    /**
     * Returns exactly the same path, except with a new
     * extension.  If newExt is null, any existing extension
     * is removed.
     */
    public static File switchExtension(File file, String newExt) {
        if(file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }

        String name = getNameWithoutExtension(file);

        if(newExt == null) {
            newExt = "";
        } else {
            if(!newExt.startsWith(".")) {
                newExt = "." + newExt;
            }
        }

        return new File(file.getAbsoluteFile().getParentFile().getAbsolutePath(),
            name + newExt);
    }

    // An equals that asks if the two file objects point to the
    // same physical file on disk, rather than whether their
    // path strings inside are the same.  In other words,
    // File.equals will return false between the following two
    // File objects:
    //    new File("A").equals(new File(".\\A"))
    // The File.equals method does however handle whether or not
    // the path comparison should be case sensitive depending
    // on operating system.
    @SuppressWarnings("null")
    public static boolean equals(File file1, File file2) {
        if(file1 == null && file2 == null) {
            return true;
        } else if(file1 == null ^ file2 == null) {
            return false;
        }

        try {
            return file1.getCanonicalFile().equals(file2.getCanonicalFile());
        } catch(IOException e) {
            return file1.getAbsoluteFile().equals(file2.getAbsoluteFile());
        }
    }

    // To be used on Windows systems to change the
    // path stored into a File object to have the
    // case that the file it points to actually has.
    public static File correctCase(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }

        // If the file doesn't exist, or this code is not
        // being executed on a case-insensitive OS, then
        // there is nothing to correct.
        if(!OsUtil.isWindows() || !file.exists()) {
            return file;
        }

        File parent = file.getAbsoluteFile().getParentFile();

        File[] siblings = parent.listFiles();

        for(File sibling : siblings) {
            String sName = sibling.getName();
            String fName = file.getName();
            if(sName.equalsIgnoreCase(fName)) {
                return sibling;
            }
        }

        return file;   // Shouldn't happen.
    }

    // Returns whether or not the given string is non-null
    // AND non-empty and whether or not it exists as a
    // file or directory on the file system.  Of note is that
    // the empty string is a valid path, as it represents
    // the current working directory.  However, this method
    // will return false if an empty string is passed to it.
    public static boolean existsPath(String path) {
        return path != null && !path.equals("") && new File(path).exists();
    }

    public static boolean isReadableDir(File f) {
        return f != null && f.isDirectory() && f.canRead();
    }
    public static boolean isReadableFile(File f) {
        return f != null && f.isFile() && f.canRead();
    }
    public static boolean isWritableDir(File f) {
        return isReadableDir(f) && f.canWrite();
    }
    public static boolean isWritableFile(File f) {   // TODO: Not implemented correctly
        return isReadableFile(f) && f.canWrite();
    }

    public static long getFileSize(File file) {
        if(file.isFile()) {
            return file.length();
        }
        long folderSize = 0;
        File[] fileList = file.listFiles();
        for(int i = 0; i < fileList.length; i++) {
            folderSize += getFileSize(fileList[i]);
        }
        return folderSize;
    }

    public static long parseByteSize(String size) {
        String[] captures = StringUtil.extractCaptures(size, "-?([0-9]*(?:\\.[0-9]*)?) ?(GB|MB|KB|B)");
        if(captures != null) {
            Map<String, Long> coeff = new RLinkedHashMap<>(
                 "B", 1L,
                "KB", 1000L,
                "MB", 1000000L,
                "GB", 1000000000L
            );
            Long c = coeff.get(captures[1]);
            return (long) (Double.parseDouble(captures[0]) * c);
        }
        return 0L;
    }
    public static String getReadableSizeString(File file) {
        return getReadableSizeString(getFileSize(file));
    }
    public static String getReadableSizeString(long size) {
        if(size < 1000) {                   // Less than 1 KB = 1,000 B; e.g. 736 B
            return size + " B";
        } else if(size < 100000) {          // Less than 100 KB = 100,000 B; e.g. 38.4 KB
            return String.format("%.1f KB", size / 1000.0);
        } else if(size < 1000000) {         //  Less than 1 MB = 1,000 KB = 1,000,000 B; e.g. 346 KB
            return String.format("%.0f KB", size / 1000.0);
        } else if(size < 100000000) {       //  Less than 100 MB = 100,000 KB = 100,000,000 B; e.g. 77.2 MB
            return String.format("%.1f MB", size / 1000000.0);
        } else if(size < 1000000000) {      //  Less than 1 GB = 1,000 MB = 1,000,000 KB = 1,000,000,000 B; e.g. 937 MB
            return String.format("%.0f MB", size / 1000000.0);
        }
        return String.format("%.1f GB", size / 1000000000.0);
    }

    public static String getReadableSizeStringBinary(long size) {
        if(size < 1024) {                                // Less than 1 KiB = 1,024 B;
            return size + " B";
        } else if(size < (1024 * 1024 / 10)) {
            return String.format("%.1f KiB", size / 1024.0);
        } else if(size < (1024 * 1024)) {                //  Less than 1 MiB = 1,024 KiB
            return String.format("%.0f KiB", size / 1024.0);
        } else if(size < (1024 * 1024 * 1024 / 10)) {
            return String.format("%.1f MiB", size / (1024.0 * 1024.0));
        } else if(size < (1024 * 1024 * 1024)) {         //  Less than 1 GiB = 1,024 MiB
            return String.format("%.0f MiB", size / (1024.0 * 1024.0));
        }
        return String.format("%.1f GiB", size / (1024.0 * 1024.0 * 1024.0));
    }

    public static void writeObjectContent(Serializable obj, File file) {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(obj);

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    public static Object getObjectContent(File file) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    // TOOD: Bugs can happen in this situation:
    //   cleanForFileName("Captain?America") -> "CaptainAmerica"
    //   cleanForFileName("Captain&America") -> "CaptainAmerica"
    // ... :/  It's up to users to know this doesn't create a 
    // 1:1 reversible mapping.
    public static String cleanForFileName(String fileNamePart) {
        return cleanForFileName(fileNamePart, null);
    }
    public static String cleanForFileName(String fileNamePart, String disallowChars) {
        fileNamePart = fileNamePart.replaceAll("[^.a-zA-Z0-9_ -]", "");
        if(disallowChars != null && !disallowChars.equals("")) {
            fileNamePart = fileNamePart.replaceAll("[\\Q" + disallowChars + "\\E]", "");
        }
        return fileNamePart;
    }

    public static void copyDirectory(File dir, File file) {
        // TODO
    }

/*
    private class Properties

    public static Map<String, String> readPropertiesFile(File propFile) {
        Map<String, String> props = new HashMap<String, String>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(propFile));

            String line;
            String curKey = null;
            String curValue = "";

            while( ( line = reader.readLine() ) != null) {

                // Trim and ignore blank and comment lines.
                line = line.trim();
                if( line.startsWith( "#" ) ) {
                    continue;
                }

                // Find location of equals character.
                int equal = line.indexOf( '=' );

                // Assume line is not a key= line initially.
                boolean isKeyLine = false;

                // If the equals character is at least the second character on
                // the line...
                if( equal > 0 ) {

                    // Grab the character before the equals sign.
                    char before = line.charAt( equal - 1 );

                    // If the character is not a backslash, then this line is
                    // a key= line, else it is a regular line, and remove
                    // the slash.
                    if( before != '\\' ) {
                        isKeyLine = true;
                    } else {
                        line = line.substring( 0, equal - 1 ) + line.substring( equal );
                    }
                }

                if( isKeyLine ) {
                    if( curKey != null ) {
                        while( curValue.length() > 0 && curValue.charAt( curValue.length() - 1 ) == '\n' ) {
                            curValue = curValue.substring( 0, curValue.length() - 1 );
                        }
                        props.put( curKey, curValue );
                    }
                    curKey = line.substring( 0, equal ).trim();
                    curValue = line.substring( equal + 1 ) + "\n";
                } else {
                    curValue += line + "\n";
                }
            }

            if( curKey != null ) {
                while( curValue.length() > 0 && curValue.charAt( curValue.length() - 1 ) == '\n' ) {
                    curValue = curValue.substring( 0, curValue.length() - 1 );
                }
                props.put( curKey, curValue );
            }

        } catch(Exception e) {
            System.err.println( "ERROR: Problem reading properties file.  Some properties may not have been read.  " + e.getMessage() );

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(Exception e) {

                }
            }
        }
        return props;
    }
    */

    public static String[] getAbsPaths(File[] files) {
        if(files == null) {
            return null;
        }
        String[] paths = new String[files.length];
        int f = 0;
        for(File file : files) {
            paths[f++] = file.getAbsolutePath();
        }
        return paths;
    }

    public static void writeBytes(byte[] bytes, File file) {
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            bos.write(bytes);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readBytes(InputStream is) throws BeyondMaxException, IOException {
        return readBytes(is, 1024, Long.MAX_VALUE);
    }
    public static byte[] readBytes(InputStream is, int bufferSize) throws BeyondMaxException, IOException {
        return readBytes(is, bufferSize, Long.MAX_VALUE);
    }
    public static byte[] readBytes(InputStream is, int bufferSize,
                                   long maxBytes) throws BeyondMaxException, IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int count = 0;
        int n;
        while((n = is.read(buffer)) != -1) {
            os.write(buffer, 0, n);
            count += n;
            if(count > maxBytes) {
                throw new BeyondMaxException("The input stream's size " +
                    "exceeds the maximum allowed " + maxBytes + " bytes.");
            }
        }
        return os.toByteArray();
    }

    public static byte[] readBytes(File file) {
        try {
            Path path = Paths.get(file.toURI());
            return Files.readAllBytes(path);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static File sibling(File output, String name) {
        return new File(output.getParent(), name);
    }
    public static File similar(File output, String extra) {
        String name = FileUtil.getNameWithoutExtension(output);
        String ext = FileUtil.getExtension(output);
        if(ext == null) {
            return sibling(output, name + extra);
        }
        return sibling(output, name + extra + "." + ext);
    }

    public static File getParent(File path, String name) {
        return getParent(path, name, false);
    }
    public static File getParent(File path, String name, boolean caseSensitive) {
        while(path != null &&
                (caseSensitive && !path.getName().equals(name) ||
                !caseSensitive && !path.getName().equalsIgnoreCase(name))) {
            path = path.getParentFile();
        }
        return path;
    }
    public static File relativeTo(File relOrAbsPath, File relativeToDir) {
        if(relOrAbsPath.isAbsolute()) {
            return relOrAbsPath;
        }
        return new File(relativeToDir, relOrAbsPath.getPath());
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws IOException {
        Map<String, Long> parseSizes = new RLinkedHashMap<>(
//            "5B", 5L,
//            "5 B", 5L,
            "31.41 MB", 31410000L
        );
        for(String key : parseSizes.keySet()) {
            Long exp = parseSizes.get(key);
            Long act = parseByteSize(key);
            if(!exp.equals(act)) {
                System.err.println(key + " => " + act + " not " + exp);
            } else {
                System.out.println(key + " Passed");
            }
        }
        if(true) {
            return;
        }
        File baseFile = new File("a/b/c/rep.config");
        File baseDir = baseFile.getAbsoluteFile().getParentFile();
        System.out.println(baseDir);
        File other = new File("..\\somejar.jar");
        File changed = relativeTo(other, baseDir);
        System.out.println(changed);
        System.out.println(changed.getAbsolutePath());
        System.out.println(changed.getCanonicalPath());
        if(true) {
            return;
        }

        File parent = new File("C:\\Users\\dtrumbo\\Desktop");
        List<File> allFiles = find(parent);
//        List<File> allFiles = find(parent, new FileFilter() {
//            public boolean accept(File file) {
//                return
//                    file.isDirectory() ||      // Directory recurse rule (all directories)
//                    (file.isFile() && file.getName().endsWith(".ini"));   // File match rule
//            }
//        });
//        List<File> allFiles = find(parent, new FileFilter() {
//            public boolean accept(File file) {
//                return
//                    (file.isDirectory() && file.getName().equals("Browsers")) ||  // Directory recurse rule
//                    (file.isFile() && file.getName().endsWith(".ini"));           // File match rule
//            }
//        });
        for(File file : allFiles) {
            System.out.println(file);
        }
//        System.out.println(cleanForFileName("de@r#e%k&& (t)r_u+m+b+ -o", "a-e"));
    }

    public static void touch(File file) throws RuntimeConvertedException {
        long timestamp = System.currentTimeMillis();
        touch(file, timestamp);
    }

    public static void touch(File file, long timestamp) throws RuntimeConvertedException {
        try {
            if(!file.exists()) {
               new FileOutputStream(file).close();
            }
            file.setLastModified(timestamp);
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    public static File toFile(URL url) {
        if(url != null && url.getProtocol().equals("file")) {
            String s = url.toString();
            int colon = s.indexOf(':');
            return new File(s.substring(colon + 1));
        }
        return null;
    }
}