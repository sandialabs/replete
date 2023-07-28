package replete.diff;

import javax.swing.ImageIcon;

public class SubObjDifferGenerator extends DifferGenerator<SubObjDifferParams, SubObj> {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Sub Obj Default Differ";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public Class<?>[] getCoordinatedClasses() {
        return new Class[] {
            SubObjDifferParams.class,
            //SubObjDifferParamsPanel.class,
            SubObjDiffer.class
        };
    }

    @Override
    public SubObjDifferParams createParams() {
        return new SubObjDifferParams();
    }

    @Override
    public DifferParamsPanel<SubObjDifferParams> createParamsPanel(Object... args) {
        return null;
    }

    @Override
    public SubObjDiffer createDiffer(SubObjDifferParams params) {
        return new SubObjDiffer(params);
    }

    @Override
    public boolean canDiff(Class<?> clazz) {
        return false;
    }
}
