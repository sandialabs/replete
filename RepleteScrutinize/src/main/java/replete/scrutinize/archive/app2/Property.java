package replete.scrutinize.archive.app2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class Property implements Serializable{


    ////////////
    // FIELDS //
    ////////////

    private String name;
    private String prettyValStr;
    private String rawValStr;
    private transient Object rawVal;
    private String code;
    private ValueType type;
    private String units;

    public List<?> asList() {
        if(rawVal instanceof List<?>) {
            return (List<?>) rawVal;
        } else if(rawVal.getClass().isArray()) {
            return Arrays.asList((Object[]) rawVal);
        } else if(type == ValueType.PATH_LIST) {
            return Arrays.asList(prettyValStr.split(":"));
        }
        List<Object> oneList = new ArrayList<Object>();
        oneList.add(rawVal);
        return oneList;
    }

    public Object[][] as2DArray() {
        if(rawVal instanceof Properties) {
            Properties p = (Properties) rawVal;
            Object[][] o = new Object[p.size()][];
            int i = 0;
            Set<String> s = new TreeSet<String>();
            for(Object key : p.keySet()) {
                s.add((String) key);
            }
            for(String key : s) {
                o[i] = new Object[2];
                o[i][0] = key;
                o[i][1] = p.get(key);
                i++;
            }
            return o;
        } else if(rawVal instanceof Map<?, ?>) {
            Map<?, ?> p = (Map<?, ?>) rawVal;
            Object[][] o = new Object[p.size()][];
            int i = 0;
            Set<String> s = new TreeSet<String>();
            for(Object key : p.keySet()) {
                s.add((String) key);
            }
            for(String key : s) {
                o[i] = new Object[2];
                o[i][0] = key;
                o[i][1] = p.get(key);
                i++;
            }
            return o;
        }
        return null;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public String getName() {
        return name;
    }
    public void setName(String nm) {
        name = nm;
    }
    public String getPrettyValStr() {
        return prettyValStr;
    }
    public void setPrettyValStr(String pvs) {
        prettyValStr = pvs;
    }
    public Object getRawVal() {
        return rawVal;
    }
    public void setRawVal(Object rv) {
        rawVal = rv;
    }
    public String getRawValStr() {
        return rawValStr;
    }
    public void setRawValStr(String rvs) {
        rawValStr = rvs;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String cd) {
        code = cd;
    }
    public ValueType getType() {
        return type;
    }
    public void setType(ValueType tp) {
        type = tp;
    }
    public String getUnits() {
        return units;
    }
    public void setUnits(String un) {
        units = un;
    }

    @Override
    public String toString() {
        return name + "=" + prettyValStr + " (raw=" + rawValStr + ")(" + code + ") TYPE=" + type;
    }
}
