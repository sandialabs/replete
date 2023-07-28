package replete.ui.help.test;

import javax.swing.Icon;

import replete.text.StringUtil;
import replete.ui.help.StandardHelpProvider;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class ExampleHelpProvider extends StandardHelpProvider {

    @Override
    public String getName() {
        return "Example Help Provider";
    }

    @Override
    public String getDescription() {
        return StringUtil.createMissingText("Description");
    }

    @Override
    public Icon getIcon() {
        return ImageLib.get(CommonConcepts._PLACEHOLDER);
    }

    @Override
    protected String getDefaultAlbumName() {
        return "example";
    }

    @Override
    protected String getWorkspaceSourceDir() {
        return "src/ui";
    }
}
