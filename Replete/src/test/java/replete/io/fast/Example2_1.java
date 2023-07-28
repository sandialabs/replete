package replete.io.fast;

import java.io.Serializable;

import org.junit.Ignore;

@Ignore
public class Example2_1 implements Serializable {
    String s = Rand.randString(200);
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
        Example2_1 other = (Example2_1) obj;
        if(s == null) {
            if(other.s != null) {
                return false;
            }
        } else if(!s.equals(other.s)) {
            return false;
        }
        return true;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((s == null) ? 0 : s.hashCode());
        return result;
    }
}
