package replete.io.fast;

import java.io.Serializable;

import org.junit.Ignore;

@Ignore
public class Example4 implements Serializable {
    private Example4_1 field1 = new Example4_1();
    private Example4_1 field2 = new Example4_1();
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field1 == null) ? 0 : field1.hashCode());
        result = prime * result + ((field2 == null) ? 0 : field2.hashCode());
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
        Example4 other = (Example4) obj;
        if(field1 == null) {
            if(other.field1 != null) {
                return false;
            }
        } else if(!field1.equals(other.field1)) {
            return false;
        }
        if(field2 == null) {
            if(other.field2 != null) {
                return false;
            }
        } else if(!field2.equals(other.field2)) {
            return false;
        }
        return true;
    }
}
