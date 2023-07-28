package replete.pipeline.stages;

public interface ParameterStage<T> {
    public void     setValue(T input);
    public T        getValue();
    public Stage<T> spawnCopy(T input);
}
