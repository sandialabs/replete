package finio.core.events;

public interface MapClearedListener extends MapListener {
    public void mapCleared(MapClearedEvent e);
}
