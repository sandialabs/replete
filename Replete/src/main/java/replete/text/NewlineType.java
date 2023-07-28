package replete.text;

/**
 * @author Derek Trumbo
 */

public enum NewlineType {
    LF("\n"),       // Unix
    CR("\r"),       // Mac <= 9.x
    CRLF("\r\n"),   // Windows
    AUTO(null),     // Though not a type itself, can be used to indicate
                    // that the source newline type should be used for
                    // the given operation, instead of a specific type.
    MIXED(null),    // Though not a type itself, can be used to indicate
                    // a given string or file has multiple newline types.
    NONE(null);     // Though not a type itself, can be used to indicate
                    // a given string of file does not have any newlines
                    // within.

    public final String image;
    NewlineType(String im) {
        image = im;
    }
}
