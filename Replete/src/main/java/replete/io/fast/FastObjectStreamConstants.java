package replete.io.fast;

public interface FastObjectStreamConstants {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static final byte TYPE_NULL          =  0;
    public static final byte TYPE_BOOL_TRUE     =  1;
    public static final byte TYPE_BOOL_FALSE    =  2;
    public static final byte TYPE_BYTE          =  3;
    public static final byte TYPE_SHORT         =  4;
    public static final byte TYPE_INTEGER       =  5;
    public static final byte TYPE_LONG          =  6;
    public static final byte TYPE_FLOAT         =  7;
    public static final byte TYPE_DOUBLE        =  8;
    public static final byte TYPE_CHARACTER     =  9;
    public static final byte TYPE_STRING1       = 10;
    public static final byte TYPE_STRING2       = 11;
    public static final byte TYPE_STRING4       = 12;
    public static final byte TYPE_OBJECT        = 13;
    public static final byte TYPE_OBJECT_CX     = 14;
    public static final byte TYPE_OBJECT_REF    = 15;
    public static final byte TYPE_ARRAY         = 16;
    public static final byte TYPE_ARRAY_CX      = 17;
    public static final byte TYPE_ARRAY_PRIM    = 18;
    public static final byte TYPE_ARRAY_PRIM_CX = 19;

    public static final int SHORT_CHAR_BYTES    = 2;
    public static final int INTEGER_FLOAT_BYTES = 4;
    public static final int LONG_DOUBLE_BYTES   = 8;

    public static final int MAX_BUFFER_BLOCK_SIZE = 2048;

    public static final int MAX_UNSIGNED_BYTE_VALUE  =   255;
    public static final int MAX_UNSIGNED_SHORT_VALUE = 65535;

    public static final String STRING_ENCODING = "UTF8";
}
