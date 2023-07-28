package replete.text;

import java.util.TreeMap;


public class StringHierarchyTree extends TreeMap<String, Object> {
    public void add(String... keys) {
        TreeMap<String, Object> map = this;
        for(int k = 0; k < keys.length; k++) {
            String key = keys[k];
            TreeMap<String, Object> next = (TreeMap) map.get(key);
            if(next == null) {
                next = new TreeMap<String, Object>();
                map.put(key, next);
            }
            map = next;
        }
    }

    public String dump() {
        StringBuilder buffer = new StringBuilder();
        dump(buffer, this, 0);
        return buffer.toString();
    }
    private void dump(StringBuilder buffer, TreeMap<String, Object> map, int level) {
        String sp = StringUtil.spaces(level * 4);
        for(String key : map.keySet()) {
            buffer.append(sp + key + "\n");
            TreeMap<String, Object> next = (TreeMap) map.get(key);
            if(next != null) {
                dump(buffer, next, level + 1);
            }
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        StringHierarchyTree s = new StringHierarchyTree();
        s.add("a", "b", "1");
        s.add("a", "b", "2");
        s.add("a", "c", "0");
        s.add("a", "c", "1");
        s.add("x", "b", "c");
        s.add("x", "f", "c");
        s.add("f", "b", "c");
        System.out.println(s.dump());
    }
}
