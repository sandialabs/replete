package replete.collections.grouping;

import java.util.ArrayList;
import java.util.List;

// Experimental grouping code

// Potentially extra exceptions in place to ensure proper initial implementation.

public class Group<G, T> {

    Group<G, T> parent;
    List<Group<G, T>> children = new ArrayList<>();
    List<T> elements = new ArrayList<>();
    G groupInfo;           // Essentially the payload of this group, which is acting as a node in a grouping's tree

    public Group(G groupInfo) {
        this.groupInfo = groupInfo;
    }

    public Group setParent(Group<G, T> parent) {
        this.parent = parent;
        return this;
    }

    public void removeSubgroup(Group<G, T> group) {
        if(!children.contains(group)) {
            throw new RuntimeException("Group does not contain subgroup");
        }
        children.remove(group);
    }

    public void addSubgroup(Group<G, T> group) {
        if(children.contains(group)) {
            throw new RuntimeException("Group already contains subgroup");
        }
        children.add(group);
    }

    public Group<G, T> getParent() {
        return parent;
    }
    public List<Group<G, T>> getChildren() {
        return children;
    }
    public G getGroupInfo() {
        return groupInfo;
    }

    public void addElement(T elem) {
        if(!elements.contains(elem)) {
            elements.add(elem);
        }
    }
}
