package replete.ui.fc;

import java.io.File;

public class ValidationProblem {
    private File file;
    private String message;
    public ValidationProblem(File file, String message) {
        this.file = file;
        this.message = message;
    }
    public File getFile() {
        return file;
    }
    public String getMessage() {
        return message;
    }
}
