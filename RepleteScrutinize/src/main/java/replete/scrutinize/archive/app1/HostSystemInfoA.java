package replete.scrutinize.archive.app1;

import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.Event;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import replete.errors.ExceptionUtil;
import replete.io.FileProperties;
import replete.io.FileUtil;
import replete.scrutinize.archive.app2.images.ScrutinizeImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.laf.LafManager;
import replete.ui.lay.Lay;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionMap;
import replete.ui.windows.Dialogs;
import replete.ui.windows.LoadingWindow;
import replete.ui.windows.escape.EscapeFrame;
import replete.util.Application;
import replete.util.User;

public class HostSystemInfoA extends EscapeFrame {
    // ALSO http://stackoverflow.com/questions/25552/using-java-to-get-os-level-system-information
    // ALSO http://support.hyperic.com/display/SIGAR/Home

    private JTabbedPane tabs;
    public HostSystemInfoA() {
        super("Host System Information");
        setIcon(ScrutinizeImageModel.SCRUTINIZE_ICON);

//        HSIActions actions = new HSIActions();

//        JMenuBar bar = new UIActionMenuBar(actions);
//        bar.add(LafManager.createLafMenu());
//        setJMenuBar(bar);
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice gd = ge.getDefaultScreenDevice();
        tabs = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
//        tabs.addTab("Mouse", createTab(MouseInfo.class));
//        tabs.addTab("SysCL", createTab(ClassLoader.getSystemClassLoader()));
//        try {
//            tabs.addTab("AllClasses", createTab(ClassUtil.findAll()));
//        } catch(Exception e1) {
//            e1.printStackTrace();
//            tabs.addTab("AllClasses", createTab("ERROR"));
//        }
//        try {
//            tabs.addTab("Robot", createTab(new Robot()));
//        } catch(AWTException e1) {
//            e1.printStackTrace();
//        }
//        tabs.addTab("MxCL", createTab(ManagementFactory.getClassLoadingMXBean()));
//        tabs.addTab("MxCm", createTab(ManagementFactory.getCompilationMXBean()));
//        tabs.addTab("MxGC", createTab(ManagementFactory.getGarbageCollectorMXBeans()));
//        tabs.addTab("MxMM", createTab(ManagementFactory.getMemoryManagerMXBeans()));
//        tabs.addTab("MxMe", createTab(ManagementFactory.getMemoryMXBean()));
//        tabs.addTab("MxMP", createTab(ManagementFactory.getMemoryPoolMXBeans()));
//        tabs.addTab("MxOS", createTab(ManagementFactory.getOperatingSystemMXBean()));
//        tabs.addTab("MxRT(*)", createTab(ManagementFactory.getRuntimeMXBean()));
//        tabs.addTab("MxTh", createTab(ManagementFactory.getThreadMXBean()));
//        try {
//            tabs.addTab("Network", createTab(NetworkInterface.getNetworkInterfaces()));
//        } catch(SocketException e) {
//            e.printStackTrace();
//        }
//        tabs.addTab("Disk", createTab(new File("")));
//        tabs.addTab("Runtime(*)", createTab(Runtime.getRuntime()));
//        tabs.addTab("System(*)", createTab(System.class));
//        tabs.addTab("Toolkit(*)", createTab(Toolkit.getDefaultToolkit()));
//        tabs.addTab("LocalGraphicsEnvironment", createTab(ge));
//        tabs.addTab("LGE.DefaultScreenDevice", createTab(gd));
//        List<GraphicsConfiguration> gds = new ArrayList<GraphicsConfiguration>();
//        for(GraphicsDevice gdv : ge.getScreenDevices()) {
//            gds.addAll(Arrays.asList(gdv.getConfigurations()));
//        }
//        tabs.addTab("gLGE.gDSD.gC[]", createTab(gds.toArray(new GraphicsConfiguration[0])));
//        tabs.addTab("gLGE.gDSD.gDM[]", createTab(gd.getDisplayModes()));
//        tabs.addTab("gLGE.gSD[]", createTab(ge.getScreenDevices()));
//        tabs.addTab("gLGE.getAllFonts[]", createTab(ge.getAllFonts()));
//        tabs.addTab("gLGE.getAvailableFontFamilyNames[]", createTab(ge.getAvailableFontFamilyNames()));
//        tabs.addTab("UIMgr.getInstalledLookAndFeels[]", createTab(UIManager.getInstalledLookAndFeels()));
//        if(UIManager.getAuxiliaryLookAndFeels() != null) {
//            tabs.addTab("UIMgr.getAuxiliaryLookAndFeels[]", createTab(UIManager.getAuxiliaryLookAndFeels()));
//        }
//        tabs.addTab("Window.getWindows", createTab(Window.getWindows()));
//        tabs.addTab("Window.getOwnerlessWindows", createTab(Window.getOwnerlessWindows()));
//        tabs.addTab("Frame.getFrames", createTab(Frame.getFrames()));

//        JButton btn = new JButton("Toggle Full Screen");
//        btn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent arg0) {
//                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//                GraphicsDevice gs = ge.getDefaultScreenDevice();
//                if(gs.getFullScreenWindow() == HostSystemInfo.this) {
//                    gs.setFullScreenWindow(null);
//                    HostSystemInfo.this.dispose();
//                    HostSystemInfo.this.setUndecorated(false);
//                } else {
//                    HostSystemInfo.this.dispose();
//                    HostSystemInfo.this.setUndecorated(true);
//                    gs.setFullScreenWindow(HostSystemInfo.this);
//                }
//                HostSystemInfo.this.setVisible(true);
//            }
//        });
//        JButton btnPixel = new RButton("&Get Pixel Color");
//        btnPixel.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(2000);
//                        } catch(InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        Robot robot = null;
//                        try {
//                            robot = new Robot();
//                        } catch(AWTException e) {
//                            e.printStackTrace();
//                        }
//                        PointerInfo pi = MouseInfo.getPointerInfo();
//                        Point loc = pi.getLocation();
//                        Color c = robot.getPixelColor(loc.x, loc.y);
//                        System.out.println(c);
//                        Dialogs.showMessage(HostSystemInfo.this, "Color: " + c);
//                    }
//                }.start();
//            }
//        });
//        JButton btnReload = new RButton("&Reload");
//        btnReload.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                reloadAppFrame();
//            }
//        });

//        String[] ss = new String[] {
//            "Disk+",
//            "Fonts",
//            "Memory",
//            "Network",
//            "Runtime+",
//            "System+",
//            "Toolkit+",
//        };

//        JList lst = new JList(ss);

        Lay.BLtg(this,
//            "W", Lay.sp(lst, "pref=[100,100],augb=eb(10tlb)"),
            "C", Lay.BL("C", tabs, "eb=10"),
//            "S", Lay.FL("R", /btnPixel, btnReload, btn),
            "size=[800,850],center"
        );
    }

    private Component createTab(Object obj) {
        System.gc();
        Wrapper[] w;
        if(obj.getClass().isArray()) {
            Object[] arr = (Object[]) obj;
            w = new Wrapper[arr.length];
            for(int o = 0; o < arr.length; o++) {
                w[o] = new Wrapper(arr[o]);
            }
        } else if(obj instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) obj;
            w = new Wrapper[col.size()];
            int o = 0;
            for(Object elem : col) {
                w[o++] = new Wrapper(elem);
            }
        } else if(obj instanceof Enumeration<?>) {
            Enumeration<?> col = (Enumeration<?>) obj;
            List<Wrapper> ws = new ArrayList<Wrapper>();
            while(col.hasMoreElements()) {
                Object nif = col.nextElement();
                ws.add(new Wrapper(nif));
            }
            w = ws.toArray(new Wrapper[0]);

        } else {
            w = new Wrapper[1];
            w[0] = new Wrapper(obj);
        }
        final JList lst = new JList(w);
        final JTextArea txt = new JTextArea();
        txt.setEditable(false);
        lst.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                Wrapper w = (Wrapper) lst.getSelectedValue();
                txt.setText(w.list());
                if(w.obj instanceof Font) {
                    Font newF = ((Font) w.obj).deriveFont(14F);
                    txt.setFont(newF);
                }
            }
        });
        lst.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getClickCount() > 1) {
                    Wrapper w = (Wrapper) lst.getSelectedValue();
                    Object o = w.obj;
                    if(o instanceof UIManager.LookAndFeelInfo) {
                        UIManager.LookAndFeelInfo laf = (UIManager.LookAndFeelInfo) o;
                        try {
                            UIManager.setLookAndFeel(laf.getClassName());
                        }
                        catch(Exception e1) {
                            Dialogs.showDetails(HostSystemInfoA.this,
                                "Error!", "Error!", ExceptionUtil.toCompleteString(e1, 4));
                        }
                        tabs.updateUI();
                    }
                }
            }
        });
        if(w.length > 0) {
            lst.setSelectedIndex(0);
            txt.setText(w[0].list());
        }
        return Lay.BL(
            "N", Lay.BL(
                "N", Lay.eb(new JLabel("Size: " + w.length), "10lt"),
                "C", Lay.augb(new JScrollPane(lst), Lay.eb("10"))
            ),
            "C", Lay.augb(Lay.sp(txt), Lay.eb("10blr"))
        );
    }
    private class Wrapper {
        private Object obj;
        public Wrapper(Object o) {
            obj = o;
        }
        @Override
        public String toString() {
            if(obj instanceof String) {
                return (String) obj;
            } else if(obj instanceof Class) {
                Class f = (Class) obj;
                return f.getName();
            } else if(obj instanceof Font) {
                Font f = (Font) obj;
                return f.getFontName();
            } else if(obj instanceof GraphicsEnvironment) {
                GraphicsEnvironment ge = (GraphicsEnvironment) obj;
                return ge.toString();
            } else if(obj instanceof GraphicsDevice) {
                GraphicsDevice gd = (GraphicsDevice) obj;
                return gd.toString();
            } else if(obj instanceof GraphicsConfiguration) {
                GraphicsConfiguration gc = (GraphicsConfiguration) obj;
                return gc.toString();
            } else if(obj instanceof BufferCapabilities) {        // Done
                BufferCapabilities bc = (BufferCapabilities) obj;
                return "FullScreenRequired=" + bc.isFullScreenRequired() + ",MultiBufferAvailable=" + bc.isMultiBufferAvailable() +
                                ",PageFlipping=" + bc.isPageFlipping();
            } else if(obj instanceof ImageCapabilities) {  // Done
                ImageCapabilities ic = (ImageCapabilities) obj;
                return "Accelerated=" + ic.isAccelerated() + ",TrueVolatile=" + ic.isTrueVolatile();
            } else if(obj instanceof DisplayMode) {  // Done
                DisplayMode dm = (DisplayMode) obj;
                return "(" + dm.getWidth() + "x" + dm.getHeight() + ") [BD=" + dm.getBitDepth() + ", HZ=" + dm.getRefreshRate() + "]";
            } else if(obj instanceof UIManager.LookAndFeelInfo) {
                UIManager.LookAndFeelInfo laf = (UIManager.LookAndFeelInfo) obj;
                return laf.getName();
            } else if(obj instanceof Class && ((Class) obj).getSimpleName().equals("System")) {
                return "System";
            } else if(obj instanceof MemoryUsage) {
                MemoryUsage u = (MemoryUsage) obj;
                return "init=" + rd(u.getInit()) + ", committed=" + rd(u.getCommitted()) + ", used=" + rd(u.getUsed()) + ", max=" + rd(u.getMax());
            } else if(obj instanceof NetworkInterface) {
                NetworkInterface u = (NetworkInterface) obj;
                return u.getDisplayName();
            } else if(obj ==  null) {
                return "null";
            }
            return obj.getClass().getSimpleName();
        }
        public String list() {
            String ret = "";
            if(obj instanceof String) {
                return (String) obj;
//            } else if(obj instanceof Robot) {
//                Robot r = (Robot) obj;
//                ret += "getAutoDelay: " + r.getAutoDelay() + "\n";
//                ret += "isAutoWaitForIdle: " + r.isAutoWaitForIdle() + "\n";
//                ret += "toString: " + r + "\n";
//            } else if(obj instanceof Font) {
//                Font f = (Font) obj;
//                ret += "AvailableAttributes: " +
//                                Arrays.toString(f.getAvailableAttributes())
//                                .replaceAll("java.awt.font.TextAttribute\\(", "")
//                                .replaceAll("\\)", "") + "\n";
//                ret += "Family: " + f.getFamily() + "\n";
//                ret += "FontName: " + f.getFontName() + "\n";
//                ret += "LayoutAttributes: " + f.hasLayoutAttributes() + "\n";
//                ret += "UniformLineMetrics: " + f.hasUniformLineMetrics() + "\n";
//                ret += "ItalicAngle: " + f.getItalicAngle() + "\n";
//                ret += "Bold: " + f.isBold() + "\n";
//                ret += "Italic: " + f.isItalic() + "\n";
//                ret += "Plain: " + f.isPlain() + "\n";
//                ret += "Transformed: " + f.isTransformed() + "\n";
//                ret += "Name: " + f.getName() + "\n";
//                ret += "NumGlyphs: " + f.getNumGlyphs() + "\n";
//                ret += "Size: " + f.getSize() + "\n";
//                ret += "Style: " + f.getStyle() + "\n";
//                ret += "ToString: " + f + "\n";
//            } else if(obj instanceof GraphicsEnvironment) {
//                GraphicsEnvironment ge = (GraphicsEnvironment) obj;
//                ret += "CenterPoint: " + ge.getCenterPoint() + "\n";
//                ret += "MaximumWindowBounds: " + ge.getMaximumWindowBounds() + "\n";
//                ret += "Headless: " + GraphicsEnvironment.isHeadless() + "\n";
//                ret += "HeadlessInstance: " + ge.isHeadlessInstance() + "\n";
//            } else if(obj instanceof GraphicsDevice) {
//                GraphicsDevice gd = (GraphicsDevice) obj;
//                ret += "AvailableAcceleratedMemory: " + rd(gd.getAvailableAcceleratedMemory()) + "\n";
//                ret += "DefaultConfiguration: " + gd.getDefaultConfiguration() + "\n";
//                ret += "DisplayMode: " + new Wrapper(gd.getDisplayMode()).toString() + "\n";
//                ret += "FullScreenWindow: " + gd.getFullScreenWindow() + "\n";
//                ret += "IDstring: " + gd.getIDstring() + "\n";
//                ret += "Type: " + gd.getType() + "\n";
//                ret += "DisplayChangeSupported: " + gd.isDisplayChangeSupported() + "\n";
//                ret += "FullScreenSupported: " + gd.isFullScreenSupported() + "\n";
//            } else if(obj instanceof GraphicsConfiguration) {
//                GraphicsConfiguration gc = (GraphicsConfiguration) obj;
//                ret += "Bounds: " + gc.getBounds() + "\n";
//                ret += "BufferCapabilities: " + new Wrapper(gc.getBufferCapabilities()) + "\n";
//                ret += "ColorModel: " + gc.getColorModel() + "\n";
//                ret += "DefaultTransform: " + gc.getDefaultTransform() + "\n";
//                ret += "Device: " + gc.getDevice() + "\n";
//                ret += "ImageCapabilities: " + new Wrapper(gc.getImageCapabilities()) + "\n";
//                ret += "NormalizingTransform: " + gc.getNormalizingTransform() + "\n";
//            } else if(obj instanceof DisplayMode) {
//                DisplayMode dm = (DisplayMode) obj;
//                ret += "BitDepth: " + dm.getBitDepth() + "\n";
//                ret += "Height: " + dm.getHeight() + "\n";
//                ret += "RefreshRate: " + dm.getRefreshRate() + " hz\n";
//                ret += "Width: " + dm.getWidth() + "\n";
//            } else if(obj instanceof UIManager.LookAndFeelInfo) {
//                UIManager.LookAndFeelInfo laf = (UIManager.LookAndFeelInfo) obj;
//                ret += "ClassName: " + laf.getClassName() + "\n";
//                ret += "Name: " + laf.getName() + "\n";
//                ret += "ToString: " + laf + "\n";
//            } else if(obj instanceof Toolkit) {
//                Toolkit tk = (Toolkit) obj;
//                ret += "AWTEventListeners#: " + tk.getAWTEventListeners().length + "\n";
//                ret += "ColorModel: " + tk.getColorModel() + "\n";
//                ret += "DesktopProperty: " + tk.getDesktopProperty("awt.font.desktophints") + "\n";
//                ret += "LockingKeyState#VK_CAPS_LOCK: " + tk.getLockingKeyState(KeyEvent.VK_CAPS_LOCK) + "\n";
//                try {
//                    ret += "LockingKeyState#VK_NUM_LOCK: " + tk.getLockingKeyState(KeyEvent.VK_NUM_LOCK) + "\n";
//                } catch(Exception e) {
//                    ret += "LockingKeyState#VK_NUM_LOCK: N/A\n";
//                }
//                try {
//                    ret += "LockingKeyState#VK_SCROLL_LOCK: " + tk.getLockingKeyState(KeyEvent.VK_SCROLL_LOCK) + "\n";
//                } catch(Exception e) {
//                    ret += "LockingKeyState#VK_SCROLL_LOCK: N/A\n";
//                }
//                try {
//                    ret += "LockingKeyState#VK_KANA_LOCK: " + tk.getLockingKeyState(KeyEvent.VK_KANA_LOCK) + "\n";
//                } catch(Exception e) {
//                    ret += "LockingKeyState#VK_KANA_LOCK: N/A\n";
//                }
//                ret += "MaximumCursorColors: " + tk.getMaximumCursorColors() + "\n";
//                ret += "MenuShortcutKeyMask: " + tk.getMenuShortcutKeyMask() + " (" + maskStr(tk.getMenuShortcutKeyMask()) + ")\n";
//                ret += "ScreenInsets: " + tk.getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()) + "\n";
//                ret += "ScreenResolution: " + tk.getScreenResolution() + " dpi\n";
//                ret += "ScreenSize: " + tk.getScreenSize() + "\n";
//                ret += "SystemClipboard: " + tk.getSystemClipboard() + "\n";
//                ret += "SystemSelection: " + tk.getSystemSelection() + "\n";
//                ret += "AlwaysOnTopSupported: " + tk.isAlwaysOnTopSupported() + "\n";
//                ret += "DynamicLayoutActive: " + tk.isDynamicLayoutActive() + "\n";
//                ret += "FrameStateSupported#ICONIFIED: " + tk.isFrameStateSupported(Frame.ICONIFIED) + "\n";
//                ret += "FrameStateSupported#MAXIMIZED_BOTH: " + tk.isFrameStateSupported(Frame.MAXIMIZED_BOTH) + "\n";
//                ret += "FrameStateSupported#MAXIMIZED_HORIZ: " + tk.isFrameStateSupported(Frame.MAXIMIZED_HORIZ) + "\n";
//                ret += "FrameStateSupported#MAXIMIZED_VERT: " + tk.isFrameStateSupported(Frame.MAXIMIZED_VERT) + "\n";
//                ret += "FrameStateSupported#NORMAL: " + tk.isFrameStateSupported(Frame.NORMAL) + "\n";
//                ret += "ModalExclusionTypeSupported#APPLICATION_EXCLUDE: " + tk.isModalExclusionTypeSupported(Dialog.ModalExclusionType.APPLICATION_EXCLUDE) + "\n";
//                ret += "ModalExclusionTypeSupported#NO_EXCLUDE: " + tk.isModalExclusionTypeSupported(Dialog.ModalExclusionType.NO_EXCLUDE) + "\n";
//                ret += "ModalExclusionTypeSupported#TOOLKIT_EXCLUDE: " + tk.isModalExclusionTypeSupported(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE) + "\n";
//                ret += "ModalityTypeSupported#APPLICATION_MODAL: " + tk.isModalityTypeSupported(Dialog.ModalityType.APPLICATION_MODAL) + "\n";
//                ret += "ModalityTypeSupported#DOCUMENT_MODAL: " + tk.isModalityTypeSupported(Dialog.ModalityType.DOCUMENT_MODAL) + "\n";
//                ret += "ModalityTypeSupported#MODELESS: " + tk.isModalityTypeSupported(Dialog.ModalityType.MODELESS) + "\n";
//                ret += "ModalityTypeSupported#TOOLKIT_MODAL: " + tk.isModalityTypeSupported(Dialog.ModalityType.TOOLKIT_MODAL) + "\n";
//            } else if(obj instanceof Class && ((Class) obj).getSimpleName().equals("MouseInfo")) {
//                ret += "NumberOfButtons: " + MouseInfo.getNumberOfButtons() + "\n";
//                ret += "PointerInfo.getDevice: " + MouseInfo.getPointerInfo().getDevice() + "\n";
//                ret += "PointerInfo.getLocation: " + MouseInfo.getPointerInfo().getLocation() + "\n";
//            } else if(obj instanceof Class && ((Class) obj).getSimpleName().equals("System")) {
//                ret += "Console?: " + (System.console() != null) + "\n";
//                ret += "CurrentTimeMillis: " + System.currentTimeMillis() + " ms\n";
//                ret += "Environment:\n";
//                Map<String, String> env = System.getenv();
//                for(String key : env.keySet()) {
//                    ret += "    " + key + " = " + env.get(key) + "\n";
//                }
//                ret += "Properties:\n";
//                Properties p = System.getProperties();
//                Set<Object> keys = p.keySet();
//                for(Object key : keys) {
//                    ret += "    " + key + " = " + p.getProperty((String) key) + "\n";
//                }
//                ret += "NanoTime: " + System.nanoTime() + " ns\n";
//            } else if(obj instanceof Runtime) {
//                Runtime rt = (Runtime) obj;
//                ret += "AvailableProcessors: " + rt.availableProcessors() + "\n";
//                ret += "FreeMemory: " + rd(rt.freeMemory()) + "\n";
//                ret += "MaxMemory: " + rd(rt.maxMemory()) + "\n";
//                ret += "TotalMemory: " + rd(rt.totalMemory()) + "\n";
//
//            } else if(obj instanceof File) {
//                File f = (File) obj;
//                ret += "WorkingDir of this app: " + f.getAbsolutePath() + "\n";
//                FileSystemView fsv = FileSystemView.getFileSystemView();
//                File[] roots = fsv.getRoots();
//                ret += "DefaultDirectory: " + fsv.getDefaultDirectory() + "\n";
//                ret += "HomeDirectory: " + fsv.getHomeDirectory() + "\n";
//                ret += "All Roots: " + Arrays.toString(roots) + " (listRoots=" + Arrays.toString(File.listRoots()) + ")\n";
//                for (File root : roots) {
//                    ret += "Root Detail: " + root.getAbsolutePath() + "\n";
//                    ret += "    FreeSpace: " + FileUtil.getReadableSizeString(root.getFreeSpace()) + "\n";
//                    ret += "    TotalSpace: " + FileUtil.getReadableSizeString(root.getTotalSpace()) + "\n";
//                    ret += "    UsableSpace: " + FileUtil.getReadableSizeString(root.getUsableSpace()) + "\n";
//                    ret += "    DisplayName: " + fsv.getSystemDisplayName(root) + "\n";
//                    ret += "    Drive: " + root + "\n";
//                    ret += "    Readable: " + root.canRead() + "\n";
//                    ret += "    Writable: " + root.canWrite() + "\n";
//                }

//            } else if(obj instanceof ClassLoadingMXBean) {
//                ClassLoadingMXBean os = (ClassLoadingMXBean) obj;
//                ret += "LoadedClassCount: " + os.getLoadedClassCount() + "\n";
//                ret += "TotalLoadedClassCount: " + os.getTotalLoadedClassCount() + "\n";
//                ret += "UnloadedClassCount: " + os.getUnloadedClassCount() + "\n";
//                ret += "Verbose: " + os.isVerbose() + "\n";

//            } else if(obj instanceof CompilationMXBean) {
//                CompilationMXBean os = (CompilationMXBean) obj;
//                ret += "Name: " + os.getName() + "\n";
//                ret += "TotalCompilationTime: " + os.getTotalCompilationTime() + " ms\n";
//                ret += "CompilationTimeMonitoringSupported: " + os.isCompilationTimeMonitoringSupported() + "\n";

//            } else if(obj instanceof GarbageCollectorMXBean) {
//                GarbageCollectorMXBean os = (GarbageCollectorMXBean) obj;
//                ret += "getCollectionCount: " + os.getCollectionCount() + "\n";
//                ret += "getCollectionTime: " + os.getCollectionTime() + "\n";
//                ret += "Name: " + os.getName() + "\n";
//                ret += "getMemoryPoolNames: " + Arrays.toString(os.getMemoryPoolNames()) + "\n";
//                ret += "isValid: " + os.isValid() + "\n";
//
//            } else if(obj instanceof MemoryManagerMXBean) {
//                MemoryManagerMXBean os = (MemoryManagerMXBean) obj;
//                ret += "Name: " + os.getName() + "\n";
//                ret += "getMemoryPoolNames: " + Arrays.toString(os.getMemoryPoolNames()) + "\n";
//                ret += "isValid: " + os.isValid() + "\n";

//            } else if(obj instanceof MemoryMXBean) {
//                MemoryMXBean os = (MemoryMXBean) obj;
//                ret += "ObjectPendingFinalizationCount: " + os.getObjectPendingFinalizationCount() + "\n";
//                ret += "HeapMemoryUsage: " + new Wrapper(os.getHeapMemoryUsage()) + "\n";
//                ret += "NonHeapMemoryUsage: " + new Wrapper(os.getNonHeapMemoryUsage()) + "\n";
//                ret += "Verbose: " + os.isVerbose() + "\n";
//                os.getObjectName();

//            } else if(obj instanceof MemoryPoolMXBean) {
//                MemoryPoolMXBean os = (MemoryPoolMXBean) obj;
//                if(os.isCollectionUsageThresholdSupported()) {
//                    ret += "CollectionUsageThreshold: " + rd(os.getCollectionUsageThreshold()) + "\n";
//                    ret += "CollectionUsageThresholdCount: " + os.getCollectionUsageThresholdCount() + "\n";
//                    ret += "CollectionUsageThresholdExceeded: " + os.isCollectionUsageThresholdExceeded() + "\n";
//                }
//                ret += "Name: " + os.getName() + "\n";
//                ret += "CollectionUsage: " + new Wrapper(os.getCollectionUsage()) + "\n";
//                ret += "MemoryManagerNames: " + Arrays.toString(os.getMemoryManagerNames()) + "\n";
//                ret += "PeakUsage: " + new Wrapper(os.getPeakUsage()) + "\n";
//                ret += "Type: " + os.getType() + "\n";
//                ret += "Usage: " + new Wrapper(os.getUsage()) + "\n";
//                ret += "CollectionUsageThresholdSupported: " + os.isCollectionUsageThresholdSupported() + "\n";
//                if(os.isUsageThresholdSupported()) {
//                    ret += "UsageThreshold: " + rd(os.getUsageThreshold()) + "\n";
//                    ret += "UsageThresholdCount: " + os.getUsageThresholdCount() + "\n";
//                    ret += "UsageThresholdExceeded: " + os.isUsageThresholdExceeded() + "\n";
//                }
//                ret += "UsageThresholdSupported: " + os.isUsageThresholdSupported() + "\n";
//                ret += "Valid: " + os.isValid() + "\n";

//            } else if(obj instanceof RuntimeMXBean) {
//                RuntimeMXBean os = (RuntimeMXBean) obj;
//                ret += "BootClassPath: " + os.getBootClassPath() + "\n";
//                ret += "ClassPath: " + os.getClassPath() + "\n";
//                ret += "LibraryPath: " + os.getLibraryPath() + "\n";
//                ret += "ManagementSpecVersion: " + os.getManagementSpecVersion() + "\n";
//                ret += "Name: " + os.getName() + "\n";
//                ret += "SpecName: " + os.getSpecName() + "\n";
//                ret += "SpecVendor: " + os.getSpecVendor() + "\n";
//                ret += "SpecVersion: " + os.getSpecVersion() + "\n";
//                ret += "StartTime: " + os.getStartTime() + " ms\n";
//                ret += "Uptime: " + os.getUptime() + " ms\n";
//                ret += "VmName: " + os.getVmName() + "\n";
//                ret += "VmVendor: " + os.getVmVendor() + "\n";
//                ret += "VmVersion: " + os.getVmVersion() + "\n";
//                ret += "InputArguments: " + os.getInputArguments() + "\n";
//                ret += "getSystemProperties: " + os.getSystemProperties() + "\n";
//                ret += "isBootClassPathSupported: " + os.isBootClassPathSupported() + "\n";

            } else if(obj instanceof ThreadMXBean) {
                ThreadMXBean os = (ThreadMXBean) obj;
//                ret += "CurrentThreadCpuTime: " + os.getCurrentThreadCpuTime() + " ns\n";
//                ret += "CurrentThreadUserTime: " + os.getCurrentThreadUserTime() + " ns\n";
//                ret += "DaemonThreadCount: " + os.getDaemonThreadCount() + "\n";
//                ret += "PeakThreadCount: " + os.getPeakThreadCount() + "\n";
//                ret += "ThreadCount: " + os.getThreadCount() + "\n";
//                ret += "ThreadCpuTime: " + os.getThreadCpuTime(Thread.currentThread().getId()) + " ns\n";
//                ret += "ThreadUserTime: " + os.getThreadUserTime(Thread.currentThread().getId()) + " ns\n";
//                ret += "TotalStartedThreadCount: " + os.getTotalStartedThreadCount() + "\n";
//                ret += "AllThreadIds: " + Arrays.toString(os.getAllThreadIds()) + "\n";
//                ret += "ThreadInfo: " + os.getThreadInfo(Thread.currentThread().getId()) + " (large object)\n";
//                ret += "CurrentThreadCpuTimeSupported: " + os.isCurrentThreadCpuTimeSupported() + "\n";
//                ret += "ObjectMonitorUsageSupported: " + os.isObjectMonitorUsageSupported() + "\n";
//                ret += "SynchronizerUsageSupported: " + os.isSynchronizerUsageSupported() + "\n";
//                ret += "ThreadContentionMonitoringEnabled: " + os.isThreadContentionMonitoringEnabled() + "\n";
//                ret += "ThreadContentionMonitoringSupported: " + os.isThreadContentionMonitoringSupported() + "\n";
//                ret += "ThreadCpuTimeEnabled: " + os.isThreadCpuTimeEnabled() + "\n";
//                ret += "ThreadCpuTimeSupported: " + os.isThreadCpuTimeSupported() + "\n";
                Thread[] t = new Thread[Thread.activeCount()];
                Thread.enumerate(t);
                ret += "*activeCount: " + Thread.activeCount() + "\n";
                ret += "*currentThread: " + Thread.currentThread().getId() + "/" + Thread.currentThread().getName() + "\n";
                for(Thread tt : t) {
                    ret += tt.getId() + " " + tt.getName() + " " + tt.getPriority() + " " + tt.getState() + " " +
                                    tt.getThreadGroup().getName() + " " + tt.getThreadGroup().getParent() +"\n";
                }
                // more could be done here.. but a better visualization is needed to properly show groups and threads.
//                ret += "\n";
//                for(Long l : os.getAllThreadIds()) {
//                    ThreadInfo ti = os.getThreadInfo(l);
//                    ret += "Thread #" + l + "\n";
//                    ret += "    BlockedCount: " + ti.getBlockedCount() + "\n";
//                    ret += "    BlockedTime: " + ti.getBlockedTime() + " ms\n";
//                    ret += "    LockName: " + ti.getLockName() + "\n";
//                    ret += "    LockOwnerId: " + ti.getLockOwnerId() + "\n";
//                    ret += "    LockOwnerName: " + ti.getLockOwnerName() + "\n";
//                    ret += "    LockedMonitors#: " + ti.getLockedMonitors().length + "\n";
//                    ret += "    LockedSynchronizers#: " + ti.getLockedSynchronizers().length + "\n";
//                    ret += "    LockInfo: " + ti.getLockInfo() + "\n";
//                    ret += "    StackTrace#: " + ti.getStackTrace().length+ "\n";
//                    ret += "    getThreadId: " + ti.getThreadId() + "\n";
//                    ret += "    getThreadName: " + ti.getThreadName() + "\n";
//                    ret += "    getThreadState: " + ti.getThreadState() + "\n";
//                    ret += "    getWaitedCount: " + ti.getWaitedCount() + "\n";
//                    ret += "    getWaitedTime: " + ti.getWaitedTime() + " ms\n";
//                    ret += "    isInNative: " + ti.isInNative() + "\n";
//                    ret += "    isSuspended: " + ti.isSuspended() + "\n";
//                }

//            } else if(obj instanceof OperatingSystemMXBean) {
//                OperatingSystemMXBean os = (OperatingSystemMXBean) obj;
//                ret += "Arch: " + os.getArch() + "\n";
//                ret += "AvailableProcessors: " + os.getAvailableProcessors() + "\n";
//                ret += "Name: " + os.getName() + "\n";
//                ret += "SystemLoadAverage: " + os.getSystemLoadAverage() + "\n";
//                ret += "Version: " + os.getVersion() + "\n";
//                if(obj instanceof UnixOperatingSystemMXBean) {
//                    UnixOperatingSystemMXBean uos = (UnixOperatingSystemMXBean) obj;
//                    ret += "CommittedVirtualMemorySize: " + rd(uos.getCommittedVirtualMemorySize()) + "\n";
//                    ret += "FreePhysicalMemorySize: " + rd(uos.getFreePhysicalMemorySize()) + "\n";
//                    ret += "FreeSwapSpaceSize: " + rd(uos.getFreeSwapSpaceSize()) + "\n";
//                    ret += "MaxFileDescriptorCount: " + uos.getMaxFileDescriptorCount() + "\n";
//                    ret += "OpenFileDescriptorCount: " + uos.getOpenFileDescriptorCount() + "\n";
//                    ret += "ProcessCpuTime: " + uos.getProcessCpuTime() + "\n";
//                    ret += "TotalPhysicalMemorySize: " + rd(uos.getTotalPhysicalMemorySize()) + "\n";
//                    ret += "TotalSwapSpaceSize: " + rd(uos.getTotalSwapSpaceSize()) + "\n";
//                    ret += "UsedPhysicalMemorySize: " + rd(uos.getTotalPhysicalMemorySize() - uos.getFreePhysicalMemorySize()) + "\n";
//                    ret += "PM: " + PerformanceMonitor.getCpuUsage() + "\n";
//                    ret += "PM: " + PerformanceMonitor.getCpuUsage() + "\n";
//                    ret += "PM: " + PerformanceMonitor.getCpuUsage() + "\n";
//                    ret += "PM: " + PerformanceMonitor.getCpuUsage() + "\n";
//                    ret += "PM: " + PerformanceMonitor.getCpuUsage() + "\n";
//                    ret += "PM: " + PerformanceMonitor.getCpuUsage() + "\n";
//                }

//            } else if(obj instanceof NetworkInterface) {
//                try {
//                    ret += getInterfaceInfo((NetworkInterface) obj).toString() + "\n";
//                    ret += networkExample();
//                    java.net.InetAddress i = java.net.InetAddress.getLocalHost();
//                    ret += i + "\n";                  // name and IP address
//                    ret += i.getHostName() + "\n";    // name
//                    ret += i.getHostAddress() + "\n"; // IP address only
//                } catch(IOException e) {
//                    e.printStackTrace();
//                }

//            } else if(obj instanceof ClassLoader) {
//                ClassLoader cl = (ClassLoader) obj;
//                if(cl instanceof URLClassLoader) {
//                    URL[] urls = ((URLClassLoader) cl).getURLs();
//                    for(URL url : urls) {
//                        ret += "URL: " + url + "\n";
//                    }
//
//                    try {
//                        Field f = ClassLoader.class.getDeclaredField("classes");
//                        f.setAccessible(true);
//                        Vector classes = (Vector) f.get(cl);
//                        ret += "# classes loaded so far: " + classes.size() + "\n";
//                        for(Object o : classes) {
//                            ret += "Classes: " + o + "\n";
//                        }
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }

//            } else if(obj instanceof Window) {
//                Window win = (Window) obj;
//                ret += "DefaultModalityType=" + Dialog.DEFAULT_MODALITY_TYPE + "\n";
//                ret += "Active=" + win.isActive() + "\n";
//                ret += "AlwaysOnTop=" + win.isAlwaysOnTop() + "\n";
//                ret += "AlwaysOnTopSupported=" + win.isAlwaysOnTopSupported() + "\n";
//                ret += "FocusableWindow=" + win.isFocusableWindow() + "\n";
//                ret += "FocusCycleRoot=" + win.isFocusCycleRoot() + "\n";
//                ret += "Focused=" + win.isFocused() + "\n";
//                ret += "LocationByPlatform=" + win.isLocationByPlatform() + "\n";
//                ret += "Showing=" + win.isShowing() + "\n";
//                // much more .. x,y, contained components, widht/height,
//                // container/component stuff
//
//                if(obj instanceof Frame) {
//                    Frame frame = (Frame) obj;
//                    ret += "Frame: " + obj.getClass().getSimpleName() + "\n";
//                    ret += "Title=" + frame.getTitle() + "\n";
//                } else if(obj instanceof Dialog) {
//                    Dialog frame = (Dialog) obj;
//                    ret += "Dialog: " + obj.getClass().getSimpleName() + "\n";
//                    ret += "Title=" + frame.getTitle() + "\n";
//                }
//            } else if(obj instanceof Class) {
//                Class f = (Class) obj;
//
//                ret += "getAnnotations: " + Arrays.toString(f.getAnnotations()) + "\n";
//                ret += "getCanonicalName: " + f.getCanonicalName() + "\n";
//                ret += "getModifiers: " + Modifier.toString(f.getModifiers()) + "\n";
//                ret += "getName: " + f.getName() + "\n";
//                ret += "getSimpleName: " + f.getSimpleName() + "\n";
//                ret += "toString: " + f.toString() + "\n";
//                ret += "desiredAssertionStatus: " + f.desiredAssertionStatus() + "\n";
//                ret += "getClasses: " + Arrays.toString(f.getClasses()) + "\n";
//                ret += "getClassLoader: " + f.getClassLoader() + "\n";
//                ret += "getComponentType: " + f.getComponentType() + "\n";
//                ret += "getConstructors: " + Arrays.toString(f.getConstructors()) + "\n";
//                ret += "getDeclaredAnnotations: " + Arrays.toString(f.getDeclaredAnnotations()) + "\n";
//                ret += "getDeclaredClasses: " + Arrays.toString(f.getDeclaredClasses()) + "\n";
//                ret += "getDeclaredConstructors: " + Arrays.toString(f.getDeclaredConstructors()) + "\n";
//                ret += "getMethods: " + Arrays.toString(f.getMethods()) + "\n";
//                ret += "getDeclaredMethods: " + Arrays.toString(f.getDeclaredMethods()) + "\n";
//                ret += "getFields: " + Arrays.toString(f.getFields()) + "\n";
//                ret += "getDeclaredFields: " + Arrays.toString(f.getDeclaredFields()) + "\n";
//                ret += "getDeclaringClass: " + f.getDeclaringClass() + "\n";
//                ret += "getEnclosingClass: " + f.getEnclosingClass() + "\n";
//                ret += "getEnclosingConstructor: " + f.getEnclosingConstructor() + "\n";
//                ret += "getEnclosingMethod: " + f.getEnclosingMethod() + "\n";
//                ret += "getEnumConstants: " + Arrays.toString(f.getEnumConstants()) + "\n";
//                ret += "getGenericInterfaces: " + Arrays.toString(f.getGenericInterfaces()) + "\n";
//                ret += "getGenericSuperclass: " + f.getGenericSuperclass() + "\n";
//                ret += "getInterfaces: " + Arrays.toString(f.getInterfaces()) + "\n";
//                ret += "getPackage: " + f.getPackage() + "\n";
//                ret += "getProtectionDomain: " + f.getProtectionDomain() + "\n";
//                ret += "getSigners: " + Arrays.toString(f.getSigners()) + "\n";
//                ret += "getSuperclass: " + f.getSuperclass() + "\n";
//                ret += "getTypeParameters: " + Arrays.toString(f.getTypeParameters()) + "\n";
//                ret += "isAnnotation: " + f.isAnnotation() + "\n";
//                ret += "isAnonymousClass: " + f.isAnonymousClass() + "\n";
//                ret += "isArray: " + f.isArray() + "\n";
//                ret += "isEnum: " + f.isEnum() + "\n";
//                ret += "isInterface: " + f.isInterface() + "\n";
//                ret += "isLocalClass: " + f.isLocalClass() + "\n";
//                ret += "isMemberClass: " + f.isMemberClass() + "\n";
//                ret += "isPrimitive: " + f.isPrimitive() + "\n";
//                ret += "isSynthetic: " + f.isSynthetic() + "\n";

            } else {
                ret = obj.getClass().getSimpleName() + " " + obj.getClass().getName();
            }
            return ret;
        }
    }
    private String rd(long bytes) {
        return FileUtil.getReadableSizeString(bytes);
    }
    private String maskStr(int code) {
        if(code == Event.CTRL_MASK) {
            return "CTRL";
        } else if(code == Event.ALT_MASK) {
            return "ALT";
        } else if(code == Event.META_MASK) {
            return "META";
        }
        return "?";
    }

//    private static String networkExample() {
//        Enumeration<NetworkInterface> ee;
//        String s = "";
//        try {
//            ee = NetworkInterface.getNetworkInterfaces();
//            while(ee.hasMoreElements()) {
//                NetworkInterface ni = ee.nextElement();
//                s += (ni.getDisplayName() + " " +
//                                toHexString(ni.getHardwareAddress()) + " " +
//                                ni.getName() + " " +
//                                ni.isUp() + " " +
//                                ni.getMTU()) + "\n";

//                Enumeration<InetAddress> ips = ni.getInetAddresses();
//                while(ips.hasMoreElements()) {
//                    s += ("  InetAddresses: " + ips.nextElement()) + "\n";
//                }
//                Enumeration<NetworkInterface> ss = ni.getSubInterfaces();
//                while(ss.hasMoreElements()) {
//                    NetworkInterface ni2 = ss.nextElement();
//                    s += ("  SubInterfaces: " + ni2.getName()) + "\n";
//                }
//                List<InterfaceAddress> blah = ni.getInterfaceAddresses();
//                for(InterfaceAddress ia : blah) {
//                    s += ("  InterfaceAddresses: " + ia) + "\n";
//                }
//            }
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//        return s;
//    }

    private static final String NL = System.getProperty("line.separator");
    private static final String NL_TAB = NL + "  ";
    private static final String IPV4 = "IPv4";
    private static final String IPV6 = "IPv6";

    private static class InterfaceInfo {
        public String displayName;
        public String name;
        public int mtu;
        public boolean isUp;
        public boolean isLoopback;
        public boolean isPointToPoint; // e.g. a PPP modem interface
        public boolean isVirtual; // a sub-interface
        public boolean supportsMulticast;
        public byte[] macAddress;
        public List<IpAddressInfo> ipAddresses;
        public List<InterfaceInfo> subInterfaces;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(NL);
            sb.append("*** Interface [" + name + "] ***").append(NL);

            sb.append(NL).append("display name  : " + displayName);
            sb.append(NL).append("MTU           : " + mtu);
            sb.append(NL).append("loopback      : " + isLoopback);
            sb.append(NL).append("point to point: " + isPointToPoint);
            sb.append(NL).append("up            : " + isUp);
            sb.append(NL).append("virtual       : " + isVirtual);
            sb.append(NL).append("multicast     : " + supportsMulticast);

            sb.append(NL).append("HW address    : ");
            if(macAddress != null) {
                for(byte b : macAddress) {
                    sb.append(String.format("%1$02X ", b));
                }
            } else {
                sb.append("n/a");
            }
            for(IpAddressInfo ipAddr : ipAddresses) {
                sb.append(ipAddr);
            }
            for(InterfaceInfo subInfo : subInterfaces) {
                sb.append(subInfo);
            }
            return sb.toString();
        }
    }

    public static String toHexString(byte[] bytes) {
        if(bytes == null) {
            return "";
        }
        char[] hexArray =
                    {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for(int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v / 16];
            hexChars[j * 2 + 1] = hexArray[v % 16];
        }
        return new String(hexChars);
    }

    private static class IpAddressInfo {
        public String ipAddress;
        public String ipVersion = "unknown";
        public String hostName;
        public String canonicalHostName;
        public boolean isLoopback;
        public boolean isSiteLocal; // private IP address
        public boolean isAnyLocal; // wildcard address
        public boolean isLinkLocal;
        public boolean isMulticast;
        public boolean isReachable;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(NL).append("INET address (" + ipVersion + "): " + ipAddress);
            sb.append(NL_TAB).append("host name           : " + hostName);
            sb.append(NL_TAB).append("canonical host name : " + canonicalHostName);
            sb.append(NL_TAB).append("loopback            : " + isLoopback);
            sb.append(NL_TAB).append("site local          : " + isSiteLocal);
            sb.append(NL_TAB).append("any local           : " + isAnyLocal);
            sb.append(NL_TAB).append("link local          : " + isLinkLocal);
            sb.append(NL_TAB).append("multicast           : " + isMulticast);
            sb.append(NL_TAB).append("reachable           : " + isReachable);

            return sb.toString();
        }
    }

//    private static InterfaceInfo getInterfaceInfo(NetworkInterface nif) throws IOException {
        // get interface information
//        InterfaceInfo info = new InterfaceInfo();
//        info.displayName = nif.getDisplayName();
//        info.name = nif.getName();
//        info.mtu = nif.getMTU();
//        info.isUp = nif.isUp();
//        info.isLoopback = nif.isLoopback();
//        info.isPointToPoint = nif.isPointToPoint();
//        info.isVirtual = nif.isVirtual();
//        info.supportsMulticast = nif.supportsMulticast();
//        info.macAddress = nif.getHardwareAddress();
//        info.ipAddresses = new ArrayList<IpAddressInfo>();
//        info.subInterfaces = new ArrayList<InterfaceInfo>();

        // get IP address information
//        Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
//        while(inetAddresses.hasMoreElements()) {
//            InetAddress inetAddr = inetAddresses.nextElement();
//            IpAddressInfo ipInfo = new IpAddressInfo();
//            if(inetAddr instanceof Inet4Address) {
//                ipInfo.ipVersion = IPV4;
//            } else if(inetAddr instanceof Inet6Address) {
//                ipInfo.ipVersion = IPV6;
//            }
//            ipInfo.ipAddress = inetAddr.getHostAddress();
//            ipInfo.hostName = inetAddr.getHostName();
//            ipInfo.canonicalHostName = inetAddr.getCanonicalHostName();
//            ipInfo.isAnyLocal = inetAddr.isAnyLocalAddress();
//            ipInfo.isLinkLocal = inetAddr.isLinkLocalAddress();
//            ipInfo.isSiteLocal = inetAddr.isSiteLocalAddress();
//            ipInfo.isLoopback = inetAddr.isLoopbackAddress();
//            ipInfo.isMulticast = inetAddr.isMulticastAddress();
//            ipInfo.isReachable = inetAddr.isReachable(5000);

//            info.ipAddresses.add(ipInfo);
//        }

//        // get virtual interface information
//        Enumeration<NetworkInterface> subIfs = nif.getSubInterfaces();
//        while(subIfs.hasMoreElements()) {
//            NetworkInterface subIf = subIfs.nextElement();
//            InterfaceInfo subInfo = getInterfaceInfo(subIf);
//            info.subInterfaces.add(subInfo);
//        }
//
//        return info;
//    }

    private static FileProperties props = new FileProperties(new File(User.getHome(), ".hsi0"));
    private static HostSystemInfoA appFrame;
    private static LoadingWindow frmReloading;

    public static void main(String[] args) {
        Application.setName("Host System Information");
        String lafClassName = props.getProperty("lookAndFeel");
        String lafTheme = props.getProperty("theme");
//        LafManager.initialize(lafClassName, lafTheme);

        createAppFrame();

//        LafManager.setNeedToRebootListener(new RebootFramesListener() {
//            public boolean allowReboot() {
//                return Dialogs.showConfirm("Allow reboot?");
//            }
//            public void reboot() {
//                reloadAppFrame();
//            }
//        });
    }

    private static void reloadAppFrame() {
        frmReloading = new LoadingWindow();
        frmReloading.setVisible(true);
        appFrame.dispose();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                createAppFrame();
            }
        }.start();
    }

    private static void createAppFrame() {
        appFrame = new HostSystemInfoA();
        appFrame.setVisible(true);
        appFrame.addClosingListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                props.setProperty("lookAndFeel", LafManager.getCurrentLaf().getCls());
                props.setProperty("theme", LafManager.getCurrentLaf().getCurTheme());
                props.save();
            }
        });
        if(frmReloading != null) {
            frmReloading.dispose();
        }
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class HSIActions extends UIActionMap {
        public HSIActions() {

            createAction("file")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setText("&File"));

            createAction("exit", (e, a) -> close())
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("file")
                        .setText("E&xit")
                        .setIcon(CommonConcepts.EXIT));
        }
    }
}