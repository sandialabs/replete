package replete.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Random;

import replete.io.FileUtil;
import replete.text.StringUtil;


/**
 * Creates a temporary directory that is specific to the
 * current user and this specific instance of the application.
 *
 * When initialize is called a directory is created under
 * [tmpdir]/[app]/[username]/[session].  The value for
 * [tmpdir] is whatever path the JVM has designated as the
 * user's temporary directory, which varies from OS to OS.
 * This is the value from the java.io.tmpdir system property.
 *
 * The user name is included in the temporary directory
 * path structure because on some operating systems, all
 * users share the same temporary directory.  So in such
 * a case this design will be safe regardless of which
 * operating system this code is running on.  If desired,
 * one could remove this additional directory level on
 * certain operating systems (e.g. Windows) with minor
 * modifications.
 *
 * The value for [app] is the value passed to the initialize
 * method.  initialize will not successfully complete if this
 * value is not a valid string.  The value for [session] is
 * the hexadecimal value of System.currentTimeMillis(), called
 * when initialize is invoked.
 *
 * Inside this temporary directory a special file called
 * keepAlive is used to determine whether or not a given
 * application instance is still executing.  This manager
 * class will keep a lock on that file for the entire
 * duration of the program.
 *
 * Before initialize creates this instance's own temporary
 * directory, it scans the [tmpdir]/[app]/[username]
 * directory for any orphaned session directories.  These
 * are temporary directories that were once assigned to
 * a running instance of the application, but which are now
 * orphaned because that instance was not shut down correctly.
 * If this instance can get a lock on the keepAlive file
 * in another session's temporary directory then that
 * directory is considered an orphan and is deleted by this
 * instance.
 *
 * The shutdown method properly releases the lock to the
 * keepAlive file and deletes the instance's temporary
 * directory.
 *
 * @author Derek Trumbo
 */

public class TempDirectoryManager {

    ////////////
    // FIELDS //
    ////////////

    private static final String keepAliveFileName = ".keepAlive";

    private static File tmpDir = null;
    private static FileLock keepAliveFileLock = null;
    private static FileOutputStream keepAliveOutputStream = null;

    // Returns the temporary directory assigned to this
    // instance of the application, or null if the
    // directory has not yet been initialized.
    public static File getTempDir() {
        return tmpDir;
    }

    // For testing purposes only.  The temporary directory
    // should be set with initialize in the normal case.
    public static void setTempDir(File newTempDir) {
        tmpDir = newTempDir;
    }

    // If a TempDirectoryManager operation (initialize or
    // shutdown) returns false, then client code can
    // query this field for details.
    private static String errMsg;
    public static String getErrorMessage() {
        return errMsg;
    }

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    private TempDirectoryManager() {}

    ////////////////////
    // INITIALIZATION //
    ////////////////////

    public static boolean initialize(String appName) {

        // Cannot initialize unless the program's name is given.
        if(appName == null || appName.equals("")) {
            errMsg = "The application name is not defined";
            return false;
        }

        errMsg = "";

        // No need to initialize if the temp directory has
        // already been set up for this session.
        if(tmpDir != null) {
            return true;
        }

        // The user's own temp directory for the application.
        File tempAppUserDir = new File(
            System.getProperty("java.io.tmpdir"),
            appName + File.separator + System.getProperty("user.name"));

        // Remove stale temporary directories.
        boolean success = cleanUpUsersStaleTempDirs(tempAppUserDir);

        // The user's temp directory for the session.
        // Loop in the improbable chance that the directory
        // exists for some reason.
        File tempUserSessionDir;
        do {
            String appSessionId = Long.toHexString(System.currentTimeMillis());
            tempUserSessionDir = new File(tempAppUserDir, appSessionId + "_Session");
        } while(tempUserSessionDir.exists());

        // Create this session's temporary directory.
        tempUserSessionDir.mkdirs();
        if(!tempUserSessionDir.exists()) {
            errMsg += "Could not create temporary directory:\n" +
                tempUserSessionDir.getAbsolutePath() + "\n";
            return false;
        }

        // Create the keepAlive file.
        success = success && createAndLockKeepAliveFile(tempUserSessionDir);

        // If everything went correctly, set the global value
        // of the temporary directory.
        if(success) {
            tmpDir = tempUserSessionDir;
        }

        return success;
    }

    private static boolean cleanUpUsersStaleTempDirs(File tempAppUserDir) {
        boolean success = true;

        if(tempAppUserDir.exists()) {
            if(tempAppUserDir.isDirectory()) {
                File[] tempUserSessionDirs = tempAppUserDir.listFiles();

                for(File tempUserSessionDir : tempUserSessionDirs) {

                    File keepAliveFile = new File(tempUserSessionDir, keepAliveFileName);

                    boolean deleteTempUserSessionDir = false;

                    if(!keepAliveFile.exists()) {
                        deleteTempUserSessionDir = true;
                    } else {

                        boolean tempDirIsLocked = false;
                        FileOutputStream tempOutputStream = null;

                        try {
                            tempOutputStream = new FileOutputStream(keepAliveFile);
                            FileLock tempFileLock = tempOutputStream.getChannel().tryLock();
                            if(tempFileLock == null) {
                                tempDirIsLocked = true;
                            } else {
                                tempDirIsLocked = false;
                            }
                        } catch(OverlappingFileLockException ioe) {
                            tempDirIsLocked = true;
                        } catch(ClosedChannelException cce) {
                            tempDirIsLocked = false;
                        } catch(FileNotFoundException fnfe) {
                            tempDirIsLocked = false;
                        } catch(IOException ioe) {
                            errMsg +=
                                "Got an IOException when testing lock on: " +
                                keepAliveFile.getAbsolutePath() + "\n";
                            tempDirIsLocked = false;
                            success = false;
                        } finally {
                            try {
                                if(tempOutputStream != null) {
                                    tempOutputStream.getChannel().close();
                                    tempOutputStream.close();
                                }
                            } catch(IOException ioe) {
                                errMsg +=
                                    "Cannot close tempOutputSream on: " +
                                    keepAliveFile.getAbsolutePath() + "\n";
                                success = false;
                            }
                        }

                        if(!tempDirIsLocked) {
                            deleteTempUserSessionDir = true;
                        }
                    }

                    if(deleteTempUserSessionDir) {
                        FileUtil.deleteDirectory(tempUserSessionDir);
                    }

                } // for each child directory in the users temp directory

            } else {
                errMsg +=
                    "Cannot access temporary directory, " + tempAppUserDir +
                    ", because it already exists as a non-directory.\n";
                success = false;
            }
        }

        return success;
    }

    private static boolean createAndLockKeepAliveFile(File tempUserSessionDir) {
        File keepAliveFile = new File(tempUserSessionDir, keepAliveFileName);

        try {
            keepAliveOutputStream = new FileOutputStream(keepAliveFile);
            keepAliveFileLock = keepAliveOutputStream.getChannel().lock();
            return true;
        } catch(FileNotFoundException fnfe) {
            errMsg +=
                "Cannot create the keepAlive file to lock:\n" +
                keepAliveFile.getAbsolutePath() + "\n";
        } catch(IOException ioe) {
            errMsg +=
                "Unable to lock the keepAlive file:\n" +
                ioe.getMessage() + "\n" +
                keepAliveFile.getAbsolutePath() + "\n";
        } catch(Exception e) {
            errMsg +=
                "Error locking the keepAlive file:\n" +
                e.getMessage() + "\n" +
                keepAliveFile.getAbsolutePath() + "\n";
        }

        // Error cases
        keepAliveFileLock = null;
        keepAliveOutputStream = null;
        return false;
    }

    //////////////
    // SHUTDOWN //
    //////////////

    // If this method returns true (success), the
    // tmpDir variable will be null.
    public static boolean shutdown() {

        errMsg = "";

        if(tmpDir != null) {
            boolean success = releaseTempDirectoryLock();
            if(success && FileUtil.deleteDirectory(tmpDir)) {
                tmpDir = null;
            } else {
                errMsg +=
                    "The temporary directory could not be removed:\n" +
                    tmpDir.getAbsolutePath() + "\n";
                return false;
            }
        }

        return true;
    }

    private static boolean releaseTempDirectoryLock() {

        try {

            keepAliveFileLock.release();
            keepAliveOutputStream.getChannel().close();
            keepAliveOutputStream.close();

            keepAliveFileLock = null;
            keepAliveOutputStream = null;

            return true;

        } catch(Exception e) {

            errMsg +=
                "Unable to release lock on keepAlive file:\n" +
                e.getMessage() + "\n";

            return false;
        }
    }

    ///////////
    // OTHER //
    ///////////

    public static File createTempSubdir() {
        return createTempSubdir(null);
    }
    public static File createTempSubdir(String tag) {
        if(tmpDir == null) {
            return null;
        }

        File tempSubdir;
        do {
            int randVal = new Random().nextInt();
            String subdirName = StringUtil.padLeft(Integer.toHexString(randVal), '0', 8);

            // A tag can be used for debugging purposes to more
            // quickly locate a temporary subdirectory of interest.
            if(tag != null) {
                subdirName += "_" + tag;
            }

            tempSubdir = new File(tmpDir, subdirName);
        } while(tempSubdir.exists());

        tempSubdir.mkdir();

        return tempSubdir;
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        System.out.println("Initialize Success? " + initialize("TempDirMgrTest"));
        createTempSubdir();
        createTempSubdir("Test");
        System.out.println("Shutdown Success? " + shutdown());
    }
}
