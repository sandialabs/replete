package replete.flux;

import java.util.List;

import replete.flux.viz.VisualizerParams;
import replete.plugins.HumanDescriptor;

public class VisualizerCompoundParams {


    ////////////
    // FIELDS //
    ////////////

    private VisualizerParams params;
    private HumanDescriptor userDescriptor;
    private boolean enabled;
    private List<String> dataStreamIds;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public VisualizerCompoundParams(VisualizerParams params, HumanDescriptor userDescriptor,
                                    boolean enabled, List<String> dataStreamIds) {
        this.params         = params;
        this.userDescriptor = userDescriptor;
        this.enabled        = enabled;
        this.dataStreamIds  = dataStreamIds;
    }
    public VisualizerCompoundParams(VisualizerCompoundParams cParams) {
        params         = cParams.params;
        userDescriptor = cParams.userDescriptor;
        enabled        = cParams.enabled;
        dataStreamIds  = cParams.dataStreamIds;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public VisualizerParams getParams() {
        return params;
    }
    public HumanDescriptor getUserDescriptor() {
        return userDescriptor;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public List<String> getDataStreamIds() {
        return dataStreamIds;
    }

    // Mutators

    public VisualizerCompoundParams setParams(VisualizerParams params) {
        this.params = params;
        return this;
    }
    public VisualizerCompoundParams setUserDescriptor(HumanDescriptor userDescriptor) {
        this.userDescriptor = userDescriptor;
        return this;
    }
    public VisualizerCompoundParams setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    public VisualizerCompoundParams setDataStreamIds(List<String> dataStreamIds) {
        this.dataStreamIds = dataStreamIds;
        return this;
    }
}
