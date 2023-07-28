package finio.core.events;

public interface KeyAddedListener extends MapListener {
    public void keyAdded(KeyAddedEvent e);
}
