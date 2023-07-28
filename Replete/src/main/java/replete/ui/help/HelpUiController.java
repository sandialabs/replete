package replete.ui.help;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.ui.help.events.PageSelectionEvent;
import replete.ui.help.events.PageSelectionListener;
import replete.ui.help.model.HelpPage;

public class HelpUiController {
    private List<HelpPage> pageHistory = new ArrayList<>();
    private int pageHistoryIndex = -1;
    private boolean suppressX = false;

    public HelpUiController() {
        addPageSelectionListener(e -> {
            if(!suppressX) {
                navigatePageNew(e.getPage());
            }
        });
    }

    public int getPageHistoryIndex() {
        return pageHistoryIndex;
    }
    public List<HelpPage> getPageHistory() {
        return pageHistory;
    }

    public void navigatePageNew(HelpPage page) {
        if(pageHistory.isEmpty()) {
            pageHistory.add(page);
            pageHistoryIndex = 0;
        } else if(page != pageHistory.get(pageHistoryIndex)) {
            if(pageHistoryIndex != pageHistory.size() - 1) {
                for(int p = pageHistory.size() - 1; p > pageHistoryIndex; p--) {
                    pageHistory.remove(p);
                }
            }
            pageHistory.add(page);
            pageHistoryIndex++;
        }
        fireHistoryNavNotifier();
    }

    public void navigatePagePrevious() {
        if(pageHistoryIndex > 0) {
            pageHistoryIndex--;
            suppressX = true;
            select(pageHistory.get(pageHistoryIndex));
            suppressX = false;
            fireHistoryNavNotifier();
        }
    }

    public void navigatePageNext() {
        if(pageHistoryIndex >= 0 && pageHistoryIndex < pageHistory.size() - 1) {
            pageHistoryIndex++;
            suppressX = true;
            select(pageHistory.get(pageHistoryIndex));
            suppressX = false;
            fireHistoryNavNotifier();
        }
    }

    public void select(HelpPage page) {
        firePageSelectionListener(page);
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected ExtChangeNotifier<PageSelectionListener> pageSelectionNotifier =
        new ExtChangeNotifier<>();
    public void addPageSelectionListener(PageSelectionListener listener) {
        pageSelectionNotifier.addListener(listener);
    }
    protected void firePageSelectionListener(HelpPage page) {
        PageSelectionEvent event = new PageSelectionEvent(page);
        pageSelectionNotifier.fireStateChanged(event);
    }

    private transient ChangeNotifier historyNavNotifier = new ChangeNotifier(this);
    public void addHistoryNavListener(ChangeListener listener) {
        historyNavNotifier.addListener(listener);
    }
    private void fireHistoryNavNotifier() {
        historyNavNotifier.fireStateChanged();
    }
}
