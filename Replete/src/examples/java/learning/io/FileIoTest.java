package learning.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileIoTest {

    public static void main(String[] args) {
        File file = new File("abstract path");
        checkPath(file);
        File file2 = new File("C:\\Users\\dtrumbo\\work\\eclipse-3\\Replete\\src\\examples\\java\\learning\\test.txt");
        checkPath(file2);

        try {
            FileReader fReader = new FileReader(file2);

//            String s = "";
//            for(int i = 0; i < file2.length(); i++) {
//                s += fReader.read();
//            }
//
//            fReader.read();
//            fReader.read();
//            fReader.read();
//            fReader.read();
//            fReader.read();
//            fReader.read();
//            fReader.read();
//            fReader.read();

//            char[] cBuf = new char[4096];
//            fReader.read(cBuf);  //well, how big is cBuf?

            BufferedReader bReader = new BufferedReader(new FileReader(file2));
            String line;
//            String line = bReader.readLine();
//            String line = bReader.readLine();
//            String line = bReader.readLine();
//            String line = bReader.readLine();
//            String line = bReader.readLine();
//            String line = bReader.readLine();
//            String line = bReader.readLine();
//            String line = bReader.readLine();
            while((line = bReader.readLine()) != null) {

//                allLines.add(line);
//                line.substring(0, 2);
//                Pattern.matches(line, line)
                // process line

            }

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkPath(File file) {
        System.out.println(file);
        System.out.println("  absolute = " + file.getAbsolutePath());
        System.out.println("  exists = " + file.exists());
        System.out.println("  isDirectory = " + file.isDirectory());
        System.out.println("  isFile = " + file.isFile());
        System.out.println("  length = " + file.length());
        System.out.println("  modified = " + file.lastModified());
        System.out.println("  absolute = " + file.isAbsolute());
        System.out.println();
    }
}

// cd C:\Users\dtrumbo\work\eclipse-X\Replete
// prompt> java -classpath .... FileIoTest [args]
