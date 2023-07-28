package replete.conf.node;

import java.util.ArrayList;
import java.util.List;

import replete.conf.Conf;
import replete.conf.ConfigurationDomain;
import replete.conf.exception.ConfigurationException;

public class ConfigurationPath {

    ////////////
    // FIELDS //
    ////////////

    public static final String LEVEL_DELIMITER = "/";
    public static final String KEY_VALUE_DELIMITER = ":";
    private List<ConfigurationNode> path;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConfigurationPath(List<ConfigurationNode> keyPath) {
        path = new ArrayList<>();
        path.addAll(keyPath);
    }

    public ConfigurationPath(ConfigurationNode ... keyPath) {
        path = new ArrayList<>();
        for(int i = 0; i < keyPath.length; i++) {
            path.add(keyPath[i]);
        }
    }

    public ConfigurationPath(String keyPath, List<ConfigurationDomain> domains, String levelDelimiter, String keyValueDelimiter) throws ConfigurationException {
        String[] segments = keyPath.split(levelDelimiter);
        for(int i = 0; i < segments.length; i++) {
            String key = null;
            String value = null;
            if(segments[i].contains(keyValueDelimiter)) {
                String[] keyValuePair = segments[i].split(keyValueDelimiter);
                assert(keyValuePair.length == 2);
                key = keyValuePair[0];
                value = keyValuePair[1];
            } else {
                key = segments[i];
            }

            ConfigurationKey newKey = Conf.getKeyFromDomainsByName(domains, key);
            if(newKey != null) {
                if(value != null) {
                    path.add(Conf.node(newKey, value));
                } else {
                    path.add(Conf.node(newKey));
                }
            } else {
                path = null;
                throw new ConfigurationException("Could not find key " + key + " in provided list of domains.");
            }
        }
    }

    public ConfigurationPath(String keyPath, List<ConfigurationDomain> domains) throws ConfigurationException {
        this(keyPath, domains, LEVEL_DELIMITER, KEY_VALUE_DELIMITER);
    }

    /////////////
    // GETTERS //
    /////////////

    public List<ConfigurationNode> getPath() {
        return path;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < path.size(); i++) {
            ConfigurationNode element = path.get(i);
            sb.append(element.getKey().getName());

            if(i < path.size() - 1) {
                sb.append(LEVEL_DELIMITER);
            } else {
                sb.append(KEY_VALUE_DELIMITER);
            }
        }
        sb.append(path.get(path.size()-1).getValue().toString());
        return sb.toString();
    }
}
