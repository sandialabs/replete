package replete.hash;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Md5HashFunction implements HashFunction<String> {

    public long hash(String item) {
        byte[] md5 = calculateMD5Hash(item);
        ByteBuffer buffer = ByteBuffer.wrap(md5);
        return buffer.getLong();
    }

    public static byte[] calculateMD5Hash(String item) {
        byte[] bytes = null;
        try {
            bytes = item.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Md5HashFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] md5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md5 = md.digest(bytes);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Md5HashFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return md5;
    }

    public static String calculateMD5HashString(String item) {
        byte[] bytes = calculateMD5Hash(item);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }
}
