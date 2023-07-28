package replete.io.fast;

import java.io.Serializable;

import org.junit.Ignore;

@Ignore
public class Key implements Serializable {
    public String a;
    public float x;
    public Key() {}
    public Key(String a, float x) {
        this.a = a;
        this.x = x;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + Float.floatToIntBits(x);
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
        Key other = (Key) obj;
        if(a == null) {
            if(other.a != null) {
                return false;
            }
        } else if(!a.equals(other.a)) {
            return false;
        }
        if(Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        return true;
    }
}
