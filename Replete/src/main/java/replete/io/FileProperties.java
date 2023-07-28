package replete.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/**
 * @author Derek Trumbo
 */

public class FileProperties extends Properties {
    protected File file;

    public FileProperties(File f) {
        file = f;
        load();
    }

    protected void load() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            load(reader);
        } catch(Exception e) {

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(Exception e) {

                }
            }
        }
    }

    public void save() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            store(writer, null);
        } catch(Exception e) {

        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch(Exception e) {

                }
            }
        }
    }
}
