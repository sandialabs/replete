package replete.io.fast;

import java.io.Serializable;

import org.junit.Ignore;

@Ignore
public class Example4_1 implements Serializable {
    public int xx = 1;
    public int yy = 2;
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + xx;
        result = prime * result + yy;
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
        Example4_1 other = (Example4_1) obj;
        if(xx != other.xx) {
            return false;
        }
        if(yy != other.yy) {
            return false;
        }
        return true;
    }
}
