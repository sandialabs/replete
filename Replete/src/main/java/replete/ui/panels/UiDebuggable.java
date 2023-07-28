package replete.ui.panels;

import java.awt.Color;

public interface UiDebuggable {
    public boolean isDebugColorEnabled();
    public boolean isDebugTicksEnabled();
    public boolean isDebugMouseEnabled();
    public void setDebugColorEnabled(boolean debugColorEnabled);
    public void setDebugColor(Color debugBackgroundColor);
    public void setDebugTicksEnabled(boolean debugTicksEnabled);
    public void setDebugMouseEnabled(boolean debugMouseEnabled);
}
