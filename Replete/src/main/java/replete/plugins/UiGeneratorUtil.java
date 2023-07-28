package replete.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.DefaultComboBoxModel;

import replete.compare.CompositeComparatorBuilder;
import replete.compare.GroupSequenceComparator;
import replete.compare.MethodIndirectionComparator;
import replete.plugins.ui.GeneratorWrapper;

public class UiGeneratorUtil {


    /////////////////
    // COMBO BOXES //
    /////////////////

    // These methods help with the building of a combo box model using
    // all the extensions loaded for a given extension point.  Specifically,
    // those extensions of the UiGenerator subtype, which by default have
    // a name, description, and icon (things needed to display an extension
    // to the user for their inspection and selection).  Overloaded versions
    // of these methods allow the developer to specify which extensions
    // should appear at the top or bottom of the model.
    public static <T extends UiGenerator>
            DefaultComboBoxModel<GeneratorWrapper<T>>
                createExtensionComboModel(Class<T> extPointClass) {
        return createExtensionComboModelInner(extPointClass, null, null, null);
    }
    public static <T extends UiGenerator>
            DefaultComboBoxModel<GeneratorWrapper<T>>
                createExtensionComboModel(Class<T> extPointClass, Predicate<T> criterion) {
        return createExtensionComboModelInner(extPointClass, criterion, null, null);
    }
    public static <T extends UiGenerator>
            DefaultComboBoxModel<GeneratorWrapper<T>>
                createExtensionComboModel(Class<T> extPointClass, Class<?> makeFirstClass) {
        return createExtensionComboModelInner(extPointClass, null, makeFirstClass, null);
    }
    public static <T extends UiGenerator>
            DefaultComboBoxModel<GeneratorWrapper<T>>
                createExtensionComboModel(Class<T> extPointClass, Class<?> makeFirstClass, Class<?> makeLastClass) {
        return createExtensionComboModelInner(extPointClass, null, makeFirstClass, makeLastClass);
    }
    public static <T extends UiGenerator>
            DefaultComboBoxModel<GeneratorWrapper<T>>
                createExtensionComboModel(Class<T> extPointClass, Predicate<T> criterion, Class<?> makeFirstClass, Class<?> makeLastClass) {
        return createExtensionComboModelInner(extPointClass, criterion, makeFirstClass, makeLastClass);
    }

    private static <T extends UiGenerator>
            DefaultComboBoxModel<GeneratorWrapper<T>>
                createExtensionComboModelInner(Class<T> extPointClass, Predicate<T> criterion, Class<?> makeFirstClass, Class<?> makeLastClass) {
        DefaultComboBoxModel<GeneratorWrapper<T>> mdlViewGenerators =
            new DefaultComboBoxModel<>();

        List<ExtensionPoint> exts0 = PluginManager.getExtensionsForPoint((Class<? extends ExtensionPoint>) extPointClass);
        List<T> exts = new ArrayList<>();    // Get new modifiable list
        for(ExtensionPoint ext0 : exts0) {   // One day this conversion might be easier
            if(criterion == null || criterion.test((T) ext0)) {
                exts.add((T) ext0);
            }
        }

        // Configure the group comparator - the top-level comparator
        // used below.
        List<Object> classGroups = new ArrayList<>();
        if(makeFirstClass != null) {
            classGroups.add(makeFirstClass);
        }
        classGroups.add(GroupSequenceComparator.OTHERS);
        if(makeLastClass != null) {
            classGroups.add(makeLastClass);
        }
        GroupSequenceComparator classGroupCmp = new GroupSequenceComparator<Class>(classGroups);

        CompositeComparatorBuilder<T> builder = new CompositeComparatorBuilder<>();
        builder.addComparator("type", new MethodIndirectionComparator<T, Class>("getClass", classGroupCmp));
        builder.addComparator("name", (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        Collections.sort(exts, builder.build("type", "name"));

        for(T ext : exts) {
            mdlViewGenerators.addElement(new GeneratorWrapper<>(ext));
        }
        return mdlViewGenerators;
    }
}
