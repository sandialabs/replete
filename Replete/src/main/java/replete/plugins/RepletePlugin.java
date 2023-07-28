package replete.plugins;

import javax.swing.ImageIcon;

import replete.SoftwareVersion;
import replete.bc.RepleteSoftwareVersionLookup;
import replete.bc.SoftwareVersionLookup;
import replete.diff.DifferGenerator;
import replete.diff.generic.GenericObjectDifferGenerator;
import replete.ui.ClassNameSimplifier;
import replete.ui.help.HelpProvider;
import replete.ui.params.hier.PropertyGenerator;
import replete.ui.params.hier.test.TestPropertyGenerator;
import replete.xstream.RepleteXStreamConfigurator;
import replete.xstream.XStreamClassNameSimplifier;
import replete.xstream.XStreamConfigurator;

public class RepletePlugin implements Plugin {
    @Override
    public String getName() {
        return "Replete Platform Plug-in";
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
        return "This plug-in provides the base Replete platform extension points and basic default extensions.";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends ExtensionPoint>[] getExtensionPoints() {
        return new Class[] {
            PropertyGenerator.class,
            HelpProvider.class,
            SoftwareVersionLookup.class,
            XStreamConfigurator.class,
            ClassNameSimplifier.class,
            DifferGenerator.class
        };
    }

    @Override
    public ExtensionPoint[] getExtensions() {
        return new ExtensionPoint[] {
            new TestPropertyGenerator(),
            new RepleteSoftwareVersionLookup(),
            new RepleteXStreamConfigurator(),

            // ClassNameSimplifier
            new XStreamClassNameSimplifier(),

            // DifferGenerator
            new GenericObjectDifferGenerator()
        };
    }

    @Override
    public void start() {}
}
