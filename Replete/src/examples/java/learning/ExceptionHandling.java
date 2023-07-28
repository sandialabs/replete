package learning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ExceptionHandling {

    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\Replete\\LICENSE");
//        AbstractPath myPath = new AbstractPath("C:\\mybutt");
//        writeContentTo(myPath);

        String s = readOldSchool(file);
        System.out.println(s);

        s = readWithTryWithResource(file);
        System.out.println(s);
    }

    private static String readOldSchool(File file) {
        BufferedReader reader = null;
        StringBuilder all = new StringBuilder();
        try {

            //String endings = CoolFileStringUtil.detectFileEnding(file);
            reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null) {
                all.append(line);
                all.append('\n');   // Using LF for consistency's sake BUT this may not have been the original file's line endings
                //all.append(endings);
            }

//        } catch(FileNotFoundException e) {
//            // deal with FNFE
//
//        } catch(IOException e) {
//            // deal with IOE - Just not the FNFE's dealt with above

        } catch(Exception e) {
            // deal with anything else that's not an IOE

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    // don't care
                }
            }
        }

        return all.toString();
    }

    public static String readWithTryWithResource(File file) {

//        for(String line : lines) {  // foreach or "enhanced forloop"
//
//        }
//        for(Object elem : lines.iterator()) {         interface Iterable { Iterator iterator(); }
//
//        }

        StringBuilder all = new StringBuilder();
        try(BufferedReader reader2 = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader2.readLine()) != null) {
                all.append(line);
                all.append('\n');   // Using LF for consistency's sake BUT this may not have been the original file's line endings
            }
        } catch(Exception e) {
            // Handle error
        }

        return all.toString();
    }

}
