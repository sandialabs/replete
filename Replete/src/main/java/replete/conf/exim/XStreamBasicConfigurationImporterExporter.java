package replete.conf.exim;

import java.io.File;
import java.io.FileWriter;

import com.thoughtworks.xstream.XStream;

import replete.conf.node.ConfigurationNode;
import replete.conf.node.ConfigurationRoot;

public class XStreamBasicConfigurationImporterExporter implements ConfigurationImporterExporter {

    ////////////
    // FIELDS //
    ////////////

    protected static final XStream xstream;
    private File destinationFile;

    ////////////
    // STATIC //
    ////////////

    static {
        xstream = new XStream();
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void setDestinationFile(File destination) {
        this.destinationFile = destination;
    }

    @Override
    public boolean write(ConfigurationNode element) {
        try(FileWriter writer = new FileWriter(destinationFile)) {
            xstream.toXML(element, writer);
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public ConfigurationRoot read(File source) {
        return (ConfigurationRoot) xstream.fromXML(source);
    }
}
