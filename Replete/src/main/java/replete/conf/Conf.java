package replete.conf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import replete.conf.exception.ConfigurationException;
import replete.conf.node.ConfigurationKey;
import replete.conf.node.ConfigurationNode;
import replete.conf.node.ConfigurationPath;
import replete.conf.node.ConfigurationRoot;

public class Conf {

    //////////////
    // CREATION //
    //////////////

    public static ConfigurationRoot root() {
        return new ConfigurationRoot();
    }

    public static ConfigurationRoot root(ConfigurationNode ... children) throws ConfigurationException {
        return (ConfigurationRoot) new ConfigurationRoot().setValue(Arrays.asList(children));
    }

    public static ConfigurationNode node(ConfigurationKey key) throws ConfigurationException {
        return node(key, new Object());
    }

    public static ConfigurationNode node(ConfigurationKey key, Object value) throws ConfigurationException {
        return new ConfigurationNode().setKey(key).setValue(value);
    }

    ////////////////
    // OPERATIONS //
    ////////////////

    public static ConfigurationRoot merge(ConfigurationRoot parent1, ConfigurationRoot parent2) throws ConfigurationException {
        Collection<ConfigurationNode> mergedChildren = new ArrayList<>();
        mergedChildren.addAll(parent1.getValue());
        mergedChildren.addAll(parent2.getValue());

        return root(mergedChildren.toArray(new ConfigurationNode[mergedChildren.size()]));
    }

    /////////////////
    // TREE SEARCH //
    /////////////////

    public static boolean keyExists(ConfigurationNode start, ConfigurationKey key, boolean recursive) {
        if(start.getKey().equals(key)) {
            return true;
        }

        if(isCollectionOfConfNodes(start.getValue()) && recursive) {
            for(Iterator<ConfigurationNode> elementIterator = ((Collection)start.getValue()).iterator(); elementIterator.hasNext(); ) {
                boolean found = keyExists(elementIterator.next(), key, true);
                if(found) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int keyCount(ConfigurationNode start, ConfigurationKey key, boolean recursive) {
        return keyCount(start, key, recursive, 0);
    }

    private static int keyCount(ConfigurationNode start, ConfigurationKey key, boolean recursive, int count) {
        if(start.getKey().equals(key)) {
            count ++;
        }

        if(isCollectionOfConfNodes(start.getValue()) && recursive) {
            for(Iterator<ConfigurationNode> elementIterator = ((Collection)start.getValue()).iterator(); elementIterator.hasNext(); ) {
                count = keyCount(elementIterator.next(), key, true, count);
            }
        }
        return count;
    }

    public static boolean nodeExists(ConfigurationNode start, ConfigurationNode node, boolean recursive) {
        if(start.equals(node)) {
            return true;
        }

        if(isCollectionOfConfNodes(start.getValue()) && recursive) {
            for(Iterator<ConfigurationNode> childNodeIterator = ((Collection)start.getValue()).iterator(); childNodeIterator.hasNext(); ) {
                boolean found = nodeExists(childNodeIterator.next(), node, true);
                if(found) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ConfigurationNode getNodeByKey(ConfigurationNode start, ConfigurationKey key, boolean recursive) {
        if(start.getKey().equals(key)) {
            return start;
        }

        if(isCollectionOfConfNodes(start.getValue()) && recursive) {
            for(Iterator<ConfigurationNode> nodeIterator = ((Collection)start.getValue()).iterator(); nodeIterator.hasNext(); ) {
                ConfigurationNode foundNode = getNodeByKey(nodeIterator.next(), key, true);
                if(foundNode != null && foundNode instanceof ConfigurationNode) {
                    return foundNode;
                }
            }
        }
        return null;
    }

    public static boolean isCollectionOfConfNodes(Object value) {
        if(value instanceof Collection) {
            Collection collection = (Collection)value;
            if(!collection.isEmpty()) {
                Object firstIterObject = collection.iterator().next();
                if(firstIterObject instanceof ConfigurationNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ConfigurationNode getNodeByPath(ConfigurationNode start, ConfigurationPath path) {
        return null;
    }

    /////////////
    // UTILITY //
    /////////////

    public static boolean keyExistsInDomains(List<ConfigurationDomain> domains, ConfigurationKey key) {
        boolean keyIdentified = false;
        for(ConfigurationDomain knownDomain : domains) {
            if(knownDomain.getAvailableDomainKeys().contains(key)) {
                keyIdentified = true;
                break;
            }
        }
        return keyIdentified;
    }

    public static ConfigurationKey getKeyFromDomainsByName(List<ConfigurationDomain> domains, String keyName) {
        for(ConfigurationDomain knownDomain : domains) {
            Collection<ConfigurationKey> keys = knownDomain.getAvailableDomainKeys();
            for(ConfigurationKey key : keys) {
                if(key.getName().equals(keyName)) {
                    return key;
                }
            }
        }
        return null;
    }

    public static ConfigurationPath getPathByNode(ConfigurationNode start, ConfigurationNode search) {
        return getPathByNode(start, search, new Stack<>());
    }

    public static ConfigurationPath getPathByNode(ConfigurationNode start, ConfigurationNode search, Stack<ConfigurationNode> stack) {
        boolean found = false;

        stack.push(start);
        if(!start.equals(search) && isCollectionOfConfNodes(start.getValue())) {
            for(ConfigurationNode child : (Collection<ConfigurationNode>)start.getValue()) {
                ConfigurationPath result = getPathByNode(child, search, stack);
                found = result != null;
                if(!found) {
                    stack.pop();
                } else {
                    break;
                }
            }
        } else {
            found = true;
        }

        if(found && !stack.empty()) {
            // If we found it, then empty the stack into the list.
            List<ConfigurationNode> confElementsList = new ArrayList<>();
            while(!stack.empty()) {
                confElementsList.add(stack.pop());
            }
            Collections.reverse(confElementsList);
            ConfigurationPath confPath = new ConfigurationPath(confElementsList);
            return confPath;
        } else {
            return null;
        }
    }

    public static String toShortConfString(ConfigurationNode element) {
        StringBuilder sb = new StringBuilder();
        sb.append(element.getKey().getName());
        return sb.toString();
    }

    public static String toFullConfString(ConfigurationNode element, int depth) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < depth; i++) {
            sb.append("\t");
        }
        sb.append(element.getKey().getName()).append(":");
        if(isCollectionOfConfNodes(element.getValue())) {
            for(ConfigurationNode child : (Collection<ConfigurationNode>)element.getValue()) {
                sb.append("\n");
                sb.append(Conf.toFullConfString(child, depth + 1));
            }
        } else {
            sb.append(element.getValue().toString());
        }

        return sb.toString();
    }
}
