package replete.diff;

import javax.swing.ImageIcon;

public class TestObjDifferGenerator extends DifferGenerator<TestObjDifferParams, TestObj> {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Test Obj Default Differ";
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
            TestObjDifferParams.class,
            //TestObjDifferParamsPanel.class,
            TestObjDiffer.class
        };
    }

    @Override
    public TestObjDifferParams createParams() {
        return new TestObjDifferParams();
    }

    @Override
    public DifferParamsPanel<TestObjDifferParams> createParamsPanel(Object... args) {
        return null;
    }

    public Class<TestObj> getTargetType() {
        return TestObj.class;
    }

    @Override
    public TestObjDiffer createDiffer(TestObjDifferParams params) {
        return new TestObjDiffer(params);
    }

    @Override
    public boolean canDiff(Class<?> clazz) {
        return false;
    }
}
