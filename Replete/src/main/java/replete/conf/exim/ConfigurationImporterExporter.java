package replete.conf.exim;

import java.io.File;

import replete.conf.node.ConfigurationNode;
import replete.conf.node.ConfigurationRoot;

public interface ConfigurationImporterExporter {

    public void setDestinationFile(File destination);

    public boolean write(ConfigurationNode element);

    public ConfigurationRoot read(File source);

}
