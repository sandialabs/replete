package replete.scrutinize.wrappers.ui;

import java.awt.geom.AffineTransform;

import replete.scrutinize.core.BaseSc;

public class AffineTransformSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return AffineTransform.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "m00;field",
            "m10;field",
            "m01;field",
            "m11;field",
            "m02;field",
            "m12;field",
            "determinant",
            "scaleX",
            "scaleY",
            "shearX",
            "shearY",
            "type",
            "translateX",
            "translateY",
            "identity"
        };
    }
}
