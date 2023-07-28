package replete.event.rnotif;


public class RChangeListenerConfig {


    ////////////
    // FIELDS //
    ////////////

    public boolean useEDT;
    public boolean edtSync;
    public RChangeListener listener;
    public boolean notifyIfAddRequestedDuringFire;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RChangeListenerConfig(RChangeListener listener) {
        this.listener = listener;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isUseEDT() {
        return useEDT;
    }
    public boolean isEdtSync() {
        return edtSync;
    }
    public boolean isNotifyIfAddRequestedDuringFire() {
        return notifyIfAddRequestedDuringFire;
    }

    // Mutators

    public RChangeListenerConfig setUseEDT(boolean useEDT) {
        this.useEDT = useEDT;
        return this;
    }
    public RChangeListenerConfig setEdtSync(boolean edtSync) {
        this.edtSync = edtSync;
        return this;
    }
    public RChangeListenerConfig setNotifyIfAddRequestedDuringFire(boolean notifyIfAddRequestedDuringFire) {
        this.notifyIfAddRequestedDuringFire = notifyIfAddRequestedDuringFire;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((listener == null) ? 0 : listener.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        RChangeListenerConfig other = (RChangeListenerConfig) obj;
        if(listener == null) {
            if(other.listener != null) {
                return false;
            }
        } else if(!listener.equals(other.listener)) {
            return false;
        }
        return true;
    }
}