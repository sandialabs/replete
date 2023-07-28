package replete.event.rnotif;

import java.util.ArrayList;
import java.util.List;

import replete.ui.GuiUtil;

public class RFireContext {


    ////////////
    // FIELDS //
    ////////////

    private RChangeEvent event;
    private List<RChangeListenerConfig> listenerListCopy;
    private List<RChangeListenerConfig> configToAdd;
    private List<RChangeListenerConfig> configToRemove;



    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RFireContext(RChangeEvent event, List<RChangeListenerConfig> listenerListCopy) {
        this.event = event;
        this.listenerListCopy = listenerListCopy;
    }


    public void add(RChangeListenerConfig config) {
        if(configToAdd == null) {
            configToAdd = new ArrayList<>();
        }
        configToAdd.add(config);
    }
    public void remove(RChangeListenerConfig config) {
        if(configToRemove == null) {
            configToRemove = new ArrayList<>();
        }
        configToRemove.add(config);
    }
    public List<RChangeListenerConfig> getConfigToAdd() {
        return configToAdd;
    }
    public List<RChangeListenerConfig> getConfigToRemove() {
        return configToRemove;
    }


    //////////
    // FIRE //
    //////////

    public void fire() {
        for(final RChangeListenerConfig config : listenerListCopy) {
            notifyListener(config);
        }
        int i = 0;
        while(configToAdd != null && i < configToAdd.size()) {
            RChangeListenerConfig config = configToAdd.get(i);
            if(config.notifyIfAddRequestedDuringFire) {
                notifyListener(config);
            }
            i++;
        }
    }

    private void notifyListener(final RChangeListenerConfig config) {
        if(config.useEDT) {
            Runnable runnable = new Runnable() {
                public void run() {
                    config.listener.handle(event);
                }
            };
            if(config.edtSync) {
                GuiUtil.safeSync(runnable);
            } else {
                GuiUtil.safe(runnable);
            }
        } else {
            config.listener.handle(event);
        }
    }
}
