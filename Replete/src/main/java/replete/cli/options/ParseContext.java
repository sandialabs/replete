package replete.cli.options;

import java.io.File;

// The design and usage of this interface is admittedly a little rushed
// and is only currently used to allow the PathOption to know whether
// it should convert the paths its inspecting before its applies its
// validation checks.

public interface ParseContext {
    boolean isConvertRelPaths();
    File getArgsFile();
    File convertPath(File path, File argsFile);
}
