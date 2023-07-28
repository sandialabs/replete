package replete.ui.help;

import replete.plugins.ExtensionPoint;
import replete.plugins.UiGenerator;
import replete.ui.help.model.HelpAlbum;

// Needs UiGenerator for name, description, icon, not necessarily
// for class generation or coordination.

public abstract class HelpProvider extends UiGenerator implements ExtensionPoint {

    protected HelpAlbum album;       // Every HelpProvider has a single album (which has multiple "root pages")

    public HelpAlbum getAlbum() {
        return album;
    }

    public abstract void loadAlbum();
    public abstract void saveAlbum();
}
