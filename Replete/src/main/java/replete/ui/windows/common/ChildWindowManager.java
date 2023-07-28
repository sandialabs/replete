package replete.ui.windows.common;

import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeListener;

public interface ChildWindowManager {
    public void addChildWindowListener(ChangeListener listener);
    public void addChildWindowCreationHandler(String typeId, ChildWindowCreationHandler handler);

    public RWindow createChildWindow(String typeId, String uniqueId, Object... args);
    public RWindow openChildWindow(String typeId, String uniqueId, Object... args);

    public void showChildWindow(String typeId);
    public void showChildWindow(String typeId, String uniqueId);
    public void showChildWindow(RWindow win);

    public void hideChildWindow(String typeId);
    public void hideChildWindow(String typeId, String uniqueId);
    public void hideChildWindow(RWindow win);

    public void destroyChildWindow(String typeId);
    public void destroyChildWindow(String typeId, String uniqueId);
    public void destroyChildWindow(RWindow win);

    public RWindow getChildWindow(String typeId);
    public RWindow getChildWindow(String typeId, String uniqueId);
    public boolean existsChildWindow(String typeId);
    public boolean existsChildWindow(String typeId, String uniqueId);

    public String getTypeIdOfWindow(RWindow win);
    public String getUniqueIdOfWindow(RWindow win);

    public String[] getRegisteredTypeIds();
    public String[] getAllTypeIds();
    public Map<String, String[]> getAllUniqueIds();

    public List<RWindow> getAllChildWindows();
    public List<RWindow> getVisibleChildWindows();

    public <T extends RWindow> T getOrCreate(String typeId, String uniqueId, Object... args);
}
