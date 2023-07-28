package replete.ui.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationFrameTransformOptions {


    ////////////
    // FIELDS //
    ////////////

    private List<MessageLevel> keepLevels = new ArrayList<>();
    private ValidationMessageTransformer transformer;
    private boolean removeEmpty;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<MessageLevel> getKeepLevels() {
        return keepLevels;
    }
    public ValidationMessageTransformer getTransformer() {
        return transformer;
    }
    public boolean isRemoveEmpty() {
        return removeEmpty;
    }

    // Mutators

    public ValidationFrameTransformOptions addLevel(MessageLevel level) {
        keepLevels.add(level);
        return this;
    }
    public ValidationFrameTransformOptions setTransformer(ValidationMessageTransformer transformer) {
        this.transformer = transformer;
        return this;
    }
    public ValidationFrameTransformOptions setRemoveEmpty(boolean removeEmpty) {
        this.removeEmpty = removeEmpty;
        return this;
    }
}
