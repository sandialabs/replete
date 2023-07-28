package replete.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import replete.errors.RuntimeConvertedException;
import replete.text.NewlineType;

public class FileUtilTest {

    @Test
    public void testGetTextContent() {

        // Test file in Windows format.
        URL url = FileUtilTest.class.getResource("file_util_test.txt");

        try {

            String s = FileUtil.getTextContent(new File(url.toURI()));
            assertEquals("test\nfile\nhere\n", s);

            s = FileUtil.getTextContent(new File(url.toURI()), true);
            assertEquals("test\r\nfile\r\nhere\r\n", s);

            s = FileUtil.getTextContent(new File(url.toURI()), false, NewlineType.CR);
            assertEquals("test\rfile\rhere\r", s);

        } catch(URISyntaxException e) {
            fail();
        }

        try {
            FileUtil.getTextContent((File) null);
            fail();
        } catch(Exception e) {
            assertEquals(RuntimeConvertedException.class, e.getClass());
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }

        try {
            FileUtil.getTextContent(new File("non-existent.txt"));
            fail();
        } catch(Exception e) {
            assertEquals(RuntimeConvertedException.class, e.getClass());
            assertEquals(FileNotFoundException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testGetTextLineCount() {
        URL url = FileUtilTest.class.getResource("file_util_test.txt");
        try {

            int count = FileUtil.getTextLineCount(new File(url.toURI()));
            assertEquals(3, count);

        } catch(URISyntaxException e) {
            fail();
        }
    }

    // Not complete - just made to test initial implementation.
    @Test
    public void testWriteTextContent() {
        try {
            URL url = FileUtilTest.class.getResource(".");
            File f = new File(url.toURI());
            File f2 = new File(f, "write.txt");
            FileUtil.writeTextContent(f2, "test");
        } catch(Exception e) {
            fail();
        }
    }
}
