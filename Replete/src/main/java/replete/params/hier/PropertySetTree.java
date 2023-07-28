package replete.params.hier;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import replete.collections.Pair;
import replete.collections.RCacheMap;
import replete.collections.SetUtil;
import replete.plugins.SerializableEmptyEqualsObject;
import replete.text.StringUtil;
import replete.ui.params.hier.test.TestPropertyParams;
import replete.ui.validation.ValidationContext;
import replete.ui.validation.ValidationContextWrapperException;

// NOTE: A SINGLE "property" is a Java bean (POJO) that can have
// multiple fields (instance variables).

public class PropertySetTree<T> extends SerializableEmptyEqualsObject {


    ////////////
    // FIELDS //
    ////////////

    private PropertyGroup<T> root;

    // If PropertySetTree ever becomes editable, need to have 'clear cache' capability.
    // Just need method-level locking here since assumed no penalty for an
    // incorrect cache miss.
    private transient Map<T, PropertySet> cachedResolutions =
        Collections.synchronizedMap(new RCacheMap().setMaxCacheSize(1_000));   // # chosen arbitrarily


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PropertySetTree(PropertyGroup<T> root) {
        if(root == null) {
            root = new PropertyGroup<>()
                .setLabel("(All)")
                .setCriteria(obj -> true)
            ;
        }

        this.root = root;
    }

    public ValidationContext validate(PropertySetSpecification spec) {
        ValidationContext context = new ValidationContext();
        context.addCloseAction(c -> c.hasError(), c -> {
            throw new ValidationContextWrapperException(
                "An error has occurred validating the property tree.", c);
        });
        validate(spec, root, null, context);
        context.close();                 // Decides if need to throw an exception right now
        return context;
    }
    private void validate(PropertySetSpecification spec, PropertyGroup<T> group,
                          PropertyGroup<T> parentGroup, ValidationContext context) {

        // Check group's properties against spec (these are critical development errors).
        for(String key : group.getProperties().keySet()) {
            PropertySlot slot = spec.getSlot(key);
            context.error("Property key '" + key + "' does not exist in specification.", slot == null);
            if(slot != null) {
                PropertyParams params = group.getProperties().get(key);
                Class<? extends PropertyParams> clazz = params.getClass();
                if(!slot.getParamsClass().equals(clazz)) {
                    context.error("Property class '" + clazz.getName() + "' for key '" +
                        key + "' not consistent with specification.");
                }
            }
        }

        // Check for duplicate properties between levels (these are user errors).
        if(parentGroup != null) {
            for(String key : parentGroup.getProperties().keySet()) {
                if(group.getProperties().containsKey(key)) {
                    PropertyParams params = parentGroup.getProperties().get(key);
                    PropertyParams paramsChild = group.getProperties().get(key);
                    if(params.equals(paramsChild)) {
                        PropertySlot slot = spec.getSlot(key);
                        String id = (slot != null) ? slot.getName() : key;
                        String msg = "Property '" + id + "' same as parent property, has no effect.";
                        context.warn(msg);
                    }
                }
            }
        }

        // Validate child groups.
        int g = 0;
        for(PropertyGroup<T> child : group.getChildren()) {
            context.push("Group " + (g + 1) + StringUtil.suffixIf(child.getLabel(), ": "));
            validate(spec, child, group, context);
            context.pop();
            g++;
        }
    }


    ////////////////
    // RESOLUTION //
    ////////////////

    public PropertySet resolve(PropertySetSpecification spec, T obj) {
        PropertySet result = cachedResolutions.get(obj);
        if(result != null) {
            return result;
        }

        // The following condition should always be true, but due to the decision
        // to just make all criteria and criteria panels provided by client code,
        // even the root, it's possible that this condition returns false.  But
        // this result constitutes an error on the part of the criteria developer.
        // This could be changed to not check root criteria at all to prevent
        // the possibility for this error.
        if(root.getCriteria().appliesTo(obj)) {
            PropertySet properties = resolveInternal(obj, root);
            if(!properties.keySet().equals(spec.getKeys())) {
                Pair<Set<String>, Set<String>> diffResult =
                    SetUtil.diff(spec.getKeys(), properties.keySet());
                throw new PropertySetUnsatisfiedSpecException(
                    diffResult.getValue1(), diffResult.getValue2());
            }
            cachedResolutions.put(obj, properties);
            return properties;
        }

        // Technically, the design of this class COULD HAVE enabled this tree
        // to somehow provide a default criteria for the root node that it
        // knows will always return true, but the decision was made to
        // push responsibility off to client code for API consistency purposes
        // (to remove as many "nuanced details" as possible that developers would
        // have to learn in using these classes).
        throw new PropertySetRootException("Root node criteria did not apply to target");
    }

    private PropertySet resolveInternal(T obj, PropertyGroup<T> node) {
        PropertySet properties = new PropertySet(node.getProperties());
        for(PropertyGroup<T> child : node.getChildren()) {
            if(child.getCriteria().appliesTo(obj)) {
                properties.putAll(resolveInternal(obj, child));
                break;              // Take first child of this group.
            }
        }
        return properties;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public PropertyGroup<T> getRoot() {
        return root;
    }


    //////////
    // MISC //
    //////////

    // Can't be private in case subclasses of this class exist.
    public Object readResolve() {
        cachedResolutions =
            Collections.synchronizedMap(new RCacheMap().setMaxCacheSize(1_000));
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((root == null) ? 0 : root.hashCode());
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
        PropertySetTree other = (PropertySetTree) obj;
        if(root == null) {
            if(other.root != null) {
                return false;
            }
        } else if(!root.equals(other.root)) {
            return false;
        }
        return true;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        PropertySetSpecification spec = TestPropertyParams.createTestSpec();
        PropertySet properties = TestPropertyParams.createTestTree().resolve(spec, "aatest");
        properties.print();
    }
}
