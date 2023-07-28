package replete.ui.help.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.event.ExtChangeNotifier;
import replete.io.FileUtil;
import replete.ui.help.StandardHelpProvider;
import replete.ui.help.events.PageContentChangedEvent;
import replete.ui.help.events.PageContentChangedListener;
import replete.ui.help.events.PageRenameEvent;
import replete.ui.help.events.PageRenameListener;

public class HelpDataModel {


    ////////////
    // FIELDS //
    ////////////

    private List<HelpAlbum> albums = new ArrayList<>();
    private List<Bookmark> bookmarks = new ArrayList<>();
    private Map<String, HelpPage> appReferences = new HashMap<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<HelpAlbum> getAlbums() {
        return albums;
    }
    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    // Mutators

    public HelpDataModel addAlbum(HelpAlbum album) {
        album.initParentReferences();
        albums.add(album);
        return this;
    }

    public HelpDataModel addBookmark(Bookmark bookmark) {
        bookmarks.add(bookmark);
        return this;
    }
    public HelpDataModel removeBookmark(Bookmark bookmark) {
        bookmarks.remove(bookmark);
        return this;
    }

    public void rename(HelpPage page, String name) {
        page.setName(name);
        firePageRenameListener(page);
    }

    public void change(HelpPage page, String source) {
        HelpPageContent content = page.getContent();
        if(content.getHtmlContent() != null) {
            content.setHtmlContent(source);
        } else {
            StandardHelpProvider provider = (StandardHelpProvider) page.getAlbum().getProvider();
            URL url = provider.getContentResource(content.getHtmlPath());
            File file = FileUtil.toFile(url);
            File fileSrc = provider.convertWorkspaceFileOutputToSource(file);
            FileUtil.writeTextContent(file, source);
            FileUtil.writeTextContent(fileSrc, source);
        }
        firePageContentChangedListener(page);
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected ExtChangeNotifier<PageRenameListener> pageRenameNotifier =
        new ExtChangeNotifier<>();
    public void addPageRenameListener(PageRenameListener listener) {
        pageRenameNotifier.addListener(listener);
    }
    protected void firePageRenameListener(HelpPage page) {
        PageRenameEvent event = new PageRenameEvent(page);
        pageRenameNotifier.fireStateChanged(event);
    }

    protected ExtChangeNotifier<PageContentChangedListener> pageContentChangedNotifier =
        new ExtChangeNotifier<>();
    public void addPageContentChangedListener(PageContentChangedListener listener) {
        pageContentChangedNotifier.addListener(listener);
    }
    protected void firePageContentChangedListener(HelpPage page) {
        PageContentChangedEvent event = new PageContentChangedEvent(page);
        pageContentChangedNotifier.fireStateChanged(event);
    }
}
