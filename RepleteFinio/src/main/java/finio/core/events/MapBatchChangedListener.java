package finio.core.events;

public interface MapBatchChangedListener extends MapListener {
    public void mapBatchChanged(MapBatchChangedEvent e);
}
