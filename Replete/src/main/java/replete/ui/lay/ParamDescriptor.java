package replete.ui.lay;

public class ParamDescriptor {
    ParamType type;
    String msg;
    public ParamDescriptor(ParamType type, String msg) {
        this.type = type;
        this.msg = msg;
    }
    @Override
    public String toString() {
        if(type == ParamType.CUSTOM) {
            return msg;
        }
        return type.name() + (msg == null ? "" : ": " + msg);
    }
}
