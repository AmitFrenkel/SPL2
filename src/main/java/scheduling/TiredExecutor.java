package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        workers = new TiredThread[numThreads];
        for(int i=0; i<numThreads; i++){
            workers[i] = new TiredThread(i, (Math.random()+0.5));
            workers[i].start();
            idleMinHeap.add(workers[i]);
        }
    }

    public void submit(Runnable task) {
    try {
        final TiredThread worker = idleMinHeap.take();
        inFlight.incrementAndGet();
        worker.newTask(() -> {
            try {
                task.run();
            } finally {
                idleMinHeap.put(worker);

                if (inFlight.decrementAndGet() == 0) {
                    synchronized (this) {
                        notifyAll();
                    }
                }
            }
        });
    } catch (InterruptedException e) {
        
    }
}

    public void submitAll(Iterable<Runnable> tasks) {
    for (Runnable task : tasks) {
        submit(task);
    }
    synchronized (this) {
        while (inFlight.get() > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}


    public void shutdown() throws InterruptedException {
        for(TiredThread worker : workers){
            worker.shutdown();
        }
        for(TiredThread worker : workers){
            worker.join();
        }
    }

    public synchronized String getWorkerReport() {
        StringBuilder sb = new StringBuilder();
        
    int n = workers.length;
    double sum = 0.0;
    double[] f = new double[n];

    for (int i = 0; i < n; i++) {
        f[i] = workers[i].getFatigue();
        sum += f[i];
    }

    double avg = (n == 0 ? 0.0 : sum / n);

    double fairness = 0.0;
    for (int i = 0; i < n; i++) {
        double d = f[i] - avg;
        fairness += d * d;
    }
        for(TiredThread worker : workers){
            sb.append(String.format("Worker %d: Fatigue=%f, TimeUsed=%d ns, TimeIdle=%d ns, \n",
                    worker.getWorkerId(),
                    worker.getFatigue(),
                    worker.getTimeUsed(),
                    worker.getTimeIdle()));
        }
        sb.append(String.format("Overall Fatigue Fairness Index: %f\n", fairness));
        return sb.toString();
    }
}
