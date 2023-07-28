package replete.io.fast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

@Ignore
public class Example2 implements Serializable {
    Example2_1 a = new Example2_1();
    Example2_1 b = new Example2_1();
    Map<Key, Example3[]> other = new HashMap<Key, Example3[]>();
    public Example2() {
        for(int x = 0; x < 20; x++) {
            Key k = new Key(Rand.randString(20), Rand.randFloat());
            int sz = Rand.randInt(33);
            Example3[] ee = new Example3[sz];
            for(int s = 0; s < sz; s++) {
                ee[s] = new Example3();
            }
            other.put(k, ee);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
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
        Example2 other = (Example2) obj;
        if(a == null) {
            if(other.a != null) {
                return false;
            }
        } else if(!a.equals(other.a)) {
            return false;
        }
        if(b == null) {
            if(other.b != null) {
                return false;
            }
        } else if(!b.equals(other.b)) {
            return false;
        }
        return true;
    }

}
