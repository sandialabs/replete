package replete.ui.label;

import java.awt.Component;

public interface DatumSeparatorGenerator {
    Component createSeparator(DatumDescriptor datumLeft, DatumDescriptor datumRight);
}
