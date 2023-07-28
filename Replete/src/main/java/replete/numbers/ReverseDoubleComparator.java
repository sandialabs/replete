package replete.numbers;

import java.io.Serializable;
import java.util.Comparator;

public class ReverseDoubleComparator implements Comparator<Double>, Serializable {
    @Override
    public int compare(Double d1, Double d2) {
        return -Double.compare(d1, d2);
    }
}
