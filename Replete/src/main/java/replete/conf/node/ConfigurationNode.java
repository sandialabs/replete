package replete.conf.node;

import java.util.ArrayList;
import java.util.List;

import replete.conf.ConfigurationIssue;
import replete.conf.exception.ConfigurationException;
import replete.conf.node.rule.ConfigurationRule;

public class ConfigurationNode<E> {

    ////////////
    // FIELDS //
    ////////////

    protected ConfigurationKey<E> key;
    protected E value = null;
    protected List<ConfigurationNodeListener> changeListeners = new ArrayList<>();

    /////////////
    // GETTERS //
    /////////////

    public ConfigurationKey getKey() {
        return key;
    }

    public E getValue() {
        if(value == null && key != null) {
            return key.getDefaultValue();
        }
        return value;
    }

    /////////////
    // SETTERS //
    /////////////

    public ConfigurationNode setKey(ConfigurationKey key) {
        this.key = key;
        return this;
    }

    public ConfigurationNode setValue(E value) throws ConfigurationException {
        boolean valid = true;
        List<ConfigurationIssue> issues = new ArrayList<>();

        for(ConfigurationRule rule : key.getRules()) {
            if(!rule.validate(this)) {
                valid = false;
                issues.add(rule.getIssue());
            }
        }

        if(valid) {
            this.value = value;
            fireChangeEvent();
        } else {
            throw new ConfigurationException(issues);
        }
        return this;
    }

    ///////////////
    // LISTENERS //
    ///////////////

    public void addChangeListener(ConfigurationNodeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ConfigurationNodeListener listener) {
        changeListeners.remove(listener);
    }

    public void fireChangeEvent() {
        for(ConfigurationNodeListener listener : changeListeners) {
            listener.valueChanged();
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public boolean equals(Object other) {
        if(other instanceof ConfigurationNode) {
            ConfigurationNode otherElement = (ConfigurationNode) other;
            return otherElement.getKey().equals(key) && otherElement.getValue().equals(value);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(key.getName()).append(":").append(value.toString());
        return sb.toString();
    }
}
