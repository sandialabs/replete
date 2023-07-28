package replete.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
    public static byte[] readAllBytes(InputStream is) throws IOException {
        if(is instanceof ByteArrayInputStream) {
            int size = is.available();
            byte[] buf = new byte[size];
            is.read(buf, 0, size);
            return buf;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int size = 1024;
        byte[] buf = new byte[size];
        int len;
        while((len = is.read(buf, 0, size)) != -1) {
            bos.write(buf, 0, len);
        }
        return bos.toByteArray();
    }
}
