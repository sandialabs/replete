package finio.ui.bytes;

public class ByteStream {

    ////////////
    // FIELDS //
    ////////////

    public static final byte[] DEFAULT_DATA   = new byte[0];
    public static final Source DEFAULT_SOURCE = null;

    private byte[] data   = DEFAULT_DATA;
    private Source source = DEFAULT_SOURCE;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ByteStream() {
    }
    public ByteStream(byte[] data, Source source) {
        this.data = data;
        this.source = source;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public byte[] getData() {
        return data;
    }
    public Source getSource() {
        return source;
    }

    // Mutators
}
