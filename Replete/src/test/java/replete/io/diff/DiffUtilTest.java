package replete.io.diff;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import replete.io.FileUtil;
import replete.ttc.DefaultTransparentTaskContext;
import replete.ttc.TransparentTaskContext;
import replete.ttc.TtcUtil;
import replete.util.CodeUtil;

public class DiffUtilTest {

    @Test
    public void local1() {
        File d1 = new File(CodeUtil.getCodeSourcePath(), "replete/io/diff/test/d1");
        File d2 = new File(CodeUtil.getCodeSourcePath(), "replete/io/diff/test/d2");

        DiffFileFilter filter = (direction, path) -> {
            return !path.getAbsolutePath().contains("ignore");
        };

        DiffResult result = DiffUtil.diffDirs(d1, d2, true, filter);
        DirComparison diff = result.getComparison();

        ComparisonRenderOptions options = new ComparisonRenderOptions();
        String actual = diff.toString(0, options);
        String expected = FileUtil.getTextContent(DiffUtilTest.class.getResourceAsStream("expected-local-diff.txt"), true);
//        FileUtil.writeTextContent(new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\Replete\\src\\test\\java\\replete\\io\\diff\\actual-local1.txt"), actual);
        assertEquals(expected, actual);
    }

    @Test
    public void local2() {
        File d1 = new File(CodeUtil.getCodeSourcePath(), "replete/io/diff/test/d1");
        File d2 = new File(CodeUtil.getCodeSourcePath(), "replete/io/diff/test/d2");

        DiffFileFilter filter = (direction, path) -> {
            return !path.getAbsolutePath().contains("ignore");
        };

        DiffResult result = DiffUtil.diffDirs(d1, d2, true, filter);
        DirComparison diff = result.getComparison();

        ComparisonRenderOptions options = new ComparisonRenderOptions()
            .setIncludeSame(true)
            .setSortType(SortType.ALPHA)
        ;
        String actual2 = diff.toString(0, options);
        String expected2 = FileUtil.getTextContent(DiffUtilTest.class.getResourceAsStream("expected-local-diff-w-same.txt"), true);
        //FileUtil.writeTextContent(new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\Replete\\test\\replete\\io\\diff\\actual-local2.txt"), actual2);
        assertEquals(expected2, actual2);
    }

    // Can be used to test a larger diff, but not included in regular unit test runs
    @Test
    @Ignore
    public void ws() {
        File d1 = new File("C:\\Users\\dtrumbo\\work\\eclipse-main");
        File d2 = new File("C:\\Users\\dtrumbo\\work\\eclipse-alt");

        TransparentTaskContext context = new DefaultTransparentTaskContext();
        context.addProgressListener(e -> System.out.println(e.getMessage()));
        TtcUtil.addTtc(context);

        DiffFileFilter filter = (direction, path) -> {
            return
                !path.getAbsolutePath().contains("\\.metadata\\") &&
                !path.getAbsolutePath().contains("\\.recommenders\\") &&
                !path.getAbsolutePath().contains("\\.settings\\") &&
                !path.getAbsolutePath().contains("\\.svn\\") &&
                !path.getAbsolutePath().contains("\\bin\\") &&
                !path.getAbsolutePath().contains("\\target\\") &&
                !path.getAbsolutePath().contains("\\build\\");
        };

        DiffResult result = DiffUtil.diffDirs(d1, d2, true, filter);
        DirComparison diff = result.getComparison();

        ComparisonRenderOptions options = new ComparisonRenderOptions()
            .setIncludeSame(true)
            .setSortType(SortType.ALPHA)
        ;
        System.out.println("Diffing " + d1 + " and " + d2 + ":");
        System.out.println(diff.toString(0, options));
    }

}
