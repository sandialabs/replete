package finio.core.events;

public interface KeyChangedListener extends MapListener {
    public void keyChanged(KeyChangedEvent e);
}
