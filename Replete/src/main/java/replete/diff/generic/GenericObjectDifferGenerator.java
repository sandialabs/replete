package replete.diff.generic;

import javax.swing.Icon;

import replete.diff.DifferGenerator;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class GenericObjectDifferGenerator extends DifferGenerator<GenericObjectDifferParams, Object> {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Generic Object Differ";
    }

    @Override
    public String getDescription() {
        return "This differ provides the ability to compare fields of two generic objects.";
    }

    @Override
    public Icon getIcon() {
        return ImageLib.get(CommonConcepts.MODEL);
    }

    @Override
    public Class<?>[] getCoordinatedClasses() {
        return new Class[] {
            GenericObjectDiffer.class,
            GenericObjectDifferParams.class,
            GenericObjectDifferParamsPanel.class
        };
    }

    @Override
    public GenericObjectDiffer createDiffer(GenericObjectDifferParams params) {
        return new GenericObjectDiffer(params);
    }

    @Override
    public GenericObjectDifferParams createParams() {
        return new GenericObjectDifferParams();
    }

    @Override
    public GenericObjectDifferParamsPanel createParamsPanel(Object... args) {
        return new GenericObjectDifferParamsPanel();
    }

    @Override
    public boolean canDiff(Class<?> clazz) {
        return true;
    }

}
