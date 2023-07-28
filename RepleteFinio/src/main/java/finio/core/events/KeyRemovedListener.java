package finio.core.events;

public interface KeyRemovedListener extends MapListener {
    public void keyRemoved(KeyRemovedEvent e);
}
