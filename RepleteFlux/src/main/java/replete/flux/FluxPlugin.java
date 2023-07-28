package replete.flux;

import javax.swing.ImageIcon;

import replete.flux.viz.VisualizerGenerator;
import replete.flux.viz.dflt.DefaultVisualizerGenerator;
import replete.flux.viz.fallingblocks.FallingBlocksVisualizerGenerator;
import replete.flux.viz.list.ListVisualizerGenerator;
import replete.flux.viz.raindrops.RaindropsVisualizerGenerator;
import replete.plugins.ExtensionPoint;
import replete.plugins.Plugin;

public class FluxPlugin implements Plugin {

    @Override
    public String getName() {
        return "Flux";
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
        return "This plug-in provides extension points and extensions for augmenting the Flux visualization panel.";
    }

    @Override
    public Class<? extends ExtensionPoint>[] getExtensionPoints() {
        return new Class[] {
            VisualizerGenerator.class
        };
    }

    @Override
    public ExtensionPoint[] getExtensions() {
        return new ExtensionPoint[] {
            new DefaultVisualizerGenerator(),
            new ListVisualizerGenerator(),
            new FallingBlocksVisualizerGenerator(),
            new RaindropsVisualizerGenerator()
        };
    }

    @Override
    public void start() {
    }

}
