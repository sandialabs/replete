package replete.ui.uiaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

/**
 * @author Derek Trumbo
 */

public abstract class UIActionDescriptor {
    protected Map<String, Boolean> enabledStateMap;
    public abstract void validate(String state, boolean valid);

    public abstract JComponent getComponent();

    protected List<UIActionEnabledDecider> stateCheckers = new ArrayList<>();
    public void addStateChecker(UIActionEnabledDecider c) {
        stateCheckers.add(c);
    }

    public UIActionDescriptor setEnabledStateMap(Map<String, Boolean> enabledStateMap) {
        this.enabledStateMap = enabledStateMap;
        return this;
    }

    protected boolean doEnable(String state) {
        if(enabledStateMap == null) {
            return true;
        }
        boolean enabled = enabledStateMap.get(state) != null &&
            enabledStateMap.get(state).booleanValue();
        for(UIActionEnabledDecider checker : stateCheckers) {
            enabled = enabled && checker.canEnable();
        }
        return enabled;
    }
}
