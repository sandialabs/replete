package replete.diff;

public abstract class Comparison {
    public abstract boolean isDiff();
    public String render() {
        return render(0);
    }
    public String render(int level) {
        StringBuilder buffer = new StringBuilder();
        render(buffer, level);
        return buffer.toString();
    }
    protected abstract void render(StringBuilder buffer, int level);
}
