package finio.ui.bytes;

import java.io.File;

public class FileSource extends Source {
    File file;
    public FileSource(File file) {
        super();
        this.file = file;
    }
    @Override
    public String toString() {
        return "From File: " + file;
    }
}