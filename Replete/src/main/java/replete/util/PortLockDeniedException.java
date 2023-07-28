package replete.util;

import java.io.File;

public class PortLockDeniedException extends RuntimeException {
    public PortLockDeniedException(String reason, File lockFile) {
        super(reason + " (" + lockFile + ")");
    }
    public PortLockDeniedException(String reason, File lockFile, Throwable cause) {
        super(reason + " (" + lockFile + ")", cause);
    }
}
