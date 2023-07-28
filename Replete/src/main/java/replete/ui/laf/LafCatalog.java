package replete.ui.laf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class LafCatalog {
    private Map<String, Laf> validLafs = new HashMap<>();

    public Map<String, Laf> getLafs() {
        return validLafs;
    }

    public LafCatalog() {
        List<Laf> potential = new ArrayList<>();
        potential.add(new Laf("javax.swing.plaf.metal.MetalLookAndFeel"));
        potential.add(new Laf("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"));
        potential.add(new Laf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"));
        potential.add(new Laf("com.sun.java.swing.plaf.motif.MotifLookAndFeel"));
        potential.add(new Laf("com.sun.java.swing.plaf.mac.MacLookAndFeel"));
        potential.add(new Laf("com.apple.laf.AquaLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.acryl.AcrylLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.aero.AeroLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.fast.FastLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.graphite.GraphiteLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.hifi.HiFiLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.luna.LunaLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.mcwin.McWinLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.mint.MintLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.noire.NoireLookAndFeel"));
//        potential.add(new Laf("com.jtattoo.plaf.smart.SmartLookAndFeel"));

        // Add any other LAF's that might be installed on the host,
        // but not present in the above list.
        LookAndFeelInfo[] lafis = UIManager.getInstalledLookAndFeels();
        for(LookAndFeelInfo lafi : lafis) {
            potential.add(new Laf(lafi.getClassName()));
        }

        for(Laf laf : potential) {

            // Skip if there already is a LAF instantiated for this class.
            if(validLafs.get(laf.getCls()) != null) {
                continue;
            }

            // Instantiate the object associated with the class.
            LookAndFeel lafInst;
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                Class<?> lafClass = Class.forName(laf.getCls(), true, loader);
                lafInst = (LookAndFeel) lafClass.newInstance();
            } catch(Exception e) {
                // Skip if cannot instantiate.
                continue;
            }

            // Skip if not supported on this platform.
            if(!lafInst.isSupportedLookAndFeel()) {
                continue;
            }

            // Set basic information.
            laf.setName(lafInst.getName());
            laf.setDesc(lafInst.getDescription());
            laf.setId(lafInst.getID());
            laf.setInst(lafInst);

            // Choose the themes for this LAF.
            /*if(laf.getInst() instanceof AbstractLookAndFeel) {
                AbstractLookAndFeel alaf = (AbstractLookAndFeel) laf.getInst();
                laf.setThemeNames(alaf.getThemes());
            } else*/ if(laf.getInst() instanceof MetalLookAndFeel) {
                laf.setThemeNames(new String[] {Laf.DEFAULT, "Ocean"});
            } else {
                laf.setThemeNames(new String[] {Laf.DEFAULT});
            }

            // A theme always starts out as in the "Default" theme.
            laf.setCurTheme(Laf.DEFAULT);

            validLafs.put(laf.getCls(), laf);
        }
    }

    public static void main(String[] args) {
        LafCatalog cat = new LafCatalog();
        for(Laf laf : cat.getLafs().values()) {
            System.out.println(laf);
            System.out.println("   " + Arrays.toString(laf.getThemeNames()));
        }
    }
}
