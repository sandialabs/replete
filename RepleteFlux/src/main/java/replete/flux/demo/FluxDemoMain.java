package replete.flux.demo;

import replete.flux.FluxPlugin;
import replete.plugins.PluginManager;
import replete.plugins.RepletePlugin;
import replete.util.DemoMain;

public class FluxDemoMain extends DemoMain {


    //////////
    // MAIN //
    //////////

    public static void main(String[] args) {
        PluginManager.initialize(
            RepletePlugin.class,
            FluxPlugin.class
        );

        DemoFrame frame = new DemoFrame();
        frame.setVisible(true);
    }
}
