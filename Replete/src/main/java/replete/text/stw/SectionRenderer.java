package replete.text.stw;

public interface SectionRenderer<T> {
    public String getTitle();
    public String getFileName();
    public String render(T data);
}
