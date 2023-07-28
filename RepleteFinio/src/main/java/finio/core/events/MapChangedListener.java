package finio.core.events;

public interface MapChangedListener extends MapListener {
    public void mapChanged(MapChangedEvent e);
}
