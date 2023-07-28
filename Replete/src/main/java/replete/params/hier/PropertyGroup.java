package replete.params.hier;

import java.util.ArrayList;
import java.util.List;

import replete.plugins.SerializableEmptyEqualsObject;

// NOTE: A SINGLE "property" is a Java bean (POJO) that can have
// multiple fields (instance variables).

public class PropertyGroup<T> extends SerializableEmptyEqualsObject {


    ////////////
    // FIELDS //
    ////////////

    private String label;
    private PropertySet properties = new PropertySet();   // Local properties
    private Criteria<T> criteria;
    private List<PropertyGroup<T>> children = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PropertyGroup() {
        this(null);
    }
    public PropertyGroup(String label) {
        this.label = label;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getLabel() {
        return label;
    }
    public PropertySet getProperties() {
        return properties;
    }
    public Criteria<T> getCriteria() {
        return criteria;
    }
    public List<PropertyGroup<T>> getChildren() {
        return children;
    }

    // Mutators

    public PropertyGroup setLabel(String label) {
        this.label = label;
        return this;
    }
    public PropertyGroup setCriteria(Criteria<T> criteria) {
        this.criteria = criteria;
        return this;
    }
    public PropertyGroup addProperty(String key, PropertyParams params) {
        properties.put(key, params);
        return this;
    }
    public PropertyGroup setChildren(List<PropertyGroup<T>> children) {
        this.children = children;
        return this;
    }
    public PropertyGroup setProperties(PropertySet properties) {
        this.properties = properties;
        return this;
    }


    //////////
    // MISC //
    //////////

    public PropertyGroup<T> copyWithoutChildren() {
        return new PropertyGroup<>()
            .setLabel(label)
            .setProperties(new PropertySet(properties))
            .setCriteria(criteria)
        ;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        result = prime * result + ((criteria == null) ? 0 : criteria.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        PropertyGroup other = (PropertyGroup) obj;
        if(children == null) {
            if(other.children != null) {
                return false;
            }
        } else if(!children.equals(other.children)) {
            return false;
        }
        if(criteria == null) {
            if(other.criteria != null) {
                return false;
            }
        } else if(!criteria.equals(other.criteria)) {
            return false;
        }
        if(label == null) {
            if(other.label != null) {
                return false;
            }
        } else if(!label.equals(other.label)) {
            return false;
        }
        if(properties == null) {
            if(other.properties != null) {
                return false;
            }
        } else if(!properties.equals(other.properties)) {
            return false;
        }
        return true;
    }
}
