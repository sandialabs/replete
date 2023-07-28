package replete.ui.fc;

import java.io.File;

public interface RFileChooserValidator {
    public ValidationProblem[] validate(
        RFileChooser chooser, int dialogType, File[] files);
}
