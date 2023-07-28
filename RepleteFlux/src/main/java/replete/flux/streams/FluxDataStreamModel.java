package replete.flux.streams;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;

public class FluxDataStreamModel {
    Map<String, DataStream> dataStreams = new LinkedHashMap<>();

    public Map<String, DataStream> getDataStreams() {
        return Collections.unmodifiableMap(dataStreams);
    }

    public void addDataStream(String id, DataStream stream) {
        dataStreams.put(id, stream);
        fireDataStreamModelChangeNotifier();
    }
    public void removeDataStream(String id) {
        dataStreams.remove(id);
        fireDataStreamModelChangeNotifier();
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private transient ChangeNotifier dataStreamModelChangeNotifier = new ChangeNotifier(this);
    public void addDataStreamModelChangeListener(ChangeListener listener) {
        dataStreamModelChangeNotifier.addListener(listener);
    }
    public void removeDataStreamModelChangeListener(ChangeListener listener) {
        dataStreamModelChangeNotifier.removeListener(listener);
    }
    private void fireDataStreamModelChangeNotifier() {
        dataStreamModelChangeNotifier.fireStateChanged();
    }
}
