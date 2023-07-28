package replete.scrutinize.archive.app2;

import java.awt.Dialog;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.filechooser.FileSystemView;

import replete.collections.ArrayUtil;
import replete.io.FileUtil;
import replete.util.DateUtil;


public class HsiInspector {
//    try {
//        Provider p[] = Security.getProviders();
//        for (int i = 0; i < p.length; i++) {
//            System.out.println(p[i]);
//            for (Enumeration e = p[i].keys(); e.hasMoreElements();) {
//              System.out.println("\t" + e.nextElement());
//          }
//        }
//      } catch (Exception e) {
//        System.out.println(e);
//      }

//    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
//        TrustManagerFactory.getDefaultAlgorithm());
//    trustManagerFactory.init((KeyStore)null);
//    System.out.println("JVM Default Trust Managers:");
//    for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
//        DebugUtil.printObjectDetails(trustManager);
//        if (trustManager instanceof X509TrustManager) {
//            X509TrustManager x509TrustManager = (X509TrustManager)trustManager;
//            System.out.println("\tAccepted issuers count : " + x509TrustManager.getAcceptedIssuers().length);
//        }
//    }
//
//    KeyStore ks = KeyStore.getInstance("JKS");
//    File caCerts = new File("C:\\Program Files\\Java\\jdk1.7.0_51\\jre\\lib\\security\\cacerts");
//    ks.load(new FileInputStream(caCerts), "changeit".toCharArray());
//    TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX", "SunJSSE");
//    tmf.init(ks);
//    TrustManager tms [] = tmf.getTrustManagers();
//    for (int i = 0; i < tms.length; i++) {
//        DebugUtil.printObjectDetails(tms[i]);
//        if (tms[i] instanceof X509TrustManager) {
//            System.out.println(" found default");
////            sunJSSEX509TrustManager = (X509TrustManager) tms[i];
//            return;
//        }
//    }


    public static Groups groups;
    public static class Groups extends TreeMap<String, Group> implements Serializable {
        private long computeTime;
        public long getComputeTime() {
            return computeTime;
        }
        public void setComputeTime(long computeTime) {
            this.computeTime = computeTime;
        }
    }
    public static class Group extends TreeMap<String, Property> implements Serializable {
        private String name;
        public Group(String nm) {
            name = nm;
        }
        public String getName() {
            return name;
        }
        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        inspectSystem();
        printResults();
    }

    public static Groups inspectSystem() {
        groups = new Groups();
        long T = System.currentTimeMillis();
        processDisk();
        processRuntime();
        processRuntime2();
        processSystem();
        processToolkit();
        groups.setComputeTime(System.currentTimeMillis() - T);
        return groups;
    }

    private static void printResults() {
        for(String groupName : groups.keySet()) {
            System.out.println("GROUP " + groupName + ">>>");
            for(Property val : groups.get(groupName).values()) {
                System.out.println("   " + val);
            }
        }
    }

    private static void processDisk() {

        // HOW PRESENT THIS INFORMATION IN UI?
        //   NEED SUB-MENU

        /*for (File root : roots) {
            ret += "Root Detail: " + root.getAbsolutePath() + "\n";
            ret += "    FreeSpace: " + FileUtil.getReadableSizeString(root.getFreeSpace()) + "\n";
            ret += "    TotalSpace: " + FileUtil.getReadableSizeString(root.getTotalSpace()) + "\n";
            ret += "    UsableSpace: " + FileUtil.getReadableSizeString(root.getUsableSpace()) + "\n";
            ret += "    DisplayName: " + fsv.getSystemDisplayName(root) + "\n";
            ret += "    Drive: " + root + "\n";
            ret += "    Readable: " + root.canRead() + "\n";
            ret += "    Writable: " + root.canWrite() + "\n";
        }*/
        File F = new File("");
        Class<?> F2 = File.class;
        FileSystemView F3 = FileSystemView.getFileSystemView();

        String codeP = "new File(\"\").";
        String codeP2 = "File.";
        String codeP3 = "FileSystemView.getFileSystemView().";

        String group = "Disk";

        addInf(group,
            "Working Directory of this Application",
            F.getAbsolutePath(),
            F, codeP + "getAbsolutePath()",
            ValueType.PATH);
        addInf(group,
            "File System Roots (File)",
            File.listRoots(),
            F2, codeP2 + "listRoots()",
            ValueType.PATH_LIST);
        addInf(group,
            "File System Roots (FileSystemView)",
            F3.getRoots(),
            F3, codeP3 + "getRoots()",
            ValueType.PATH_LIST);
        addInf(group,
            "Default Directory",
            F3.getDefaultDirectory(),
            F3, codeP3 + "getDefaultDirectory()",
            ValueType.PATH);
        addInf(group,
            "Home Directory",
            F3.getHomeDirectory(),
            F3, codeP3 + "getHomeDirectory()",
            ValueType.PATH);
    }

    private static void processRuntime() {
        Runtime R = Runtime.getRuntime();
        String codeP = "Runtime.getRuntime().";
        String group = "Runtime";

        addInf(group,
            "Available Processors",
            R.availableProcessors(),
            R, codeP + "availableProcessors()",
            ValueType.COUNT,
            "processors");
        addInf(group,
            "Free Memory",
            R.freeMemory(),
            R, codeP + "freeMemory()",
            ValueType.MEMORY_SIZE,
            "B");
        addInf(group,
            "Max Memory",
            R.maxMemory(),
            R, codeP + "maxMemory()",
            ValueType.MEMORY_SIZE,
            "B");
        addInf(group,
            "Total Memory",
            R.totalMemory(),
            R, codeP + "totalMemory()",
            ValueType.MEMORY_SIZE,
            "B");
        // other useful methods?
    }

    private static void processRuntime2() {
        RuntimeMXBean B = ManagementFactory.getRuntimeMXBean();
        String codeP = "ManagementFactory.getRuntimeMXBean().";
        String group = "Runtime2";

        addInf(group,
            "Boot Class Path Supported?",
            B.isBootClassPathSupported(),
            B, codeP + "isBootClassPathSupported()",
            ValueType.BOOLEAN);
        if(B.isBootClassPathSupported()) {
            addInf(group,
                "Boot Class Path",
                B.getBootClassPath(),
                B, codeP + "getBootClassPath()",
                ValueType.PATH_LIST);
        }
        addInf(group,
            "Class Path",
            B.getClassPath(),
            B, codeP + "getClassPath()",
            ValueType.PATH_LIST);
        addInf(group,
            "Input Arguments",
            B.getInputArguments(),
            B, codeP + "getInputArguments()",
            ValueType.VALUE_LIST);
        addInf(group,
            "Library Path",
            B.getLibraryPath(),
            B, codeP + "getLibraryPath()",
            ValueType.PATH_LIST);
        addInf(group,
            "Management Specification Version",
            B.getManagementSpecVersion(),
            B, codeP + "getManagementSpecVersion()",
            ValueType.VERSION);
        addInf(group,
            "Name",
            B.getName(),
            B, codeP + "getName()",
            ValueType.NAME);
        addInf(group,
            "Specification Name",
            B.getSpecName(),
            B, codeP + "getSpecName()",
            ValueType.NAME);
        addInf(group,
            "Specification Vendor",
            B.getSpecVendor(),
            B, codeP + "getSpecVendor()",
            ValueType.NAME);
        addInf(group,
            "Specification Version",
            B.getSpecVersion(),
            B, codeP + "getSpecVersion()",
            ValueType.VERSION);
        addInf(group,
            "Start Time",
            B.getStartTime(),
            B, codeP + "getStartTime()",
            ValueType.ABSOLUTE_TIME,
            "ms");
        addInf(group,
            "Up Time",
            B.getUptime(),
            B, codeP + "getUptime()",
            ValueType.ELAPSED_TIME,
            "ms");
        addInf(group,
            "VM Name",
            B.getVmName(),
            B, codeP + "getVmName()",
            ValueType.NAME);
        addInf(group,
            "VM Vendor",
            B.getVmVendor(),
            B, codeP + "getVmVendor()",
            ValueType.NAME);
        addInf(group,
            "VM Version",
            B.getVmVersion(),
            B, codeP + "getVmVersion()",
            ValueType.VERSION);
        addInf(group,
            "System Properties",
            B.getSystemProperties(),
            B, codeP + "getSystemProperties()",
            ValueType.KEY_VALUE_MAP);
        // other useful methods?
    }
    private static void processSystem() {
        Class<System> S = System.class;
        String codeP = "System.";
        String group = "System";

        addInf(group,
            "Console?",
            System.console() != null,
            S, codeP + "console()",
            ValueType.BOOLEAN);
        addInf(group,
            "Security Manager?",
            System.getSecurityManager() != null,
            S, codeP + "getSecurityManager()",
            ValueType.BOOLEAN);
        addInf(group,
            "Current Time (ms)",
            System.currentTimeMillis(),
            S, codeP + "currentTimeMillis()",
            ValueType.ABSOLUTE_TIME,
            "ms");
        addInf(group,
            "Current Time (ns)",
            System.nanoTime(),
            S, codeP + "nanoTime()",
            ValueType.ABSOLUTE_TIME,
            "ns");
        addInf(group,
            "Environment",
            System.getenv(),
            S, codeP + "getEnv()",
            ValueType.KEY_VALUE_MAP);
        addInf(group,
            "Properties",
            System.getProperties(),
            S, codeP + "getProperties()",
            ValueType.KEY_VALUE_MAP);
    }

    private static void processToolkit() {
        Toolkit T = Toolkit.getDefaultToolkit();
        String codeP = "Toolkit.getDefaultToolkit().";
        String group = "Toolkit";

        addInf(group,
            "AWT Event Listeners",
            T.getAWTEventListeners(),
            T, codeP + "getAWTEventListeners()",
            ValueType.VALUE_LIST);
        addInf(group,
            "Color Model",
            T.getColorModel(),
            T, codeP + "getColorModel()",
            ValueType.DEFAULT);
        addInf(group,
            "Desktop Property",
            T.getDesktopProperty("awt.font.desktophints"),
            T, codeP + "getDesktopProperty(\"awt.font.desktophints\")",
            ValueType.DEFAULT);
        addInf(group,
            "Locking Key State: Caps Lock",
            getKeyLockingState(T, KeyEvent.VK_CAPS_LOCK),
            T, codeP + "getLockingKeyState(KeyEvent.VK_CAPS_LOCK)",
            ValueType.DEFAULT);
        addInf(group,
            "Locking Key State: Num Lock",
            getKeyLockingState(T, KeyEvent.VK_NUM_LOCK),
            T, codeP + "getLockingKeyState(KeyEvent.VK_NUM_LOCK)",
            ValueType.DEFAULT);
        addInf(group,
            "Locking Key State: Scroll Lock",
            getKeyLockingState(T, KeyEvent.VK_SCROLL_LOCK),
            T, codeP + "getLockingKeyState(KeyEvent.VK_SCROLL_LOCK)",
            ValueType.DEFAULT);
        addInf(group,
            "Locking Key State: Kana Lock",
            getKeyLockingState(T, KeyEvent.VK_KANA_LOCK),
            T, codeP + "getLockingKeyState(KeyEvent.VK_KANA_LOCK)",
            ValueType.DEFAULT);
        addInf(group,
            "Maximum Cursor Colors",
            T.getMaximumCursorColors(),
            T, codeP + "getMaximumCursorColors()",
            ValueType.DEFAULT,
            "colors");
        addInf(group,
            "Menu Shortcut Key Mask",
            T.getMenuShortcutKeyMask(),
//            " (" + maskStr(T.getMenuShortcutKeyMask()),
            T, codeP + "getMenuShortcutKeyMask()",
            ValueType.KEY_MASK);
        addInf(group,
            "Screen Insets",
            T.getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()),
            T, codeP + "getScreenInsets()",
            ValueType.DEFAULT);
        addInf(group,
            "Screen Resolution",
            T.getScreenResolution(),
            T, codeP + "getScreenResolution()",
            ValueType.DEFAULT,
            "dpi");
        addInf(group,
            "Screen Size",
            T.getScreenSize(),
            T, codeP + "getScreenSize()",
            ValueType.DEFAULT);
        addInf(group,
            "System Clipboard",
            T.getSystemClipboard(),
            T, codeP + "getSystemClipboard()",
            ValueType.DEFAULT);
        addInf(group,
            "System Selection",
            T.getSystemSelection(),
            T, codeP + "getSystemSelection()",
            ValueType.DEFAULT);
        addInf(group,
            "Always On Top Supported?",
            T.isAlwaysOnTopSupported(),
            T, codeP + "isAlwaysOnTopSupported()",
            ValueType.BOOLEAN);
        addInf(group,
            "Dynamic Layout Active?",
            T.isDynamicLayoutActive(),
            T, codeP + "isDynamicLayoutActive()",
            ValueType.BOOLEAN);
        addInf(group,
            "Frame State Supported: ICONIFIED",
            T.isFrameStateSupported(Frame.ICONIFIED),
            T, codeP + "isFrameStateSupported(Frame.ICONIFIED)",
            ValueType.BOOLEAN);
        addInf(group,
            "Frame State Supported: MAXIMIZED_BOTH",
            T.isFrameStateSupported(Frame.MAXIMIZED_BOTH),
            T, codeP + "isFrameStateSupported(Frame.MAXIMIZED_BOTH)",
            ValueType.BOOLEAN);
        addInf(group,
            "Frame State Supported: MAXIMIZED_HORIZ",
            T.isFrameStateSupported(Frame.MAXIMIZED_HORIZ),
            T, codeP + "isFrameStateSupported(Frame.MAXIMIZED_HORIZ)",
            ValueType.BOOLEAN);
        addInf(group,
            "Frame State Supported: MAXIMIZED_VERT",
            T.isFrameStateSupported(Frame.MAXIMIZED_VERT),
            T, codeP + "isFrameStateSupported(Frame.MAXIMIZED_VERT)",
            ValueType.BOOLEAN);
        addInf(group,
            "Frame State Supported: NORMAL",
            T.isFrameStateSupported(Frame.NORMAL),
            T, codeP + "isFrameStateSupported(Frame.NORMAL)",
            ValueType.BOOLEAN);
        addInf(group,
            "Modal Exclusion Type Supported: APPLICATION_EXCLUDE",
            T.isModalExclusionTypeSupported(Dialog.ModalExclusionType.APPLICATION_EXCLUDE),
            T, codeP + "isModalExclusionTypeSupported(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)",
            ValueType.BOOLEAN);
        addInf(group,
            "Modal Exclusion Type Supported: NO_EXCLUDE",
            T.isModalExclusionTypeSupported(Dialog.ModalExclusionType.NO_EXCLUDE),
            T, codeP + "isModalExclusionTypeSupported(Dialog.ModalExclusionType.NO_EXCLUDE)",
            ValueType.BOOLEAN);
        addInf(group,
            "Modal Exclusion Type Supported: TOOLKIT_EXCLUDE",
            T.isModalExclusionTypeSupported(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE),
            T, codeP + "isModalExclusionTypeSupported(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE)",
            ValueType.BOOLEAN);
        addInf(group,
            "Modality Type Supported: APPLICATION_MODAL",
            T.isModalityTypeSupported(Dialog.ModalityType.APPLICATION_MODAL),
            T, codeP + "isModalityTypeSupported(Dialog.ModalityType.APPLICATION_MODAL)",
            ValueType.BOOLEAN);
        addInf(group,
            "Modality Type Supported: DOCUMENT_MODAL",
            T.isModalityTypeSupported(Dialog.ModalityType.DOCUMENT_MODAL),
            T, codeP + "isModalityTypeSupported(Dialog.ModalityType.DOCUMENT_MODAL)",
            ValueType.BOOLEAN);
        addInf(group,
            "Modality Type Supported: MODELESS",
            T.isModalityTypeSupported(Dialog.ModalityType.MODELESS),
            T, codeP + "isModalityTypeSupported(Dialog.ModalityType.MODELESS)",
            ValueType.BOOLEAN);
        addInf(group,
            "Modality Type Supported: TOOLKIT_MODAL",
            T.isModalityTypeSupported(Dialog.ModalityType.TOOLKIT_MODAL),
            T, codeP + "isModalityTypeSupported(Dialog.ModalityType.TOOLKIT_MODAL)",
            ValueType.BOOLEAN);
    }

    private static Object getKeyLockingState(Toolkit T, int v) {
        try {
            return T.getLockingKeyState(v);
        } catch(Exception e) {
            return "N/A";
        }
    }

    private static void addInf(String groupName, String name, Object value, Object target, String code, ValueType type) {
        addInf(groupName, name, value, target, code, type, null);
    }
    private static void addInf(String groupName, String name, Object value, Object target, String code, ValueType type, String units) {
        Group group = groups.get(groupName);
        if(group == null) {
            group = new Group(groupName);
            groups.put(groupName, group);
        }
        String unitsStr = (units == null) ? "" : " " + units;

        String pretty, rawStr;
        if(value == null) {
            pretty = "(none)";
            rawStr = "null";
        } else {
            switch(type) {
                case VERSION:       pretty = "v" + value; break;
                case MEMORY_SIZE:   pretty = FileUtil.getReadableSizeString((Long) value); break;
                case ABSOLUTE_TIME:
                    if(units.equals("ms")) {
                        pretty = DateUtil.toLongString((Long) value);
                    } else if(units.equals("ns")) {
                        pretty = DateUtil.toLongString((Long) value / 1000000);
                    } else {
                        pretty = "UNKNOWN";
                    }
                    break;
                case ELAPSED_TIME:
                    if(units.equals("ms")) {
                        pretty = DateUtil.toElapsedString((Long) value);
                    } else if(units.equals("ns")) {
                        pretty = DateUtil.toElapsedString((Long) value / 1000000);
                    } else {
                        pretty = "UNKNOWN";
                    }
                    break;
                case KEY_VALUE_MAP:
                    pretty = value.getClass().getName();
                    if(value instanceof Properties) {
                        pretty += " [" + ((Properties) value).size() + " elements]";
                    } else if(value instanceof Map<?, ?>) {
                        pretty += " [" + ((Map<?, ?>) value).size() + " elements]";
                    }
                    break;
                case VALUE_LIST:
                    pretty = value.getClass().getName();
                    if(value instanceof List<?>) {
                        pretty += " [" + ((List<?>) value).size() + " elements]";
                    } else if(value.getClass().isArray()) {
                        pretty += " [" + ((AWTEventListener[]) value).length + " elements]";
                    }
                    break;
                case BOOLEAN:
                    pretty = (Boolean) value ? "Yes" : "No";
                    break;
                case KEY_MASK:
                    pretty = toReadableMaskString((Integer) value);
                    break;
                default:
                    pretty = value + unitsStr;
                    break;
            }
            rawStr = value + unitsStr;
            if(value.getClass().isArray()) {
                rawStr = Arrays.toString((Object[]) value);
            }
        }

//        if(type == ValueType.KEY_VALUE_MAP || type == ValueType.VALUE_LIST) {
//            rawStr = "<curtailed>";
//        }

        Property prop = new Property();
        prop.setName(name);
        prop.setPrettyValStr(pretty);
        prop.setRawVal(value);
        prop.setRawValStr(rawStr);
        prop.setCode(code);
        prop.setType(type);

        group.put(name, prop);
    }

    private static String toReadableMaskString(int code) {
        if(code == Event.CTRL_MASK) {
            return "CTRL";
        } else if(code == Event.ALT_MASK) {
            return "ALT";
        } else if(code == Event.META_MASK) {
            return "META";
        }
        return "?";
    }


    private static void printMethods(Object o) {
        Class<?> c = o.getClass();
        printMethods(c);
    }
    private static void printMethods(Class<?> c) {
        Map<String, Method> methods = new TreeMap<String, Method>();
        List<Method> voidm = new ArrayList<Method>();
        List<Method> depm = new ArrayList<Method>();
        List<Method> goodm = new ArrayList<Method>();
        List<Method> argm = new ArrayList<Method>();
        List<Method> staticm = new ArrayList<Method>();
        for(Method m : c.getDeclaredMethods()) {
            methods.put(m.getName(), m);
        }
        for(Method m : c.getMethods()) {
            methods.put(m.getName(), m);
        }
        for(String key : methods.keySet()) {
            Method m = methods.get(key);
            if(!Modifier.isPublic(m.getModifiers())) {
                continue;
            }

            // Object methods
            if(m.getName().equals("equals") || m.getName().equals("getClass") || m.getName().equals("toString") || m.getName().equals("hashCode")) {
                continue;
            }

            if(isDep(m)) {
                depm.add(m);
            } else if(m.getReturnType().getSimpleName().equals("void")) {
                voidm.add(m);
            } else if(Modifier.isStatic(m.getModifiers())) {
                staticm.add(m);
            } else if(m.getParameterTypes().length != 0) {
                argm.add(m);
            } else {
                goodm.add(m);
            }
        }
        if(goodm.size() != 0) {
            System.out.println("=== GOOD ===");
            for(Method m : goodm) {
                printMethod(m);
            }
        }
        if(staticm.size() != 0) {
            System.out.println("=== STATIC ===");
            for(Method m : staticm) {
                printMethod(m);
            }
        }
        if(argm.size() != 0) {
            System.out.println("=== HAS ARGS ===");
            for(Method m : argm) {
                printMethod(m);
            }
        }
        if(voidm.size() != 0) {
            System.out.println("=== VOID ===");
            for(Method m : voidm) {
                printMethod(m);
            }
        }
        if(depm.size() != 0) {
            System.out.println("=== DEPRECATED ===");
            for(Method m : depm) {
                printMethod(m);
            }
        }
    }
    private static boolean isDep(Method m) {
        Annotation[] annos = m.getAnnotations();
        boolean isDep = false;
        for(Annotation anno : annos) {
            if(anno.annotationType().getName().equals("java.lang.Deprecated")) {
                isDep = true;
            }
        }
        return isDep;
    }
    private static void printMethod(Method m) {
        Class<?> rt = m.getReturnType();
        String params = ArrayUtil.render(m.getParameterTypes(), new ArrayUtil.RenderOne<Class<?>>() {
            public String render(Class<?> t) {
                return t.getSimpleName();
            }
        });
        System.out.println((Modifier.isStatic(m.getModifiers()) ? "static " : "") +
            rt.getSimpleName() + " " + m.getName() +
            "(" + params + ")" + (isDep(m)?" @Deprecated" : ""));
    }
}
