package replete.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

import replete.hash.Md5Util;
import replete.io.FileUtil;

public class PortLockManager {


    //////////////
    // CLEAN UP //
    //////////////

    static {
        File lockDir = makeLockDir();
        for(File f : lockDir.listFiles()) {
            f.delete();
        }
    }


    ////////////
    // FIELDS //
    ////////////

    private static Map<String, File> files = new HashMap<>();
    private static Map<String, FileOutputStream> streams = new HashMap<>();
    private static Map<String, FileLock> locks = new HashMap<>();


    //////////
    // LOCK //
    //////////

    public static synchronized String lock(int port, String actorName) throws PortLockDeniedException {

        File lockFile = makeLockFile(port, actorName);

        // Make sure the lock file exists for this port.
        if(!lockFile.exists()) {
            try {
                lockFile.createNewFile();
            } catch(IOException e) {
                throw new PortLockDeniedException("Could not create lock file", lockFile);
            }
        }

        // Attempt to get the lock for this port & actor name.
        String key = lock(lockFile);
        return key;
    }

    public static String lock(File lockFile) {
        FileOutputStream stream = null;
        FileLock lock = null;
        try {
            stream = new FileOutputStream(lockFile);
            lock = stream.getChannel().tryLock();
            if(lock == null) {
                throw new PortLockDeniedException("Could not obtain lock", lockFile);
            }
            String key = Md5Util.getMd5((System.currentTimeMillis() + lockFile.getAbsolutePath()).getBytes());
            files.put(key, lockFile);
            streams.put(key, stream);
            locks.put(key, lock);
            return key;
        } catch(Exception e) {
            throw new PortLockDeniedException("An error occurred securing the lock", lockFile, e);

        } finally {

            // If somehow we opened the stream but couldn't get the lock,
            // close the stream (don't think this is possible).
            if(stream != null && lock == null) {
                try {
                    stream.close();
                } catch(Exception e) {}
            }
        }
    }


    ////////////
    // UNLOCK //
    ////////////

    public synchronized static void free(String key) {

        File lockFile = files.get(key);

        if(lockFile == null || !lockFile.exists()) {
            return;
        }

        // Get the relevant objects.
        FileOutputStream stream = streams.get(key);
        FileLock lock = locks.get(key);

        try {
            lock.release();
            stream.getChannel().close();
            stream.close();

            files.remove(key);
            streams.remove(key);
            locks.remove(key);

            // Attempt to delete file.  Note: this is not required for
            // the locking class to work properly, only as a convenience
            // so you can look into the directory and see which remote
            // actors are "active".
            lockFile.delete();

        } catch(Exception e) {
            throw new PortLockDeniedException("An error occurred releasing the lock", lockFile, e);
        }
    }


    //////////
    // MISC //
    //////////

    private static File makeLockFile(int port, String actorName) {

        // Make sure the locks directory exists.
        File lockDir = makeLockDir();

        // Construct the name of the lock file from the port and actor name.
        String fileName = createFileName(port, actorName);

        // Construct the file object.
        File lockFile = new File(lockDir, fileName);

        return lockFile;
    }

    private static File makeLockDir() {
        File lockDir = new File(getAppResourceDir(), "locks");
        lockDir.mkdirs();
        return lockDir;
    }

    // How merge with Crawler/Main.getAppResourceDir?
    private static File getAppResourceDir() {
        return new File(User.getHome(), ".avcluster");
    }

    private static String createFileName(int port, String actorName) {
        actorName = FileUtil.cleanForFileName(actorName, " -");
        return "port-" + port + "-name-" + actorName + ".lock";
    }
}
