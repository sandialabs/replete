package replete.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Derek Trumbo
 */

public class ConsoleUtil {

    public static String getLine() {
        return getLine(null);
    }
    public static String getLine(String prompt) {
        if(prompt != null) {
            System.out.print(prompt);
        }
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            return in.readLine();
        } catch(IOException e) {
            return null;
        }
    }

    private static PrintStream saved;
    public static void disable() {
        saved = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                //DO NOTHING
            }
        }));
    }
    public static void restore() {
        if(saved != null) {
            System.setOut(saved);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
       String s = getLine();
       System.out.println(s);
    }
}
