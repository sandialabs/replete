package replete.io.diff;

import java.io.File;

public class DiffResult {


    ////////////
    // FIELDS //
    ////////////

    private File leftPath;
    private File rightPath;
    private DirComparison comparison;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DiffResult(File leftPath, File rightPath, DirComparison comparison) {
        this.leftPath = leftPath;
        this.rightPath = rightPath;
        this.comparison = comparison;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public File getLeftPath() {
        return leftPath;
    }
    public File getRightPath() {
        return rightPath;
    }
    public DirComparison getComparison() {
        return comparison;
    }
}
