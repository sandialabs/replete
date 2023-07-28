package replete.pipeline.stages;

public interface Stage<T> {
    public T         execute(T input);
    public String    getName();
    public String    getShortDescription();
    public String    getDescription();
}
