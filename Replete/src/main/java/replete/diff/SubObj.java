package replete.diff;

import java.util.ArrayList;
import java.util.List;

public class SubObj {
    public int s;
    public int t;
    public List<String> words = new ArrayList<>();

    public SubObj(int s, int t) {
        this.s = s;
        this.t = t;
    }

    public SubObj setS(int s) {
        this.s = s;
        return this;
    }

    public SubObj setT(int t) {
        this.t = t;
        return this;
    }

    @Override
    public String toString() {
        return "[" + s + "," + t + "," + words + "]";
    }
}
