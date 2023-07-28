package replete.ui.laf;

import javax.swing.LookAndFeel;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import replete.text.ToStringFormatter;

//import com.jtattoo.plaf.AbstractLookAndFeel;
//import com.jtattoo.plaf.acryl.AcrylLookAndFeel;
//import com.jtattoo.plaf.aero.AeroLookAndFeel;
//import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
//import com.jtattoo.plaf.bernstein.BernsteinLookAndFeel;
//import com.jtattoo.plaf.fast.FastLookAndFeel;
//import com.jtattoo.plaf.graphite.GraphiteLookAndFeel;
//import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
//import com.jtattoo.plaf.luna.LunaLookAndFeel;
//import com.jtattoo.plaf.mcwin.McWinLookAndFeel;
//import com.jtattoo.plaf.mint.MintLookAndFeel;
//import com.jtattoo.plaf.noire.NoireLookAndFeel;
//import com.jtattoo.plaf.smart.SmartLookAndFeel;

public class Laf {


    ////////////
    // FIELDS //
    ////////////

    public static final String DEFAULT = "Default";

    private String cls;
    private String name;
    private String desc;
    private String id;
    private String curTheme;
    private String[] themeNames;
    private LookAndFeel inst;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Laf(String c) {
        cls = c;
    }

    @Override
    public String toString() {
        ToStringFormatter ts = new ToStringFormatter();
        return ts.render(this);
    }

    public boolean isWindowDecorationOn() {
        boolean decorated = false;
        if(inst instanceof MetalLookAndFeel) {
            decorated = true;
//        } else if(inst instanceof AbstractLookAndFeel) {
//            // The following code could be cleaner...
//            decorated = AbstractLookAndFeel.getTheme().isWindowDecorationOn();
        }
        return decorated;
    }

    private void updateAddlTheme() {

        // Would be nice to have this...
        //      if(laf instanceof AbstractLookAndFeel) {
        //          ((AbstractLookAndFeel) laf).setTheme(theme);
        //      }

        // BaseBorders.java paints logo in incorrect direction on Mac.
        String nm = ""; //Application.getName() == null ? "" : Application.getName();
//        if(inst instanceof FastLookAndFeel) {             FastLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof GraphiteLookAndFeel) {  GraphiteLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof SmartLookAndFeel) {     SmartLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof AcrylLookAndFeel) {     AcrylLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof AeroLookAndFeel) {      AeroLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof BernsteinLookAndFeel) { BernsteinLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof AluminiumLookAndFeel) { AluminiumLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof McWinLookAndFeel) {     McWinLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof MintLookAndFeel) {      MintLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof HiFiLookAndFeel) {      HiFiLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof NoireLookAndFeel) {     NoireLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof LunaLookAndFeel) {      LunaLookAndFeel.setTheme(curTheme, "", nm);
//        } else if(inst instanceof MetalLookAndFeel) {
//            if(curTheme.equals("Ocean")) {
//                MetalLookAndFeel.setCurrentTheme(new OceanTheme());
//            } else {
//                MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
//            }
//        }
        if(inst instanceof MetalLookAndFeel) {
            if(curTheme.equals("Ocean")) {
                MetalLookAndFeel.setCurrentTheme(new OceanTheme());
            } else {
                MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            }
        }
        // Theme doesn't affect any other LAF's.
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public String getCls() {
        return cls;
    }
    public void setCls(String cls) {
        this.cls = cls;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCurTheme() {
        return curTheme;
    }
    public void setCurTheme(String newTheme) {
        if(newTheme == null) {
            newTheme = DEFAULT;
        }

        boolean found = false;
        for(String theme : themeNames) {
            if(theme.equals(newTheme)) {
                found = true;
            }
        }
        if(!found) {
            newTheme = DEFAULT;
        }
        curTheme = newTheme;

        updateAddlTheme();
    }
    public String[] getThemeNames() {
        return themeNames;
    }
    public void setThemeNames(String[] themeNames) {
        this.themeNames = themeNames;
    }
    public LookAndFeel getInst() {
        return inst;
    }
    public void setInst(LookAndFeel inst) {
        this.inst = inst;
    }
}
