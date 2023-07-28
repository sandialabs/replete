package replete.io.diff;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import replete.collections.Triple;
import replete.hash.Md5Util;
import replete.progress.FractionProgressMessage;
import replete.ttc.DefaultTransparentTaskContext;
import replete.ttc.TransparentTaskContext;
import replete.ttc.TtcUtil;

// Decided to create a separate utility class instead of
// placing this functionality into FileUtil.  It's perhaps
// a little more convenient here, but debatable.

public class DiffUtil {

    public static DiffResult diffDirs(File dir1, File dir2) {
        DirComparison comp = diffDirs(dir1, dir2, false, null, 0);
        return new DiffResult(dir1, dir2, comp);
    }
    public static DiffResult diffDirs(File dir1, File dir2, boolean ignoreCaseOk) {
        DirComparison comp = diffDirs(dir1, dir2, ignoreCaseOk, null, 0);
        return new DiffResult(dir1, dir2, comp);
    }
    public static DiffResult diffDirs(File dir1, File dir2, boolean ignoreCaseOk, DiffFileFilter filter) {
        DirComparison comp = diffDirs(dir1, dir2, ignoreCaseOk, filter, 0);
        return new DiffResult(dir1, dir2, comp);
    }

    private static DirComparison diffDirs(File dir1, File dir2, boolean ignoreCaseOk, DiffFileFilter filter, int level) {
        File[] paths1 = dir1.listFiles();
        File[] paths2 = dir2.listFiles();
        List<File> unmatchedPath2 = new ArrayList<>(Arrays.asList(paths2));

        DirComparison diff = new DirComparison();
        int p = 0;
        for(File path1 : paths1) {
            boolean isDir1  = path1.isDirectory();
            boolean found = false;
            for(File path2 : paths2) {
                boolean match   = path1.getName().equals(path2.getName());
                boolean matchIc = path1.getName().equalsIgnoreCase(path2.getName());
                boolean isDir2  = path2.isDirectory();

                if(!path1.isDirectory() && !path1.isFile()) {
                    throw new IllegalStateException();
                }
                if(!path2.isDirectory() && !path2.isFile()) {
                    throw new IllegalStateException();
                }

                if(match || ignoreCaseOk && matchIc) {
                    if(found) {
                        throw new IllegalStateException("Already matched this path: " + path1 + ".  Are you using ignoreCaseOk = true on Linux?");
                    }
                    found = true;
                    unmatchedPath2.remove(path2);

                    boolean leftAccept = filter == null || filter.accept(DiffDirection.LEFT, path1);
                    boolean rightAccept = filter == null || filter.accept(DiffDirection.RIGHT, path2);
                    if(!leftAccept || !rightAccept) {
                        break;
                    }

                    PathComparison pathDiff = new PathComparison(
                        match != matchIc,
                        isDir1 ? PathType.DIR : PathType.FILE,
                        isDir2 ? PathType.DIR : PathType.FILE
                    );
                    if(isDir1 && isDir2) {
                        DirComparison dirDiff = diffDirs(path1, path2, ignoreCaseOk, filter, level + 1);
                        pathDiff.setContentComparison(dirDiff);
                    } else if(!isDir1 && !isDir2) {
                        FileComparison fileDiff = diffFiles(path1, path2);
                        pathDiff.setContentComparison(fileDiff);
                    }
                    diff.getResults().add(
                        new Triple<>(path1, path2, pathDiff)
                    );
                }
            }

            if(!found) {
                if(filter == null || filter.accept(DiffDirection.LEFT, path1)) {
                    PathComparison pathDiff = new PathComparison(
                        false,
                        isDir1 ? PathType.DIR : PathType.FILE,
                        PathType.NONE
                    );
                    diff.getResults().add(
                        new Triple<>(path1, null, pathDiff)
                    );
                }
            }

            p++;

            TransparentTaskContext context = TtcUtil.getTtc();
            if(context != null) {
                context.publishProgress(new FractionProgressMessage("Checked: " + path1, p, paths1.length));
            }
        }

        for(File path2 : unmatchedPath2) {
            boolean isDir2 = path2.isDirectory();
            if(filter == null || filter.accept(DiffDirection.RIGHT, path2)) {
                PathComparison pathDiff = new PathComparison(
                    false,
                    PathType.NONE,
                    isDir2 ? PathType.DIR : PathType.FILE
                );
                diff.getResults().add(
                    new Triple<>(null, path2, pathDiff)
                );
            }
        }

        return diff;
    }

    public static FileComparison diffFiles(File file1, File file2) {
        FileComparison diff = new FileComparison();

        diff.setLeftLength(file1.length());
        diff.setRightLength(file2.length());

        // If the two files are the exact same size, then let's check
        // the bytes for equality.  Here we'll be lazy and just use
        // a canned MD5 hash check for now, even if this opens the
        // possibility for the mythical hash collision to enable
        // erroneous behavior.
        if(file1.length() == file2.length()) {

            // Error handling here is somewhat rushed but makes sure that
            // problems reading the file (say it's locked or missing somehow)
            // don't stop the overall diffing process, which might be a long
            // one, and still highlight the issue in the final results.
            // Obviously a full solution would involve not bastardizing the
            // MD5 field but rather store file/dir access errors in another
            // part of the final diff results object.  At the same time we
            // could review whether any other errors are possible (can they
            // happen when you try to list a directory's contents?).

            try {
                diff.setLeftMd5(Md5Util.getMd5(file1));
            } catch(Exception e) {
                diff.setLeftMd5("ERROR-LEFT[" + e + "]");
            }
            try {
                diff.setRightMd5(Md5Util.getMd5(file2));
            } catch(Exception e) {
                diff.setRightMd5("ERROR-RIGHT[" + e + "]");
            }
        }
        return diff;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        File d1 = new File("C:\\Users\\dtrumbo\\Desktop\\A\\jackson-core-asl-1.9.9");
//        File d2 = new File("C:\\Users\\dtrumbo\\Desktop\\A\\jackson-all-1.9.9");
//        File d1 = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\Avondale\\target\\avondale-3.5.0-SNAPSHOT");
//        File d2 = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\AvondaleBundler\\build\\deploy\\avondale-3.5.0");
//        File d1 = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\AvondaleBundler\\target\\avondale-bundler-3.5.0-SNAPSHOT");
//        File d2 = new File("C:\\Users\\dtrumbo\\Desktop\\avondale-3.5.0-SNAPSHOT");
//        File d1 = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\AvondaleBundler\\target\\avondale-bundler-3.5.0-SNAPSHOT\\bin\\lib\\replete-1.1.0-SNAPSHOT");
//        File d2 = new File("C:\\Users\\dtrumbo\\Desktop\\avondale-3.5.0-SNAPSHOT\\bin\\lib\\replete-1.1.0-SNAPSHOT");
        File d1 = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\AvondaleBundler\\target\\avondale-bundler-3.5.0-SNAPSHOT");
        File d2 = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\AvondaleBundlerMaven\\target\\avondale-bundler-3.5.0-SNAPSHOT");

        TransparentTaskContext ttc = new DefaultTransparentTaskContext();
        ttc.addProgressListener(e -> {
            System.out.println(e.getMessage());
        });
        TtcUtil.addTtc(ttc);

        DiffResult result = DiffUtil.diffDirs(d1, d2, false, null);
        DirComparison diff = result.getComparison();

        ComparisonRenderOptions options = new ComparisonRenderOptions()
            .setIncludeSame(false)
            .setSortType(SortType.ALPHA_IC)
            .setLeftLabel("ABM")
            .setRightLabel("AV")
        ;
        String actual = diff.toString(0, options);
        System.out.println(d1 + " (Left) vs. " + d2 + " (Right) " + diff.getDiffCountLocal() + " Differences (" + diff.getDiffCountGlobal() + " Total)");
        if(!diff.isDiff()) {
            System.out.println("<<< Identical >>>");
        }
        System.out.println(actual);
    }
}
