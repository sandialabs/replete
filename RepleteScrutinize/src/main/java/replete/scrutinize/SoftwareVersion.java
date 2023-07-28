package replete.scrutinize;

public class SoftwareVersion extends replete.version.SoftwareVersion {


    ///////////////
    // SINGLETON //
    ///////////////

    // All configuration of this object is handled by base class constructor.
    private static SoftwareVersion instance = new SoftwareVersion();

    public static SoftwareVersion get() {
        return instance;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        instance.print();
    }
}
