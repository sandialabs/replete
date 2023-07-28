package finio.ui.fpanel.mlp;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import replete.text.StringUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class BorderLayoutCreatorGenerator extends LayoutCreatorGenerator<BorderLayoutParams> {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Text";
    }

    @Override
    public String getDescription() {
        return StringUtil.createMissingText("Description");
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(CommonConcepts.RENAME);
    }

    @Override
    public Class<?>[] getCoordinatedClasses() {
        return new Class[] {
            BorderLayoutParams.class,            // Empty
            //BorderLayoutParamsPanel.class      // Not needed yet
            BorderLayoutCreator.class,
        };
    }

    @Override
    public BorderLayoutParams createParams() {
        return new BorderLayoutParams();
    }

    @Override
    public LayoutParamsPanel<BorderLayoutParams> createParamsPanel(Object... args) {
//        JobParamsWindowContext context = (JobParamsWindowContext) args[0];
//        return new NoParametersExtractorParamsPanel(context, this);   // Placeholder for now, might return null soon
        return null;
    }

    @Override
    public BorderLayoutCreator createLayoutCreator(BorderLayoutParams params, JPanel pnl) {
        return null;
    }
}
