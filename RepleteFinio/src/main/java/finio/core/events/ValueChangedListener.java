package finio.core.events;

public interface ValueChangedListener extends MapListener {
    public void valueChanged(ValueChangedEvent e);
}
