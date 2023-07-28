package replete.io.diff;

import java.io.File;

public interface DiffFileFilter {
    boolean accept(DiffDirection direction, File path);
}
