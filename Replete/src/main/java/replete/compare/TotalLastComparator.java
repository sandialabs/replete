package replete.compare;

import java.util.Comparator;

public class TotalLastComparator implements Comparator<String> {
    public int compare(String o1, String o2) {
        if(o1.equals(o2)) {
            return 0;
        }

        boolean t1 = o1.startsWith("Total");
        boolean t2 = o2.startsWith("Total");

        if(t1 && !t2) {
            return 1;
        } else if(t2 && !t1) {
            return -1;
        } else if(!t1 && !t2) {
            return o1.compareTo(o2);
        }
        return o2.length() - o1.length();
    }
}
