package finio.platform.exts.view.textonly;

import javax.swing.ImageIcon;

import finio.platform.exts.view.textonly.ui.TextOnlyViewPanel;
import finio.plugins.extpoints.View;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.ViewPanel;
import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.ImageLib;

public class TextOnlyView implements View {

    @Override
    public String getName() {
        return "Text Only";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(FinioImageModel.TEXTONLY_VIEW);
    }

    @Override
    public boolean canView(Object V) {
        return true;
    }

    @Override
    public ViewPanel createPanel(AppContext ac, WorldContext wc, Object K, Object V) {
        return new TextOnlyViewPanel(ac, wc, K, V, this);
    }

}
