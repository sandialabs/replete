package replete.scrutinize;

import javax.swing.ImageIcon;

import replete.plugins.ExtensionPoint;
import replete.plugins.Plugin;
import replete.scrutinize.core.IBaseSc;
import replete.scrutinize.wrappers.disk.FileSc;
import replete.scrutinize.wrappers.disk.FileSystemViewSc;
import replete.scrutinize.wrappers.mgmt.ClassLoadingMXBeanSc;
import replete.scrutinize.wrappers.mgmt.CompilationMXBeanSc;
import replete.scrutinize.wrappers.mgmt.GarbageCollectorMXBeanSc;
import replete.scrutinize.wrappers.mgmt.LockInfoSc;
import replete.scrutinize.wrappers.mgmt.ManagementFactorySc;
import replete.scrutinize.wrappers.mgmt.MemoryMXBeanSc;
import replete.scrutinize.wrappers.mgmt.MemoryManagerMXBeanSc;
import replete.scrutinize.wrappers.mgmt.MemoryPoolMXBeanSc;
import replete.scrutinize.wrappers.mgmt.OperatingSystemMXBeanSc;
import replete.scrutinize.wrappers.mgmt.RuntimeMXBeanSc;
import replete.scrutinize.wrappers.mgmt.ThreadInfoSc;
import replete.scrutinize.wrappers.mgmt.ThreadMXBeanSc;
import replete.scrutinize.wrappers.net.InetAddressSc;
import replete.scrutinize.wrappers.net.InterfaceAddressSc;
import replete.scrutinize.wrappers.net.NetworkInterfaceSc;
import replete.scrutinize.wrappers.sys.ClassLoaderSc;
import replete.scrutinize.wrappers.sys.ClassSc;
import replete.scrutinize.wrappers.sys.ClipboardSc;
import replete.scrutinize.wrappers.sys.DataFlavorSc;
import replete.scrutinize.wrappers.sys.RobotSc;
import replete.scrutinize.wrappers.sys.RuntimeSc;
import replete.scrutinize.wrappers.sys.SystemSc;
import replete.scrutinize.wrappers.sys.ToolkitSc;
import replete.scrutinize.wrappers.sys.UrlSc;
import replete.scrutinize.wrappers.ui.AffineTransformSc;
import replete.scrutinize.wrappers.ui.BufferCapabilitiesSc;
import replete.scrutinize.wrappers.ui.ColorSc;
import replete.scrutinize.wrappers.ui.DimensionSc;
import replete.scrutinize.wrappers.ui.DisplayModeSc;
import replete.scrutinize.wrappers.ui.FontSc;
import replete.scrutinize.wrappers.ui.GraphicsConfigurationSc;
import replete.scrutinize.wrappers.ui.GraphicsDeviceSc;
import replete.scrutinize.wrappers.ui.GraphicsEnvironmentSc;
import replete.scrutinize.wrappers.ui.ImageCapabilitiesSc;
import replete.scrutinize.wrappers.ui.InsetsSc;
import replete.scrutinize.wrappers.ui.LocaleSc;
import replete.scrutinize.wrappers.ui.LookAndFeelInfoSc;
import replete.scrutinize.wrappers.ui.LookAndFeelSc;
import replete.scrutinize.wrappers.ui.MouseInfoSc;
import replete.scrutinize.wrappers.ui.PointSc;
import replete.scrutinize.wrappers.ui.PointerInfoSc;
import replete.scrutinize.wrappers.ui.RectangleSc;
import replete.scrutinize.wrappers.ui.SecurityManagerSc;
import replete.scrutinize.wrappers.ui.UIManagerSc;
import replete.scrutinize.wrappers.ui.WindowSc;

public class ScrutinizePlugin implements Plugin {
    @Override
    public String getName() {
        return "Replete Scrutinize Platform Plug-in";
    }
    @Override
    public String getVersion() {
        return SoftwareVersion.get().getFullVersionString();
    }
    @Override
    public String getProvider() {
        return "Sandia National Laboratories";
    }
    @Override
    public ImageIcon getIcon() {
        return null;
    }
    @Override
    public String getDescription() {
        return "This plug-in provides the base RepleteScrutinize platform extension points and basic default extensions.";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends ExtensionPoint>[] getExtensionPoints() {
        return new Class[] {
            IBaseSc.class
        };
    }

    @Override
    public ExtensionPoint[] getExtensions() {
        return new ExtensionPoint[] {
            new MouseInfoSc(),
            new PointerInfoSc(),
            new PointSc(),
            new RectangleSc(),
            new GraphicsDeviceSc(),
            new DisplayModeSc(),
            new GraphicsEnvironmentSc(),
            new GraphicsConfigurationSc(),
            new GraphicsDeviceSc(),
            new SecurityManagerSc(),
            new ImageCapabilitiesSc(),
            new AffineTransformSc(),
            new DimensionSc(),
            new InsetsSc(),
            new FontSc(),
            new BufferCapabilitiesSc(),
            new UIManagerSc(),
            new LookAndFeelSc(),
            new LookAndFeelInfoSc(),
            new WindowSc(),
            new LocaleSc(),
            new ColorSc(),

            new ClassSc(),
            new ClipboardSc(),
            new DataFlavorSc(),
            new RobotSc(),
            new RuntimeSc(),
            new SystemSc(),
            new ToolkitSc(),
            new ClassLoaderSc(),
            new UrlSc(),

            new FileSc(),
            new FileSystemViewSc(),

            new InetAddressSc(),
            new InterfaceAddressSc(),
            new NetworkInterfaceSc(),

            new ManagementFactorySc(),
            new ClassLoadingMXBeanSc(),
            new CompilationMXBeanSc(),
            new GarbageCollectorMXBeanSc(),
            new MemoryManagerMXBeanSc(),
            new MemoryMXBeanSc(),
            new MemoryPoolMXBeanSc(),
            new OperatingSystemMXBeanSc(),
            new RuntimeMXBeanSc(),
            new ThreadMXBeanSc(),
            new ThreadInfoSc(),
            new LockInfoSc()
        };
    }

    @Override
    public void start() {
    }
}
