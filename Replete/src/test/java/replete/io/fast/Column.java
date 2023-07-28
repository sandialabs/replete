package replete.io.fast;

import org.junit.Ignore;


@Ignore
public class Column {


    ////////////
    // FIELDS //
    ////////////

    public String header;
    public char code;
    public boolean left;
    public int width;       // Calculated later


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Column(String header, char code, boolean left) {
        this.header = header;
        this.code = code;
        this.left = left;
    }
}
