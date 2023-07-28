package replete.process;

// This class attempts to encapsulate general information about a
// process in an OS-independent way.
//
// Only processId & commandLine are reliably populated right now
// for both Windows & Linux.
//
// However, due to reliance on 3rd party tools (e.g. wmic, ps), the
// large variety of mechanisms with which processes can be spawned,
// and the issue of insufficient permissions, one should allow for
// the possibility that any given field might be null.

public class OsProcessDescriptor {


    ////////////
    // FIELDS //
    ////////////

    private String  user;
    private Integer processId;
    private String  imageName;
    private String  executablePath;
    private String  commandLine;
    private String  creationDate;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getUser() {
        return user;
    }
    public Integer getProcessId() {
        return processId;
    }
    public String getImageName() {
        return imageName;
    }
    public String getExecutablePath() {
        return executablePath;
    }
    public String getCommandLine() {
        return commandLine;
    }
    public String getCreationDate() {
        return creationDate;
    }

    // Mutators

    public OsProcessDescriptor setUser(String user) {
        this.user = user;
        return this;
    }
    public OsProcessDescriptor setProcessId(Integer processId) {
        this.processId = processId;
        return this;
    }
    public OsProcessDescriptor setImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }
    public OsProcessDescriptor setExecutablePath(String executablePath) {
        this.executablePath = executablePath;
        return this;
    }
    public OsProcessDescriptor setCommandLine(String commandLine) {
        this.commandLine = commandLine;
        return this;
    }
    public OsProcessDescriptor setCreationDate(String creationDate) {
        this.creationDate = creationDate;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "ProcessDescriptor [user=" + user + ", processId=" + processId + ", imageName=" + imageName +
            ", executablePath=" + executablePath + ", commandLine=" + commandLine +
            ", creationDate=" + creationDate + "]";
    }
}
