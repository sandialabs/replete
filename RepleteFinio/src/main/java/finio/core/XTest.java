package finio.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XTest {
    public static void main(String[] args) {

        Object[][] paths = new Object[][] {
            {"US", "Colorado", "Denver"},
            {"US", "Colorado"},
            {"US", "New Mexico", "Bernalillo"},
            {"US", "New Mexico", "Bernalillo", "Abq"},
            {"US", "New Mexico", "Sandoval"},
            {"US", "Florida"},
            {"MX"}
        };

        Object[][] simplifiedPaths = simplify(paths);

        for(Object[] sPath : simplifiedPaths) {
            System.out.println(Arrays.toString(sPath));
        }
    }

    private static Object[][] simplify(Object[][] paths) {
        List<Object[]> accepted = new ArrayList<Object[]>();
        for(Object[] Ppossible : paths) {
            if(accepted.isEmpty()) {
                accepted.add(Ppossible);
                continue;
            }
            boolean addPossible = true;
            for(int s = accepted.size() - 1; s >= 0; s--) {
                Object[] Paccepted = accepted.get(s);
                if(isAncestor(Ppossible, Paccepted)) {
                    accepted.remove(s);
                } else if(isAncestor(Paccepted, Ppossible)) {
                    addPossible = false;
                    break;
                }
            }
            if(addPossible) {
                accepted.add(Ppossible);
            }
        }
        return accepted.toArray(new Object[0][]);
    }

    // Panc == null, Pdes == null
    private static boolean isAncestor(Object[] Panc, Object[] Pdes) {
        if(Pdes.length < Panc.length) {
            return false;
        }
        for(int i = 0; i < Panc.length; i++) {
            if(Panc[i] != Pdes[i] && !Panc[i].equals(Pdes[i])) {
                return false;
            }
        }
        return true;
    }
}
