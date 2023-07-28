package replete.process;

import java.util.List;

public class LaunchResult {
    private List<String> commandTokens;
    private Process      process;
    private Integer      processId;

    public List<String> getCommandTokens() {
        return commandTokens;
    }
    public Process getProcess() {
        return process;
    }
    public Integer getProcessId() {
        return processId;
    }

    public LaunchResult setCommandTokens(List<String> commandLineTokens) {
        this.commandTokens = commandLineTokens;
        return this;
    }
    public LaunchResult setProcess(Process process) {
        this.process = process;
        return this;
    }
    public LaunchResult setProcessId(Integer processId) {
        this.processId = processId;
        return this;
    }
}
