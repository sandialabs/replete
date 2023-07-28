package replete.ui.params.hier;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JPanel;

import replete.params.hier.PropertyGroup;
import replete.params.hier.PropertyParams;
import replete.params.hier.PropertySet;
import replete.params.hier.PropertySetSpecification;
import replete.params.hier.PropertySlot;
import replete.plugins.Generator;
import replete.ui.BeanPanel;
import replete.ui.lay.Lay;
import replete.ui.validation.ValidationContext;

public class SingleGlobalPanel<T> extends BeanPanel<PropertyGroup<T>> {


    ////////////
    // FIELDS //
    ////////////

    private PropertySetSpecification spec;
    private Map<String, PropertyParamsPanel> panels = new LinkedHashMap<>();
    private PropertyGroup<T> rootNode;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SingleGlobalPanel(PropertySetSpecification spec) {
        this.spec = spec;
        Lay.BxLtg(this);
        int r = 0;
        for(String key : spec.getKeys()) {
            PropertySlot slot = spec.getSlot(key);
            Class<? extends PropertyParams> paramsClass = slot.getParamsClass();
            PropertyGenerator generator = Generator.lookup(paramsClass);
            PropertyParamsPanel pnlParams = generator.createParamsPanel();
            String desc = PropertyUtil.getDescription(slot, generator);
            Icon icon = PropertyUtil.getIcon(slot, generator);
            JPanel pnlWrapper = Lay.BL(
                "N", Lay.lb("<html><u>" + slot.getName() + ":</u>" + desc + "</html>", icon, "eb=5lrt"),
                "C", pnlParams
            );
            Lay.hn(pnlWrapper, "alignx=0,eb=10,dimh=100,opaque=true,chtransp");
            add(pnlWrapper);
            if(r % 2 == 1) {
                Lay.hn(pnlWrapper, "bg=248");
            }
            panels.put(key, pnlParams);
            r++;
        }
        add(Box.createVerticalGlue());
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public PropertyGroup<T> getRootNode() {
        return rootNode;
    }

    // Accessors (Computed)

    @Override
    public PropertyGroup<T> get() {
        PropertySet properties = rootNode.getProperties();
        for(String key : panels.keySet()) {
            PropertyParamsPanel pnl = panels.get(key);
            PropertyParams params = pnl.get();
            properties.put(key, params);
        }
        return rootNode;
    }

    // Mutators

    @Override
    public void set(PropertyGroup<T> rootNode) {
        this.rootNode = rootNode;
        PropertySet properties = rootNode.getProperties();
        for(String key : properties.keySet()) {
            PropertyParamsPanel pnl = panels.get(key);
            if(pnl == null) {
                throw new IllegalStateException();
            }
            PropertyParams params = properties.get(key);
            pnl.set(params);
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void validateInput(ValidationContext context) {
        for(String key : panels.keySet()) {
            PropertyParamsPanel pnl = panels.get(key);
            PropertyGenerator generator = Generator.lookup(pnl);
            context.check(generator.getName(), pnl);
        }
    }
}
