package replete.io.fast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

@Ignore
public class Example1 implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    Object nullVal = null;
    boolean boolTrue = true;
    boolean boolFalse = false;
    byte b = -23;
    short sh = 4312;
    int x = Rand.randInt();
    int y = Rand.randInt();
    long l = Rand.randLong();
    float f = Rand.randFloat();
    double d = Rand.randDouble();
    char c = '$';
    String s = Rand.randString();
    Map map;
    Example1_1 e2 = new Example1_1();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Example1() {
        map = new HashMap();
        for(int i = 0; i < Rand.randInt(500); i++) {
            map.put(Rand.randString(), Rand.randLong());
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + b;
        result = prime * result + (boolFalse ? 1231 : 1237);
        result = prime * result + (boolTrue ? 1231 : 1237);
        result = prime * result + c;
        long temp;
        temp = Double.doubleToLongBits(d);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((e2 == null) ? 0 : e2.hashCode());
        result = prime * result + Float.floatToIntBits(f);
        result = prime * result + (int) (l ^ (l >>> 32));
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        result = prime * result + ((nullVal == null) ? 0 : nullVal.hashCode());
        result = prime * result + ((s == null) ? 0 : s.hashCode());
        result = prime * result + sh;
        result = prime * result + x;
        result = prime * result + y;
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
        Example1 other = (Example1) obj;
        if(b != other.b) {
            return false;
        }
        if(boolFalse != other.boolFalse) {
            return false;
        }
        if(boolTrue != other.boolTrue) {
            return false;
        }
        if(c != other.c) {
            return false;
        }
        if(Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d)) {
            return false;
        }
        if(e2 == null) {
            if(other.e2 != null) {
                return false;
            }
        } else if(!e2.equals(other.e2)) {
            return false;
        }
        if(Float.floatToIntBits(f) != Float.floatToIntBits(other.f)) {
            return false;
        }
        if(l != other.l) {
            return false;
        }
        if(map == null) {
            if(other.map != null) {
                return false;
            }
        } else if(!map.equals(other.map)) {
            return false;
        }
        if(nullVal == null) {
            if(other.nullVal != null) {
                return false;
            }
        } else if(!nullVal.equals(other.nullVal)) {
            return false;
        }
        if(s == null) {
            if(other.s != null) {
                return false;
            }
        } else if(!s.equals(other.s)) {
            return false;
        }
        if(sh != other.sh) {
            return false;
        }
        if(x != other.x) {
            return false;
        }
        if(y != other.y) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "{" + getClass().getSimpleName() + "}";
    }
}
