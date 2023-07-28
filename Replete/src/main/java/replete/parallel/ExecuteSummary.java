package replete.parallel;

public class ExecuteSummary {
    public int tasksSubmitted = 0;
    public int tasksFinished = 0;
    public int tasksErrored = 0;
    public double averageTaskLengthMillis = 0.0;
    public ExecuteSummary(int tasksSubmitted, int tasksFinished, int tasksErrored,
                          double averageTaskLengthMillis) {
        super();
        this.tasksSubmitted = tasksSubmitted;
        this.tasksFinished = tasksFinished;
        this.tasksErrored = tasksErrored;
        this.averageTaskLengthMillis = averageTaskLengthMillis;
    }
    public int getTasksSubmitted() {
        return tasksSubmitted;
    }
    public int getTasksFinished() {
        return tasksFinished;
    }
    @Override
    public String toString() {
        return "ExecuteSummary [tasksSubmitted=" + tasksSubmitted + ", tasksFinished=" +
            tasksFinished + ", tasksErrored=" + tasksErrored + ", averageTaskLengthMillis=" +
            averageTaskLengthMillis + "]";
    }
    public int getTasksErrored() {
        return tasksErrored;
    }
    public double getAverageTaskLengthMillis() {
        return averageTaskLengthMillis;
    }
    public void setTasksSubmitted(int tasksSubmitted) {
        this.tasksSubmitted = tasksSubmitted;
    }
    public void setTasksFinished(int tasksFinished) {
        this.tasksFinished = tasksFinished;
    }
    public void setTasksErrored(int tasksErrored) {
        this.tasksErrored = tasksErrored;
    }
    public void setAverageTaskLengthMillis(double averageTaskLengthMillis) {
        this.averageTaskLengthMillis = averageTaskLengthMillis;
    }
}
