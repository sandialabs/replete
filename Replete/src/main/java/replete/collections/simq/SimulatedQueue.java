package replete.collections.simq;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import replete.collections.Pair;
import replete.util.DateUtil;

// Simulate periodic adding of data
// Simulate periodic modification of data
// Simulate periodic removal of data

public class SimulatedQueue<I, T> {


    ////////////
    // FIELDS //
    ////////////

    private Map<I, DataGlob<T>> data = new LinkedHashMap<>();
    private Map<UUID, TaskGlob> taskGlobs = new HashMap<>();
    private Timer timer;
    private int total;                // Total # of data elements ever added
    private boolean printAfterTasks;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SimulatedQueue() {
        this(false);
    }
    public SimulatedQueue(boolean daemon) {
        timer = new Timer(daemon);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Map<I, DataGlob<T>> getDataInternal() {  // Only meant to be called by Mutators that are
        return data;                                // already synchronized on data via executeMutator.
    }
    public int getTotal() {
        return total;
    }
    public boolean isPrintAfterTasks() {
        return printAfterTasks;
    }

    // Accessors (Computed)

    public int getSize() {
        return data.size();
    }
    public synchronized Map<I, T> getAll() {
        Map<I, T> dataCopy = new LinkedHashMap<>();
        for(I key : data.keySet()) {
            DataGlob<T> glob = data.get(key);
            dataCopy.put(key, glob.object);
        }
        return dataCopy;
    }

    // Mutators

    public SimulatedQueue setPrintAfterTasks(boolean printAfterModification) {
        this.printAfterTasks = printAfterModification;
        return this;
    }


    ///////////
    // TASKS //
    ///////////

    public UUID addCreator(int interval, SimDataCreator<I, T> creator) {
        TaskGlob taskGlob = new CreatorGlob(UUID.randomUUID(), interval, null, creator);
        synchronized(taskGlobs) {
            taskGlobs.put(taskGlob.id, taskGlob);
            //if(timer started) {
            //   startTask(blob);
            //}
        }
        return taskGlob.id;
    }

    public void removeCreator(UUID taskId) {
        synchronized(taskGlobs) {
            TaskGlob taskGlob = taskGlobs.get(taskId);
            if(taskGlob.task != null) {
                taskGlob.task.cancel();
            }
            taskGlobs.remove(taskId);
        }
    }

    public UUID addMutator(int interval, SimDataMutator<I, T> mutator) {
        MutatorGlob blob = new MutatorGlob(UUID.randomUUID(), interval, null, mutator);
        synchronized(taskGlobs) {
            taskGlobs.put(blob.id, blob);
            //if(timer started) {
            //   startCreatorTask(blob);
            //}
        }
        return blob.id;
    }

    private void startTask(TaskGlob taskGlob) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                taskGlob.perform();
                if(printAfterTasks) {
                    print(taskGlob.getName());
                }
            }
        };
        taskGlob.task = task;
        timer.schedule(task, 0, taskGlob.interval);
    }

    public void start() {
        synchronized(taskGlobs) {
            for(TaskGlob taskGlob : taskGlobs.values()) {
                startTask(taskGlob);
            }
        }
    }


    //////////
    // DATA //
    //////////

    private synchronized void executeCreator(SimDataCreator<I, T> creator) {
        Pair<I, T>[] elems = creator.createElements(data.size(), total);
        for(Pair<I, T> elem : elems) {
            addElement(elem.getValue1(), elem.getValue2());
        }
    }
    private synchronized void executeMutator(SimDataMutator<I, T> mutator) {
        mutator.mutate(this);
    }

    public synchronized void addElement(I key, T elem) {
        DataGlob<T> dataGlob = new DataGlob<>(total++, System.currentTimeMillis(), elem);
        data.put(key, dataGlob);
    }

    public synchronized void changeElement(I key, T elem) {
        DataGlob<T> dataGlob = data.get(key);
        if(dataGlob != null) {
            dataGlob.object = elem;
        }
    }

    public synchronized void removeElement(I key) {
        data.remove(key);
    }


    //////////
    // MISC //
    //////////

    private synchronized void print(String title) {
        System.out.println("Data After " + title);
        for(I key : data.keySet()) {
            DataGlob<T> glob = data.get(key);
            System.out.println("ID=" + key + "; IDX=" + glob.index + "; TIME=" + DateUtil.toLongString(glob.when) + "; OBJ=" + glob.object);
        }
        System.out.println("---");
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private abstract class TaskGlob {
        UUID id;
        int interval;
        TimerTask task;

        public TaskGlob(UUID id, int interval, TimerTask task) {
            this.id = id;
            this.interval = interval;
            this.task = task;
        }

        public abstract void perform();
        public abstract String getName();
    }

    private class CreatorGlob extends TaskGlob {
        SimDataCreator<I, T> creator;

        public CreatorGlob(UUID id, int interval, TimerTask task, SimDataCreator<I, T> creator) {
            super(id, interval, task);
            this.creator = creator;
        }

        @Override
        public void perform() {
            executeCreator(creator);
        }
        @Override
        public String getName() {
            return "Creator";
        }
    }

    private class MutatorGlob extends TaskGlob {  // Just get access to queue as a whole, can do anything when executed
        SimDataMutator<I, T> mutator;

        public MutatorGlob(UUID id, int interval, TimerTask task, SimDataMutator<I, T> mutator) {
            super(id, interval, task);
            this.mutator = mutator;
        }

        @Override
        public void perform() {
            executeMutator(mutator);
        }
        @Override
        public String getName() {
            return "Mutator";
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        SimulatedQueue<UUID, Person> queue = new SimulatedQueue<>();
        UUID id = queue.addCreator(3000, (s, t) -> {
            System.out.println("Current Size = " + s + " / Total Added = " + t);
            return new Pair[] {
                new Pair<>(UUID.randomUUID(), new Person("Name" + t))
            };
        });
        queue.addMutator(1000, q -> {
            Set<UUID> removeOld = new HashSet<>();
            for(UUID key : q.getDataInternal().keySet()) {
                DataGlob<Person> glob = q.getDataInternal().get(key);
                int age = q.getTotal() - 1 - glob.index;
                if(age >= 10) {
                    removeOld.add(key);
                }
            }
            for(UUID key : removeOld) {
                q.getDataInternal().remove(key);
            }
        });
        queue.start();
        System.out.println(id);
    }

    private static class Person {
        private String name;
        private int age;
        public Person(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
    }
}
