package replete.parallel;

public interface ParallelizableCallable<P, R> {
    public R run(P param) throws Exception;
}
