package replete.conf.node;

import java.util.ArrayList;
import java.util.List;

import replete.conf.node.rule.ConfigurationRule;

public class ConfigurationKey<E> {

    ////////////
    // FIELDS //
    ////////////

    private final String name;
    private final String description;
    private final E defaultValue;
    private List<ConfigurationRule> rules;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConfigurationKey(String name, String description) {
        this(name, description, null);
    }

    public ConfigurationKey(String name, String description, E defaultValue) {
        this(name, description, defaultValue, new ArrayList<>());
    }

    public ConfigurationKey(String name, String description, E defaultValue, List<ConfigurationRule> rules) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;

        this.rules = new ArrayList<>();
        this.rules.addAll(rules);
    }

    /////////////
    // GETTERS //
    /////////////

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public E getDefaultValue() {
        return defaultValue;
    }

    public List<ConfigurationRule> getRules() {
        return rules;
    }

    ////////////////////////
    // GETTERS (COMPUTED) //
    ////////////////////////

    public Class getValueClassType() {
        return defaultValue.getClass();
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public boolean equals(Object other) {
        if(other instanceof ConfigurationKey) {
            if(((ConfigurationKey)other).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}