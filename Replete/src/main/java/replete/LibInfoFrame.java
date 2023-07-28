package replete;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;

import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.list.EmptyMessageList;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;
import replete.util.ClassUtil;


public class LibInfoFrame extends EscapeFrame {
    private EmptyMessageList lstMainClasses;

    public LibInfoFrame(String title, String msg) {
        super(title);
        setIcon(RepleteImageModel.REPLETE_LOGO);

        JButton btnLaunch = Lay.btn("&Launch", CommonConcepts.LAUNCH, (ActionListener) e -> launchClass());
        JButton btnClose  = Lay.btn("&Close",  CommonConcepts.CANCEL,    (ActionListener) e -> close());

        lstMainClasses = new EmptyMessageList("Loading Runnable Classes");
        lstMainClasses.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() > 1) {
                    launchClass();
                }
            }
        });
        lstMainClasses.addListSelectionListener(e -> {
            btnLaunch.setEnabled(lstMainClasses.getSelectedIndex() != -1);
        });

        Lay.BLtg(this,
            "N", Lay.BL(
                "C", Lay.lb(msg, "eb=10b,valign=top"),
                "augb=mb(1b,black),opaque=false"
            ),
            "C", Lay.BL(
                "N", Lay.lb(
                    "<html>Below is a list of all the classes in this library that contain a 'main' method.  " +
                    "The majority of these applications are merely test applications to demonstrate some library functionality, " +
                    "though there are a few full-fledged apps (note: many applications output to the console).</html>",
                    "eb=5b"
                ),
                "C", Lay.sp(lstMainClasses),
                "eb=10t,opaque=false"
            ),
            "S", Lay.FL("R",
                Lay.hn(btnLaunch, "enabled=false"),
                Lay.p(btnClose, "eb=5l"),
                "vgap=0,hgap=0,eb=10t,opaque=false"
            ),
            "size=[600,600],center,eb=10"
        );

        new Thread() {
            @Override
            public void run() {
                loadMainClasses();
            }
        }.start();
    }

    private void loadMainClasses() {
        try {
            Set<String> classNames = new TreeSet<>();
            for(Class<?> cls : ClassUtil.findAll()) {
                if(cls != LibInfo.class) {
                    try {
                        String[] args = new String[0];
                        Method m = cls.getDeclaredMethod("main", new Class[] { args.getClass() });
                        m.setAccessible(true);
                        int mods = m.getModifiers();
                        if(m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
                            continue;
                        }
                        classNames.add(cls.getName());
                    } catch(Exception nsme) {}
                }
            }
            lstMainClasses.setListData(classNames.toArray());
        } catch(Exception ex) {
            Dialogs.showDetails(this, "Could not load runnable classes.", "Error", ex);
        }
    }

    private void launchClass() {
        try {
            String[] args = new String[0];
            String className = (String) lstMainClasses.getSelectedValue();
            Class<?> cls = Class.forName(className);
            Method m = cls.getDeclaredMethod("main", new Class[] { args.getClass() });
            m.setAccessible(true);
            int mods = m.getModifiers();
            if(m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
                throw new NoSuchMethodException("main");
            }
            m.invoke(null, new Object[] { args });
        } catch(Exception ex) {
            Dialogs.showDetails(this, "Could not launch application.", "Error", ex);
        }
    }
}