package replete.ui.sdplus;

import javax.swing.event.ChangeListener;

import replete.ui.sdplus.panels.ScalePanelModel;


/**
 * Serves as the backing model to a scale set panel.
 *
 * @author Derek Trumbo
 */

public interface ScaleSetPanelModel {

    // Returns a non-null array of keys, one to uniquely identify every scale
    // in the model.
    public String[] getScaleKeys();

    // Returns the scale panel model for the scale identified by the given key.
    // It is recommended that all calls for the same key return the same object.
    public ScalePanelModel getScalePanelModel(String key);

    // Returns the number of data elements that comprise the scale panel model
    // for the given key.  This isn't called by the ScaleSetPanel itself, but
    // is used by the Subselector, and could very well be used in the implementation
    // of the class that implements this interface.
    public int getDataElementCount(String key);

    // Returns the data element for a given scale and index.  This can be any
    // object.  The type of scale panel model returned by getScalePanelModel(String)
    // will determine what kind of scale panel will be created and thus determine
    // how these objects will be visualized.  A null value indicates "no data"
    // for that data element's value for that scale.
    public Object getValue(String key, int elementIndex) throws IllegalArgumentException;

    // Adds a listener that should be notified when the model changes.  Any
    // class that implements this interface is also responsible for keeping
    // track of all the listeners and then notifying those listeners when the
    // model changes.  The methods removeScaleSetPanelModelListener,
    // getScaleSetPanelModelListeners and fireScaleSetPanelModelChanged can
    // optionally be added to the class but are not required by this interface.
    // This is the same pattern employed by javax.swing.table.TableModel.
    public void addScaleSetPanelModelListener(ChangeListener listener);
}
