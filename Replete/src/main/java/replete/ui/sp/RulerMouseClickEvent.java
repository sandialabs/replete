package replete.ui.sp;

import java.util.List;

public class RulerMouseClickEvent {
    private int x;
    private int y;
    private int line;
    private List<RulerDescriptor> descriptors;
    public RulerMouseClickEvent(int x, int y, int line, List<RulerDescriptor> descriptors) {
        super();
        this.x = x;
        this.y = y;
        this.line = line;
        this.descriptors = descriptors;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getLine() {
        return line;
    }
    public List<RulerDescriptor> getDescriptors() {
        return descriptors;
    }
}
