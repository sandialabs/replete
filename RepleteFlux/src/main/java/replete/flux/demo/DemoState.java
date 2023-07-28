package replete.flux.demo;

import java.util.List;

import replete.flux.FluxPanelParams;

public class DemoState {
    List<FluxPanelParams> params;
    public DemoState setParams(List<FluxPanelParams> params) {
        this.params = params;
        return this;
    }
}
