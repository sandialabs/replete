package replete.ui.fc;

import java.util.List;

public interface RecentListContext<T> {
    public boolean isLinkClickable(T object);   // Whether or not the link is active/clickable
    public void linkClicked(T object);          // When recent link clicked
    public String getLinkNamePlural();        // Label above all the recent links
    public void addRecentLink(T link);
    public List<T> getRecentList();
    public void setRecentList(List<T> list);
}
