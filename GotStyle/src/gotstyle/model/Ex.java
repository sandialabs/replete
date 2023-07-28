package gotstyle.model;

import java.util.ArrayList;
import java.util.List;

public class Ex {
    public Status status = Status.PENDING;
    public String title;
    public List<Pg> pages = new ArrayList<>();
    public Ex() {
        pages.add(new Pg());   // Never zero pages
    }
}
