package replete.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.numbers.NumUtil;
import replete.text.StringUtil;

public class MapUtil {

    public static boolean isBlank(Map map) {
        return map == null || map.isEmpty();
    }

    //Example: input = "d=dog,c=3,a=fjdi sl $sds"
    public static Map sToS(String input) {
        Map map = new HashMap<>();
        String[] pairs = input.split(",");
        for(String pair : pairs) {
            String[] kv = pair.split("=");
            String k = kv[0].equals("null") ? null : kv[0];
            String v = kv[1].equals("null") ? null : kv[1];
            map.put(k, v);
        }
        return map;
    }

    // Example: input = "a=1,b=3,null=0"
    public static Map sToI(String input) {
        Map map = new HashMap<>();
        String[] pairs = input.split(",");
        for(String pair : pairs) {
            String[] kv = pair.split("=");
            String k = kv[0].equals("null") ? null : kv[0];
            Integer v = Integer.parseInt(kv[1]);
            map.put(k, v);
        }
        return map;
    }

    // Example: input = "k=[2,3],q=[5,4,6],d=null,e=[]"
    public static Map sToLofS(String input) {
        Map map = new HashMap<>();
        String[] pairs = input.split("\\s*,\\s*");
        for(String pair : pairs) {
            String[] kv = pair.split("\\s*=\\s*");
            String k = kv[0].equals("null") ? null : kv[0];
            List<String> list = null;
            if(!kv[1].equals("null")) {
                list = new ArrayList<>();
                if(kv[1].startsWith("[")) {
                    kv[1] = StringUtil.squeeze(kv[1], 1);
                    for(String part : kv[1].split("\\s*;\\s*")) {
                        if(!part.isEmpty()) {
                            list.add(part);
                        }
                    }
                } else {
                    list.add(kv[1]);
                }
            }
            map.put(k, list);
        }
        return map;
    }

    // Example: input = "k=[2,3],q=[5,4,6],d=null,e=[]"
    public static Map sToLofI(String input) {
        Map map = new HashMap<>();
        String[] pairs = input.split("\\s*,\\s*");
        for(String pair : pairs) {
            String[] kv = pair.split("\\s*=\\s*");
            String k = kv[0].equals("null") ? null : kv[0];
            List<Integer> list = null;
            if(!kv[1].equals("null")) {
                list = new ArrayList<>();
                if(kv[1].startsWith("[")) {
                    kv[1] = StringUtil.squeeze(kv[1], 1);
                    for(String part : kv[1].split("\\s*;\\s*")) {
                        if(!part.isEmpty()) {
                            list.add(NumUtil.i(part));
                        }
                    }
                } else {
                    list.add(NumUtil.i(kv[1]));
                }
            }
            map.put(k, list);
        }
        return map;
    }

    public static void main(String[] args) {
        System.out.println(sToLofI("a=1,b=[2;3],d=null"));
    }
}
