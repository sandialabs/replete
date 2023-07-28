package replete.text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Derek Trumbo
 */

// TODO: Add indentation, ignore fields of type, and custom formatting for certain fields types.

public class ToStringFormatter {

    ////////////
    // FIELDS //
    ////////////

    public static final String DEFAULT_START_DELIM = "{";
    public static final String DEFAULT_END_DELIM = "}";
    public static final String DEFAULT_FIELD_DELIM = ",";
    public static final boolean DEFAULT_EXPAND_OBJ = false;
    public static final int DEFAULT_MAX_LEVEL = 0;
    public static final boolean DEFAULT_INCLUDE_CLASS = false;
    public static final boolean DEFAULT_TOSTRING_COLLAPSE = false;

    private String startDelim = DEFAULT_START_DELIM;
    private String endDelim = DEFAULT_END_DELIM;
    private String fieldDelim = DEFAULT_FIELD_DELIM;
    private boolean expandObj = DEFAULT_EXPAND_OBJ;
    private int maxLevel = DEFAULT_MAX_LEVEL;
    private boolean includeClass = DEFAULT_INCLUDE_CLASS;
    private boolean toStringCollapse = DEFAULT_TOSTRING_COLLAPSE;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ToStringFormatter() {

    }

    public ToStringFormatter(String sd, String ed, String fd, boolean eo, int ml, boolean ic, boolean tsc) {
        startDelim = sd;
        endDelim = ed;
        fieldDelim = fd;
        expandObj = eo;
        maxLevel = ml;
        includeClass = ic;
        toStringCollapse = tsc;
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public String getStartDelim() {
        return startDelim;
    }

    public String getEndDelim() {
        return endDelim;
    }

    public String getFieldDelim() {
        return fieldDelim;
    }

    public boolean isExpandObj() {
        return expandObj;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean isIncludeClass() {
        return includeClass;
    }

    public boolean isToStringCollapse() {
        return toStringCollapse;
    }

    public void setStartDelim(String startDelim) {
        this.startDelim = startDelim;
    }

    public void setEndDelim(String endDelim) {
        this.endDelim = endDelim;
    }

    public void setFieldDelim(String fieldDelim) {
        this.fieldDelim = fieldDelim;
    }

    public void setExpandObj(boolean expandObj) {
        this.expandObj = expandObj;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setIncludeClass(boolean includeClass) {
        this.includeClass = includeClass;
    }

    public void setToStringCollapse(boolean toStringCollapse) {
        this.toStringCollapse = toStringCollapse;
    }

    ////////////
    // RENDER //
    ////////////

    public String render(Object obj) {
        return render(obj, 0);
    }

    // Could be refactored a little for better organization.

    private String render(Object obj, int curLevel) {
        if(obj == null) {
            return "null";
        }

        String ret = "";
        if(curLevel > maxLevel || curLevel == Integer.MAX_VALUE) {
            if(toStringCollapse) {
                ret += obj;
            } else {
                if(includeClass) {
                    ret += obj.getClass().getSimpleName();
                }
                ret += startDelim + "OBJ" + endDelim;
            }
        } else {
            if(includeClass) {
                ret += obj.getClass().getSimpleName();
            }
            if(Collection.class.isAssignableFrom(obj.getClass())) {
                ret += "[";
                boolean atLeastOne = false;
                for(Object elem : (Collection<?>) obj) {
                    ret += render(elem, curLevel + 1);
                    ret += fieldDelim;
                    atLeastOne = true;
                }
                if(atLeastOne) {
                    ret = ret.substring(0, ret.length() - fieldDelim.length());
                }
                ret += "]";
            } else {
                ret += startDelim;
                try {

                    List<Field> allFields = new ArrayList<Field>();
                    Class<?> thisClass = obj.getClass();
                    while(thisClass != null && !thisClass.equals(Object.class)) {
                        allFields.addAll(Arrays.asList(thisClass.getDeclaredFields()));
                        thisClass = thisClass.getSuperclass();
                    }

                        Class<?>[] simple = {byte.class, Byte.class, char.class, Character.class,
                                             boolean.class, Boolean.class, short.class, Short.class,
                                             int.class, Integer.class, long.class, Long.class,
                                             float.class, Float.class, double.class, Double.class,
                                             String.class};
                        for(Field f : allFields) {
                            f.setAccessible(true);
                            ret += f.getName() + "=";
                            if(Arrays.asList(simple).contains(f.getType())) {
                                ret += f.get(obj);
                            } else {
                                if(expandObj) {
                                    ret += render(f.get(obj), curLevel + 1);
                                } else {
                                    ret += render(f.get(obj), Integer.MAX_VALUE);
                                }
                            }
                            ret += fieldDelim;
                        }
                        if(allFields.size() != 0) {
                            ret = ret.substring(0, ret.length() - fieldDelim.length());
                        }
                } catch(Exception e) {
                    e.printStackTrace();
                    return "error";
                }
                ret += endDelim;
            }
        }

        return ret;
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        boolean expandObject = true;
        int maxlevel = 1;
        boolean collapseToString = false;

        ToStringFormatter ts = new ToStringFormatter("[", "]", ", ", expandObject, maxlevel, true, collapseToString);

        A a = new A();
        System.out.println(ts.render(a));
        a.b = null;
        System.out.println(ts.render(a));
        System.out.println(ts.render(null));
    }

    static class A {
        int x = 8;
        float y = 5.5F;
        B b = new B();
        double dd = 123.;
    }

    static class B {
        String f = "eee";
        boolean rr = false;
        C c = null;
    }

    static class C {
        char x = 'x';
    }

}
