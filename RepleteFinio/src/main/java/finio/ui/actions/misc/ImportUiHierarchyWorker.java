package finio.ui.actions.misc;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;

import javax.swing.ImageIcon;

import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.util.ReflectionUtil;

public class ImportUiHierarchyWorker extends FWorker<Void, NonTerminal> {


    ////////////
    // FIELDS //
    ////////////

    private SelectionContext C;
    private NonTerminal Mcontext;
    private String K;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ImportUiHierarchyWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void gather() {
        C = wc.getWorldPanel().getSelectedView().getSelectedValue();
        Mcontext = (NonTerminal) C.getV();
        String Kproposed = (String) Mcontext.getNextAvailableKey(getName() + "-");
        K = getDesiredKeyName(Kproposed, Mcontext);
        return null;
    }

    @Override
    protected boolean proceed(Void gathered) {
        return K != null;
    }

    @Override
    protected NonTerminal background(Void gathered) throws Exception {
        NonTerminal M = traverseContainerHierarchy(ac.getSelectedWorld().getWorldPanel(), 0);
        Mcontext.put(K, M);

        select(
            new SelectRequest()
                .setContext(C)
                .setAction(SelectAction.CHILD)
                .setArgs(K)
        );
        expand(
            new ExpandRequest()
                .setContext(C)
                .setAction(SelectAction.CHILD)
                .setArgs(K)
        );

        return M;
    }

    @Override
    public String getActionVerb() {
        return "importing UI hierarchy";
    }


    ////////////
    // HELPER //
    ////////////

    private NonTerminal traverseContainerHierarchy(Container c, int level) {
        FMap M = FMap.A();

        int i = 0;
        for(Component cc : c.getComponents()) {
            if(cc instanceof Container) {
                NonTerminal Mchild = traverseContainerHierarchy((Container) cc, level + 1);
                M.put((i++) + ") " + cc.getClass().getSimpleName(), Mchild);
            } else {
                M.put((i++) + ") " + "--------" + cc.getClass().getSimpleName(), 2);
            }
        }

        FMap Mprop = FMap.A();

        M.put("Properties", Mprop);
        Mprop.put("x", c.getX());
        Mprop.put("y", c.getY());

        if(ReflectionUtil.hasMethod(c, "getText")) {
            Mprop.put("text", ReflectionUtil.invoke(c, "getText"));
        }

        if(ReflectionUtil.hasMethod(c, "getIcon")) {
            String str;
            ImageIcon img = (ImageIcon) ReflectionUtil.invoke(c, "getIcon");
            if(img == null) {
                str = null;
            } else {
                str = img.getIconWidth() + "x" + img.getIconHeight();
            }
            Mprop.put("icon", str);
        }

        if(ReflectionUtil.hasMethod(c, "getLayout")) {
            LayoutManager lm = (LayoutManager) ReflectionUtil.invoke(c, "getLayout");
            Mprop.put("layout", lm == null ? null : lm.getClass().getSimpleName());
        }

        Mprop.put("min", c.getMinimumSize());
        Mprop.put("max", c.getMaximumSize());
        Mprop.put("pref", c.getPreferredSize());
        return M;
    }
}
