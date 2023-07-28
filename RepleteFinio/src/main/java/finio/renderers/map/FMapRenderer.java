package finio.renderers.map;


public interface FMapRenderer {
    public String renderKey(Object K);
    public String renderValue(Object V);
    public String render(Object K, Object V);
}
