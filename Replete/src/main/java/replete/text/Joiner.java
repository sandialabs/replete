package replete.text;

import java.util.Arrays;
import java.util.Map;

import replete.util.ReflectionUtil;

public class Joiner {


    ////////////
    // FIELDS //
    ////////////

    private Map<?, ?> map;
    private String kvSep = "=";
    private String kvPairSep = "\n";
    private String pre;
    private String post;

    private Object[] itemsArray;
    private Iterable<?> items;
    private String mid;
    private String prefix;
    private String suffix;

    private String accessorMethod;


    //////////
    // MISC //
    //////////

    public String joinList() {
        StringBuilder b = new StringBuilder();
        boolean first = true;
        if(items == null && itemsArray != null) {
            items = Arrays.asList(itemsArray);
        }
        for(Object o : items) {
            if(!first && mid != null) {
                b.append(mid);
            }
            if(prefix != null) {
                b.append(prefix);
            }
            if(o != null) {
                String str;
                if(accessorMethod != null) {
                    str = (String) ReflectionUtil.invoke(o, accessorMethod);
                } else {
                    str = o.toString();
                }
                b.append(str);
            }
            if(suffix != null) {
                b.append(suffix);
            }
            first = false;
        }
        return b.toString();
    }

    public String joinMap() {
        StringBuilder b = new StringBuilder();
        if(pre != null) {
            b.append(pre);
        }
        for(Object k : map.keySet()) {
            Object v = map.get(k);
            b.append("" + k);
            if(kvSep != null) {
                b.append(kvSep);
            }

            String str;
            if(accessorMethod != null) {
                str = (String) ReflectionUtil.invoke(v, accessorMethod);
            } else {
                str = v.toString();
            }
            b.append(str);

            if(kvPairSep != null) {
                b.append(kvPairSep);
            }
        }
        if(kvPairSep != null) {
            b.delete(b.length() - kvPairSep.length(), b.length());
        }
        if(post != null) {
            b.append(post);
        }
        return b.toString();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Map<?, ?> getMap() {
        return map;
    }
    public String getKvSep() {
        return kvSep;
    }
    public String getKvPairSep() {
        return kvPairSep;
    }
    public String getPre() {
        return pre;
    }
    public String getPost() {
        return post;
    }

    public Object[] getItemsArray() {
        return itemsArray;
    }
    public Iterable<?> getItems() {
        return items;
    }
    public String getMid() {
        return mid;
    }
    public String getPrefix() {
        return prefix;
    }
    public String getSuffix() {
        return suffix;
    }

    public String getAccessorMethod() {
        return accessorMethod;
    }

    // Mutators (Builders)

    public Joiner setMap(Map<?, ?> map) {
        this.map = map;
        return this;
    }
    public Joiner setKvSep(String kvSep) {
        this.kvSep = kvSep;
        return this;
    }
    public Joiner setKvPairSep(String kvPairSep) {
        this.kvPairSep = kvPairSep;
        return this;
    }
    public Joiner setPre(String pre) {
        this.pre = pre;
        return this;
    }
    public Joiner setPost(String post) {
        this.post = post;
        return this;
    }

    public Joiner setItemsArray(Object[] itemsArray) {
        this.itemsArray = itemsArray;
        return this;
    }
    public Joiner setItems(Iterable<?> items) {
        this.items = items;
        return this;
    }
    public Joiner setMid(String mid) {
        this.mid = mid;
        return this;
    }
    public Joiner setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
    public Joiner setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public Joiner setAccessorMethod(String accessorMethods) {
        accessorMethod = accessorMethods;
        return this;
    }
}
