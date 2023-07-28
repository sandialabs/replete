package replete.ui.sdplus.demo;

/**
 * Demo data scale describing a dimension of the data.
 *
 * @author Derek Trumbo
 */

public class DataScale {
    public String key;
    public String name;
    public String units;
    public String note;

    public DataScale(String k, String nm, String un, String nt) {
        key = k;
        name = nm;
        units = un;
        note = nt;
    }

    @Override
    public boolean equals(Object sc) {
        return key.equals(((DataScale)sc).key);
    }
}