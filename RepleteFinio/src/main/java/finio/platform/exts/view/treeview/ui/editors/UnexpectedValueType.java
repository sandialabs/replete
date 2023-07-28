package finio.platform.exts.view.treeview.ui.editors;

public class UnexpectedValueType extends RuntimeException {
    private Class<?> clazz;
    public UnexpectedValueType(Class<?> clazz) {
        this.clazz = clazz;
    }
    public Class<?> getClazz() {
        return clazz;
    }
}
