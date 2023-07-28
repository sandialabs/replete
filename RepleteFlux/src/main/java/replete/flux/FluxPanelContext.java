package replete.flux;

import javax.swing.event.ChangeListener;

import replete.flux.streams.FluxDataStreamModel;

public interface FluxPanelContext {
    public FluxDataStreamModel getDataStreamModel();

    public void addDataStreamModelChangeListener(ChangeListener listener);
    public void removeDataStreamModelChangeListener(ChangeListener listener);
}
