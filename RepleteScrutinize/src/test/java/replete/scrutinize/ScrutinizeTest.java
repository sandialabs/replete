package replete.scrutinize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import replete.plugins.PluginManager;
import replete.scrutinize.core.BaseSc;
import replete.scrutinize.core.Scrutinizer;
import replete.util.ReflectionUtil;

public class ScrutinizeTest {
    private boolean PRINT = false;

    @Test
    public void stub() {}

//    @Test
//    public void robot() {
//        try {
//            check(RobotSc.class, new Robot(), 2, PRINT);
//        } catch(AWTException e) {
//            fail();
//        }
//    }
//    @Test
//    public void system() {
//        check(SystemSc.class, null, 104, PRINT);
//    }
//    @Test
//    public void toolkit() {
//        check(ToolkitSc.class, null, 2, PRINT);
//    }
//    @Test
//    public void point() {
//        check(PointSc.class, new Point(7, 8), 2, PRINT);
//    }
//    @Test
//    public void rectangle() {
//        check(RectangleSc.class, new Rectangle(7, 8, 100, 200), 4, PRINT);
//    }
//    @Test
//    public void displayMode() {
//        check(DisplayModeSc.class,
//            GraphicsEnvironment.getLocalGraphicsEnvironment().
//            getDefaultScreenDevice().getDisplayMode(), 4, PRINT);
//    }
//    @Test
//    public void graphicsDevice() {
//        check(GraphicsDeviceSc.class,
//            GraphicsEnvironment.getLocalGraphicsEnvironment().
//            getDefaultScreenDevice(), 343, PRINT);
//    }
//    @Test
//    public void graphicsEnvironment() {
//        check(GraphicsEnvironmentSc.class, null, 700, PRINT);
//    }
//    @Test
//    public void graphicsConfiguration() {
//        check(GraphicsConfigurationSc.class,
//            GraphicsEnvironment.getLocalGraphicsEnvironment().
//            getDefaultScreenDevice().getDefaultConfiguration(), 10, PRINT);
//    }
//    @Test
//    public void pointerInfo() {
//        check(PointerInfoSc.class, MouseInfo.getPointerInfo(), 347, PRINT);
//    }
//    @Test
//    public void mouseInfo() {
//        check(MouseInfoSc.class, null, 349, true);
//    }
//    @Test
//    public void scrut() {
//        check(ScrutinizationSc.class, 1159, PRINT);
//    }


    ////////////
    // HELPER //
    ////////////

    private void check(Class<?> scClass, int expectedCount, boolean print) {
        check(scClass, null, expectedCount, print);
    }
    private void check(Class<?> scClass, Object nativeObj, int expectedCount, boolean print) {
        PluginManager.initialize(ScrutinizePlugin.class);
        Scrutinizer.initialize();
        BaseSc sc = (BaseSc) ReflectionUtil.create(scClass);
        try {
            sc.load(nativeObj);
        } catch(Exception e) {
            fail();
        }
        int count = sc.countNodes();
        if(print) {
            sc.print();
            System.out.println(scClass.getSimpleName() + ": " + count);
        }
        assertEquals(expectedCount, count);
    }
}
