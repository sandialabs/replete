package finio.platform.exts.view.consoleview;

import javax.swing.ImageIcon;

import finio.core.FUtil;
import finio.platform.exts.view.consoleview.ui.ConsolePanel;
import finio.plugins.extpoints.View;
import finio.ui.app.AppContext;
import finio.ui.view.ViewPanel;
import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class ConsoleView implements View {
    // TODO: console doesn't fire anyaction on 'rm' command

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(CommonConcepts.CONSOLE);
    }

    @Override
    public ViewPanel createPanel(AppContext ac, WorldContext wc, Object K, Object V) {
        return new ConsolePanel(ac, wc, K, V, this);
    }

    @Override
    public boolean canView(Object V) {
        return FUtil.isNonTerminal(V);
    }

}
