package replete.io.fast;

import java.io.Serializable;

import org.junit.Ignore;

@Ignore
public class Example1_1 implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    public int a = -123;
    public float b = 5123.44F;
    public String s = "abc";


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + a;
        result = prime * result + Float.floatToIntBits(b);
        result = prime * result + ((s == null) ? 0 : s.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        Example1_1 other = (Example1_1) obj;
        if(a != other.a) {
            return false;
        }
        if(Float.floatToIntBits(b) != Float.floatToIntBits(other.b)) {
            return false;
        }
        if(s == null) {
            if(other.s != null) {
                return false;
            }
        } else if(!s.equals(other.s)) {
            return false;
        }
        return true;
    }
}
