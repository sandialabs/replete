package replete.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import replete.collections.RLinkedHashMap;
import replete.conf.exception.ConfigurationException;
import replete.conf.exim.ConfigurationImporterExporter;
import replete.conf.node.ConfigurationKey;
import replete.conf.node.ConfigurationNode;
import replete.conf.node.ConfigurationPath;
import replete.conf.node.ConfigurationRoot;
import replete.conf.node.rule.ConfigurationRule;

public class ConfigurationManager {

    ////////////
    // FIELDS //
    ////////////

    private ConfigurationRoot conf;
    private File confFile;
    private List<ConfigurationDomain> confDomains;
    private ConfigurationImporterExporter confImporterExporter;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConfigurationManager(List<ConfigurationDomain> domains, File file, ConfigurationImporterExporter confImporterExporter) throws ConfigurationException {
        this.confFile = file;

        this.conf = Conf.root();
        for(ConfigurationDomain domain : domains) {
            this.conf = Conf.merge(this.conf, domain.getDefaultTree());
        }

        this.confDomains = new ArrayList<>();
        this.confDomains.addAll(domains);

        this.confImporterExporter = confImporterExporter;
        this.confImporterExporter.setDestinationFile(confFile);
    }

    /////////////
    // GETTERS //
    /////////////

    public File getConfFile() {
        return confFile;
    }

    public List<ConfigurationDomain> getConfDomains() {
        return confDomains;
    }

    public ConfigurationImporterExporter getConfImporterExporter() {
        return confImporterExporter;
    }

    ////////////////////////
    // GETTERS (COMPUTED) //
    ////////////////////////

    public ConfigurationNode getConfNode(ConfigurationKey key) {
        return Conf.getNodeByKey(conf, key, true);
    }

    public Object getConfNode(ConfigurationPath path) {
        return Conf.getNodeByPath(conf, path);
    }

    /////////////
    // SETTERS //
    /////////////

    public void setConfFile(File confFile) {
        this.confFile = confFile;
    }

    public void setImporterExporter(ConfigurationImporterExporter importerExporter) {
        this.confImporterExporter = importerExporter;
    }

    ////////////////////////
    // SETTERS (COMPUTED) //
    ////////////////////////

    public void setConfNode(ConfigurationKey key, Object value) throws ConfigurationException {
        setConfNode(conf, key, value);
    }

    public void setConfNode(ConfigurationNode parent, ConfigurationKey key, Object value) throws ConfigurationException {
        // Type check for the key-value pair we're about to assign.
        if(key.getValueClassType().isAssignableFrom(value.getClass())) {
            throw new ConfigurationException("Inserting a new configuration element with an invalid value class. " +
                                             key.getValueClassType() + " is not " + value.getClass());
        }

        // See if the appropriate node already exists in our tree (search by key).
        ConfigurationNode node = Conf.getNodeByKey(parent, key, true);

        // Insert a new node into our configuration tree if:
        // - the node we were looking for couldn't be found.
        // - the provided parent node is known to hold ConfNodes.
        if(node == null && Conf.isCollectionOfConfNodes(parent)) {
            ((Collection)parent.getValue()).add(
                Conf.node(key, value)
            );
        } else {
            node.setValue(value);
        }
    }

    /////////////////////
    // FILE OPERATIONS //
    /////////////////////

    public ConfigurationRoot loadConfFile() throws ConfigurationException {
        conf = confImporterExporter.read(confFile);
        return conf;
    }

    public boolean saveConfFile() {
        return confImporterExporter.write(conf);
    }


    /////////////
    // UTILITY //
    /////////////

    public RLinkedHashMap<ConfigurationPath, List<ConfigurationIssue>> calculateIssues() {
        return calculateIssues(conf, new RLinkedHashMap<>());
    }

    private RLinkedHashMap<ConfigurationPath, List<ConfigurationIssue>> calculateIssues(ConfigurationNode start, RLinkedHashMap<ConfigurationPath, List<ConfigurationIssue>> mapSoFar) {
        if(!Conf.keyExistsInDomains(confDomains, start.getKey())) {
            ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.Level.WARNING, ConfigurationIssue.Type.DEPRECATED, start.getKey().getName());
            mapSoFar.getAndPutIfAbsent(Conf.getPathByNode(conf, start), new ArrayList<>()).add(issue);
        }

        List<ConfigurationRule> rules = start.getKey().getRules();
        for(ConfigurationRule rule : rules) {
            if(!rule.validate(start)) {
                mapSoFar.getAndPutIfAbsent(Conf.getPathByNode(conf, start), new ArrayList<>()).add(rule.getIssue());
            }
        }
        if(start.getValue() instanceof Collection<?>) {
            for(ConfigurationNode child : (Collection<ConfigurationNode>)start.getValue()) {
                calculateIssues(child, mapSoFar);
            }
        }
        return mapSoFar;
    }
}
