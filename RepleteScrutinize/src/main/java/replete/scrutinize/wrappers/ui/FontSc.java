package replete.scrutinize.wrappers.ui;

import java.awt.Font;

import replete.scrutinize.core.BaseSc;

public class FontSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Font.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "availableAttributes",   // TODO
            "attributes",            // TODO
            "family",
            "fontName",
            "italicAngle",
            "missingGlyphCode",
            "name",
            "numGlyphs",
            "getPSName",
            "size",
            "size2D",
            "style",
            "transform",
            "layoutAttributes",
            "uniformLineMetrics",
            "bold",
            "italic",
            "plain",
            "transformed",
        };
    }
}
