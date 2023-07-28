package gotstyle.model;

public class Pg {
    public Status status = Status.PENDING;
    public String msg = "?";
    public String src = "NEWPAGE";
    public String img = null;

    public void clear() {
        status = Status.PENDING;
        msg = "?";
        src = "NEWPAGE";
        img = null;
    }
}
