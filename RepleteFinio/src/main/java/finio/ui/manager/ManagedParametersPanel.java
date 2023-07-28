package finio.ui.manager;

import finio.manager.ManagedParameters;
import replete.ui.panels.RPanel;

public abstract class ManagedParametersPanel extends RPanel {


    //////////////
    // ABSTRACT //
    //////////////

    public abstract ManagedParameters getParameters();
    public abstract void setParameters(ManagedParameters params);
    public abstract String getValidationMessage();

}
