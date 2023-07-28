package replete.parallel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import replete.params.combine.ParameterSet;
import replete.params.combine.groupset.ParameterSpecGroupSet;
import replete.params.combine.specs.ListParameterSpecification;
import replete.threads.ThreadUtil;

public class Parallel {

    // Equivalent of C#'s Parallel.For(int, int, Action)
    public static <R> ExecuteSummary execute(int start, int end, ParallelizableCallable<Integer, R> runnable) throws ExecuteException {
        return execute(start, end, runnable, null);
    }

    // Equivalent of C#'s Parallel.For(int, int, Action) with control parameters
    public static <R> ExecuteSummary execute(int start, int end, ParallelizableCallable<Integer, R> runnable, ExecuteParameters exParams) throws ExecuteException {
        List<Integer> params = new ArrayList<Integer>();
        for(int i = start; i <= end; i++) {
            params.add(i);
        }
        return execute(params, runnable, exParams);
    }

    // For integration with fixed parameter space searching code
    public static <R> ExecuteSummary execute(ParameterSpecGroupSet set, ParallelizableCallable<ParameterSet, R> runnable) throws ExecuteException {
        return execute(set, runnable, null);
    }

    // For integration with fixed parameter space searching code
    public static <R> ExecuteSummary execute(ParameterSpecGroupSet set, ParallelizableCallable<ParameterSet, R> runnable, ExecuteParameters exParams) throws ExecuteException {
        return execute(set.generateAllSetsFromSpecs(), runnable, exParams);
    }

    // Equivalent of C#'s Parallel.ForEach(Iterable, Action)
    public static <P, R> ExecuteSummary execute(Iterable<P> params, ParallelizableCallable<P, R> runnable) throws ExecuteException {
        return execute(params, runnable, null);
    }

    // Equivalent of C#'s Parallel.ForEach(Iterable, Action) with control parameters
    public static <P, R> ExecuteSummary execute(Iterable<P> params, final ParallelizableCallable<P, R> runnable, ExecuteParameters exParams) throws ExecuteException {

        // Create default execute parameters if none provided.
        if(exParams == null) {
            exParams = new ExecuteParameters();
        }

        // Set up final variables to be used in thread classes below.
        final ExecuteParameters fExParams = exParams;
        final ThreadPoolExecutor svc = (ThreadPoolExecutor) Executors.newFixedThreadPool(exParams.getThreads());

        // http://codereview.stackexchange.com/questions/25763/moving-from-normal-threads-to-executorservice-thread-pools-in-java
                                                          // core,max,katime,katimeunit,Q
        // Executors.newFixedThreadPool(10);              // 10,10,0L,MS,LinkedBlockingQ
        // Executors.newCachedThreadPool();               // 0,MAX,60L,S,SyncQ
        // Executors.newSingleThreadExecutor();           // 1,1,0L,MS,LBQ
        // Executors.newScheduledThreadPool(10);          // 0,10,0,NS,DelayedWorkQ
        // Executors.newSingleThreadScheduledExecutor();  // 1,MAX,0,NS,DWQ

        final Set<Future<R>> submitted = new HashSet<Future<R>>();
        final Iterator<P> iterator = params.iterator();
        final ExecuteState state = new ExecuteState();
        final AtomicLong totalDuration = new AtomicLong();

        // Create the producer thread.
        Thread producerThread = new Thread() {
            @Override
            public void run() {

                // While the parameters iterator has more parameters, attempt
                // to submit more tasks for each parameter (or set of parameters).
                while(iterator.hasNext()) {

                    // Get control of the submitted future set so we can potentially
                    // wait on it if it is too large at the moment.
                    synchronized(submitted) {

                        // Wait this producer thread if the number of submitted
                        // is at the maximum # of tasks allowed to be submitted
                        // at any given time.
                        while(submitted.size() >= fExParams.getMaxSimulSubmitted()) {
                            ThreadUtil.wait(submitted);
                        }

                        int before = submitted.size();   // Record current size

                        // While there are more parameters (tasks to be created)
                        // and we are still under the maximum # of tasks allowed
                        // to be submitted at any given time...
                        while(iterator.hasNext() && submitted.size() < fExParams.getMaxSimulSubmitted()) {

                            final P param = iterator.next();

                            // Create a Callable, submit it to the service, and save
                            // the future in the submitted set.
                            Future<R> future = svc.submit(new Callable<R>() {
                                public R call() throws Exception {
                                    long time = System.currentTimeMillis();
                                    try {
                                        R result = runnable.run(param);
                                        return result;
                                    } finally {
                                        totalDuration.addAndGet(System.currentTimeMillis() - time);
                                    }
                                };
                            });
                            submitted.add(future);
                        }

                        // If there were new tasks submitted, then notify the
                        // consumer thread, which could be waiting to do
                        // something.
                        if(before != submitted.size()) {
                            submitted.notify();
                        }

                        // Increment how many tasks were submitted.
                        state.submitted += submitted.size() - before;
                    }
                }

                // Now that all the parameters have been exhausted, signal that
                // the submission segment is done.
                state.submissionsDone = true;
            }
        };

        // Create the consumer thread.
        Thread consumerThread = new Thread() {
            @Override
            public void run() {
                while(!state.submissionsDone || state.finished != state.submitted) {
                    ThreadUtil.sleep(fExParams.getPollDelay());   // POLL WAITING FOR SOME TO FINISH...
                    synchronized(submitted) {
                        while(submitted.size() == 0) {
                            ThreadUtil.wait(submitted);
                        }
                        List<Future<R>> removeFutures = new ArrayList<Future<R>>();
                        for(Future<R> future : submitted) {
                            if(future.isDone()) {
                                removeFutures.add(future);
                                if(fExParams.getDoneListener() != null) {
                                    try {
                                        // TODO: Why are errors not propogating
                                        R result = future.get();
                                        DoneListener<R> dl = (DoneListener<R>) fExParams.getDoneListener();
                                        dl.done(result);
                                    } catch(InterruptedException e) {
                                        e.printStackTrace();
                                    } catch(ExecutionException e) {
//                                        e.printStackTrace();
                                        state.errored++;
                                    }
                                }
                            }
                        }
                        for(Future<R> remove : removeFutures) {
                            submitted.remove(remove);
                        }
                        state.finished += removeFutures.size();
                        if(removeFutures.size() != 0) {
                            submitted.notify();
                        }
                    }
                }
            };
        };

        // Start both of the above threads from this thread.
        producerThread.start();
        consumerThread.start();

        // Halt this thread until both of the above threads have
        // themselves completed.
        try {
            producerThread.join();
            consumerThread.join();
        } catch(InterruptedException e) {
            throw new ExecuteException(e);
        }

        // Perform clean up of the executor service.
        List<Runnable> pending = svc.shutdownNow();
        if(pending.size() != 0) {
            throw new ExecuteException("Validity check failed.");
        }

//        ThreadUtil.sleep(1000);
//        if(svc.getCompletedTaskCount() != state.submitted) {
//            throw new ExecuteException("Validity check failed. " +
//                            svc.getCompletedTaskCount() + " != " + state.submitted);
//        }

        // Compose and return the summary of the task execution.
        double avgLength = (double) totalDuration.get() / state.submitted;
        return new ExecuteSummary(state.submitted, state.finished, state.errored, avgLength);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("November");
        list.add("Thursday");
        list.add("Winter");
        list.add("Game");
        list.add("Colorado");
        list.add("Onion");
        list.add("Couch");
        list.add("Holy");
        list.add("Hi");
        list.add("There");
        list.add("Hearth");
        list.add("Stone");
        list.add("Turn");

        execute(1, 100, new ParallelizableCallable<Integer, String>() {
            public String run(Integer param) throws Exception {
                System.out.println(Thread.currentThread().getId());
                return param + "!";
            }
        }, new ExecuteParameters().setMaxSimulSubmitted(20));

//        System.out.println(
//        execute(list, new ParallelizableCallable<String, Void>() {
//            @Override
//            public Void run(String param) throws Exception {
//                System.out.println(Thread.currentThread().getId());
//                System.out.println("Param=" + param);
//                return null;
//            }
//        })
//        );

        ParameterSpecGroupSet set = new ParameterSpecGroupSet();
        set.add("left", new ListParameterSpecification((List) list));
        set.add("right", new ListParameterSpecification((List) list));

        execute(set, new ParallelizableCallable<ParameterSet, Void>() {
            @Override
            public Void run(ParameterSet params) throws Exception {
                System.out.println(Thread.currentThread().getId());
                System.out.println("Params=" + params);
                return null;
            }
        });


        //        ExecuteSummary summary = execute(list, new ParallelizableCallable<String, String>() {
//            public String run(String huh) throws Exception {
//                System.out.println("TASK STARTED=" + huh);
//                ThreadUtil.sleep(3000);
//                return huh + "|" + huh.length();
//            }
//        }, new ExecuteParameters().setDoneListener(new DoneListener<String>() {
//            public void done(String result) {
//                System.out.println("DONE=" + result);
//            }
//        }).setThreads(2)
//          .setMaxSimulSubmitted(2)
//        );
//        System.out.println(summary);

//        if(true) {
//            return;
//        }

//        execute(5, list, new ParallelizableRunnable() {
//            @Override
//            public void run(Map<String, Object> params) {
//                System.out.println(Thread.currentThread().getId() + " " + params);
//            }
//        });

//        ParameterSpecGroupSet set = new ParameterSpecGroupSet();
//        set.add("left", new ListParameterSpecification((List) list));
//        set.add("right", new ListParameterSpecification((List) list));
////        set.add("right", new ListParameterSpecification(6, 10));
//        Parallel.execute(5, set, new ParallelizableCallable<Void, Integer>() {
//            @Override
//            public Void run(Integer param) throws Exception {
//                System.out.println(Thread.currentThread().getId() + " " + param);
//                return null;
//            }
//        });

//        ParameterSpecGroupSet set = new ParameterSpecGroupSet();
//        set.add(5, "a", new StepParameterSpecification(10, 2));
//        set.add(10, "b", new EvenSpacingParameterSpecification(40, 60));
//        execute(5, set, new ParallelizableRunnable() {
//            @Override
//            public void run(Map<String, Object> params) {
//                Number a = (Number) params.get("a");
//                Number b = (Number) params.get("b");
//                System.out.println(Thread.currentThread().getId() + " " + params + " " +
//                    a.doubleValue() * b.doubleValue());
//            }
//        });
    }
}
