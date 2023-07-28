package replete.io.fast;

import org.junit.Ignore;

import replete.equality.EqualsUtil;
import replete.text.StringUtil;

@Ignore
public class FastTest {


    ////////////
    // FIELDS //
    ////////////

    public Object value;
    public long javaOutTime;
    public long fastOutTime;
    public String otPct;
    public long javaInTime;
    public long fastInTime;
    public String itPct;
    public long javaSize;
    public long fastSize;
    public String sizePct;
    public String javaEquals;
    public String fastEquals;
    public Object javaRead;
    public Object fastRead;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FastTest(Object value) {
        this.value = value;
    }


    //////////
    // MISC //
    //////////

    public void check() {
        javaEquals = EqualsUtil.equals(value, javaRead) ? "Y" : "!";
        fastEquals = EqualsUtil.equals(value, fastRead) ? "Y" : "!";
        if(javaOutTime == 0) {
            otPct = "";
        } else {
            double otPctD = ((double) fastOutTime / javaOutTime) * 100;
            otPct = String.format("%.1f%%", otPctD);
        }
        if(javaInTime == 0) {
            itPct = "";
        } else {
            double itPctD = ((double) fastInTime / javaInTime) * 100;
            itPct = String.format("%.1f%%", itPctD);
        }
        double sizePctD = ((double) fastSize / javaSize) * 100;
        sizePct = String.format("%.1f%%", sizePctD);
        if(!EqualsUtil.equals(javaRead, fastRead)) {
            System.out.println(javaRead);
            System.out.println(fastRead);
            throw new RuntimeException("Big Error");
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return StringUtil.max("" + value, 35);
    }
}
