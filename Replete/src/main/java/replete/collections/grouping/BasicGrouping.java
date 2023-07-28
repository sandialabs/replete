package replete.collections.grouping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.text.StringUtil;

// Experimental grouping code

// Potentially extra exceptions in place to ensure proper initial implementation.

public class BasicGrouping<G, T> extends Grouping<G, T> {


    ////////////
    // FIELDS //
    ////////////

    private final G ROOT = null;
    private Group<G, T> rootGroup = new Group<>(ROOT);
    private Map<G, Group<G, T>> groupIndirection = new HashMap<>();
    private Map<T, G> elementGroupMembership = new HashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public BasicGrouping() {
        rootGroup = new Group<>(ROOT);
        groupIndirection.put(ROOT, rootGroup);
    }

    @Override
    public G getRootGroup() {
        return rootGroup.groupInfo;
    }

    @Override
    public List<G> getSubgroups(G group) {
        Group<G, T> g = getGroup(group);
        List<G> sub = new ArrayList<>();
        for(Group<G, T> sub2 : g.children) {
            sub.add(sub2.groupInfo);
        }
        return sub;
    }

    @Override
    public List<G> getGroups(T object) {
        return null;
    }
//    public Group<G, T> getGroup(G xx) {   // how public is this?
//        return null;
//    }

    public void addGroup(G info) {
        if(groupIndirection.containsKey(info)) {
            throw new RuntimeException("Group already exists for " + info);
        }
        Group<G, T> g = new Group<>(info);
        rootGroup.children.add(g);
        g.parent = rootGroup;
    }
    public void addGroup(G info, G toParent) {
        if(groupIndirection.containsKey(info)) {
            throw new RuntimeException("Group already exists for " + info);
        }
        Group<G, T> p = getGroup(toParent);
        Group<G, T> g = new Group<>(info);
        rootGroup.children.add(g);
        g.parent = rootGroup;
    }

    public void setGroupParent(G info, G toParent) {
        Group<G, T> g = getGroup(info);
        Group<G, T> p = getGroup(toParent);

        g.parent.removeSubgroup(g);
        p.addSubgroup(g);
        g.setParent(p);
    }

    private Group<G, T> getGroup(G info) {
        if(info == null) {
            return rootGroup;
        }
        Group<G, T> g = groupIndirection.get(info);
        if(g == null) {
            throw new RuntimeException("No group found for " + info);
        }
        return g;
    }

    public void addElement(T elem, G toGroup) {
        if(toGroup == null) {
            throw new RuntimeException("Group cannot be null");
        }
        Group<G, T> g = getGroup(toGroup);
        g.addElement(elem);
    }

    public void print() {

    }
    private void print(Group<G, T> group, int level) {
        String sp = StringUtil.spaces(level * 4);
        String sp2 = StringUtil.spaces((level + 1) * 4);
        System.out.println(sp + group.groupInfo);
        for(T elem : group.elements) {
            System.out.println(sp2 + elem);
        }
        for(Group<G, T> sg : group.children) {
            print(sg, level + 1);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        BasicGrouping<String, Person> grouping = new BasicGrouping<>();
        grouping.setRootGroup("All");
        grouping.addGroup("A-M");
        grouping.addGroup("N-Z");
        grouping.addGroup("A-G");
        grouping.addGroup("H-M");
        grouping.addGroup("N-S");
        grouping.addGroup("Z-T");

        grouping.setGroupParent("A-G", "A-M");
        grouping.setGroupParent("H-M", "A-M");

        grouping.print();

    }

    private void setRootGroup(G group) {
        rootGroup.groupInfo = group;
    }

    static class Person {
        public String name;
        public Person(String name) {
            this.name = name;
        }
    }
}
