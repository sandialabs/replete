package replete.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.SortedMap;

import replete.util.User;

// http://www.java2s.com/Code/Java/I18N/Howtoautodetectafilesencoding.htm

public class CharsetDetector {

    public static Charset detectCharset(byte[] bytes, String[] charsets) {
        Charset charset = null;
        for(String charsetName : charsets) {
            if(detectCharset(bytes, charset = Charset.forName(charsetName))) {
                break;
            }
        }
        return charset;
    }

    private static boolean detectCharset(byte[] bytes, Charset charset) {
        try(BufferedInputStream input = new BufferedInputStream(new ByteArrayInputStream(bytes))) {
            CharsetDecoder decoder = charset.newDecoder();
            decoder.reset();
            byte[] buffer = new byte[512];
            boolean identified = false;
            while((input.read(buffer) != -1) && (!identified)) {
                identified = identify(buffer, decoder);
            }
            return identified;
        } catch(Exception e) {
            return false;
        }
    }

    private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
        try {
            decoder.decode(ByteBuffer.wrap(bytes));
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        SortedMap<String, Charset> ch = Charset.availableCharsets();
        String s = "Hello World!";
//        byte[] bytes = s.getBytes("UTF-8");
        byte[] bytes = FileUtil.readBytes(User.getDesktop("cs2.html"));
        for(String k : ch.keySet()) {
            Charset cs = ch.get(k);
            if(detectCharset(bytes, cs)) {
                System.out.println("matches: " + k);
            } else {
                System.out.println("doesn't match: " + k);
            }
//            String n = cs.name();
//            String d = cs.displayName();
////            if(!k.equals(n) || !n.equals(d)) {
////                cs.
////            }
//            System.out.println(k + " = " + n + " / " + d + " " + StringUtil.yesNo(cs.isRegistered()));
//            System.out.println(cs.aliases());
        }
    }

    public static void mainx(String[] args) {
        File f = new File("example.txt");

        String[] charsetsToBeTested = {"UTF-8", "windows-1253", "ISO-8859-7"};

        CharsetDetector cd = new CharsetDetector();
        Charset charset = cd.detectCharset(null, charsetsToBeTested);

        if (charset != null) {
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(f), charset);
                int c = 0;
                while ((c = reader.read()) != -1) {
                    System.out.print((char)c);
                }
                reader.close();
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }

        }else{
            System.out.println("Unrecognized charset.");
        }
    }
}
