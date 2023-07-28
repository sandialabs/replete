package replete.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import replete.util.User;
import replete.xstream.XStreamWrapper;

public class SzUtil {
    public static void test(Object d) throws Exception {
        test(d, User.getDesktop());
    }
    public static void test(Object d, File dir) throws Exception {
        XStreamWrapper.writeToFile(d, new File(dir, "sztest-before.xml"));
        File bin = new File(dir, "sztest.bin");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(bin));
        oos.writeObject(d);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(bin));
        d = ois.readObject();
        XStreamWrapper.writeToFile(d, new File(dir, "sztest-after.xml"));
    }
}
