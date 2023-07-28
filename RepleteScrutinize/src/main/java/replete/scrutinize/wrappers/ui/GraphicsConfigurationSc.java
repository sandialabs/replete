package replete.scrutinize.wrappers.ui;

import java.awt.GraphicsConfiguration;

import replete.scrutinize.core.BaseSc;

public class GraphicsConfigurationSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return GraphicsConfiguration.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "bounds",
            "bufferCapabilities",
            "colorModel",
            "device",
            "defaultTransform",
            "imageCapabilities",
            "normalizingTransform",
            "translucencyCapable",
        };
    }
}
