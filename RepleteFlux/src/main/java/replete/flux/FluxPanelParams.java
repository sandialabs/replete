package replete.flux;

import java.util.ArrayList;
import java.util.List;

import replete.flux.viz.VisualizerGenerator;
import replete.plugins.Generator;

public class FluxPanelParams {


    ////////////
    // FIELDS //
    ////////////

    private List<VisualizerCompoundParams> visualizerCompoundParams = new ArrayList<>();
    private VisualizerCombineMethod combineMethod = VisualizerCombineMethod.TABS;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<VisualizerCompoundParams> getVisualizerCompoundParams() {
        return visualizerCompoundParams;
    }
    public VisualizerCombineMethod getCombineMethod() {
        return combineMethod;
    }

    // Mutators

    public FluxPanelParams setVisualizerCompoundParams(List<VisualizerCompoundParams> visualizerCompoundParams) {
        this.visualizerCompoundParams = visualizerCompoundParams;
        return this;
    }
    public FluxPanelParams setCombineMethod(VisualizerCombineMethod combineMethod) {
        this.combineMethod = combineMethod;
        return this;
    }


    //////////
    // MISC //
    //////////

    public void dump() {
        int v = 0;
        System.out.println(combineMethod + ":");
        for(VisualizerCompoundParams vParams : getVisualizerCompoundParams()) {
            VisualizerGenerator generator = Generator.lookup(vParams.getParams());
            System.out.println(
                v + "/" +
                generator.getName() + "/" +
                vParams.getUserDescriptor().getName() + "/" +
                vParams.getParams() + "/" +
                vParams.isEnabled()
            );
            v++;
        }
    }
}
