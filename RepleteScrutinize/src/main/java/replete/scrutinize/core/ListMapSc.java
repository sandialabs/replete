package replete.scrutinize.core;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class ListMapSc extends BaseSc {
    public ListMapSc(boolean sort) {
        if(sort) {
            fields = new TreeMap<>();
        } else {
            fields = new LinkedHashMap<>();
        }
    }
    public void put(String k, ScFieldResult result) {
        fields.put(k, result);
    }
    public void set(int i, ScFieldResult result) {
        fields.put("" + i, result);
    }
}
