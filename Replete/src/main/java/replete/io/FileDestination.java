package replete.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileDestination implements Destination {
    private File file;
    private BufferedWriter writer;
    public FileDestination(File file) {
        this.file = file;
    }
    public void begin() throws Exception {
        writer = new BufferedWriter(new FileWriter(file));
    }
    public void end() throws Exception {
        writer.close();
    }
    @Override
    public void accept(String line) throws Exception {
        writer.write(line);
        writer.write('\n');
    }
}
