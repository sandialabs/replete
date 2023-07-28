package gov.sandia.avondale.buildutils.licenses;

public class LicenseGroup {


    ////////////
    // FIELDS //
    ////////////

    private String longName;
    private String shortName;
    private String spdxId;
    private String[] generalLinks;
    private String[] patterns;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getLongName() {
        return longName;
    }
    public String getShortName() {
        return shortName;
    }
    public String getSpdxId() {
        return spdxId;
    }
    public String[] getGeneralLinks() {
        return generalLinks;
    }

    // Mutators

    public LicenseGroup setLongName(String longName) {
        this.longName = longName;
        return this;
    }
    public LicenseGroup setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }
    public LicenseGroup setSpdxId(String spdxId) {
        this.spdxId = spdxId;
        return this;
    }
    public LicenseGroup setGeneralLinks(String[] generalLinks) {
        this.generalLinks = generalLinks;
        return this;
    }
}
