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
            workers[i] = new TiredThread(i, (Math.random()*1.5+0.5));
            workers[i].start();
            idleMinHeap.add(workers[i]);
        }
    }

    public void submit(Runnable task) {
        final TiredThread worker;
        int min=0;
        for(int i=1; i<workers.length; i++){
            if(workers[i].compareTo(workers[min]) < 0){
                min = i;
            }
        }
        worker = workers[min];
        idleMinHeap.remove(worker);
        worker.newTask(()->{
            try {
                task.run();
            } finally {
                idleMinHeap.put(worker);
            }
        });
    }

    public void submitAll(Iterable<Runnable> tasks) {
        for(Runnable task : tasks){
            submit(task);
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
        for(TiredThread worker : workers){
            sb.append(String.format("Worker %d: Fatigue=%.2f, TimeUsed=%d ns, TimeIdle=%d ns\n",
                    worker.getWorkerId(),
                    worker.getFatigue(),
                    worker.getTimeUsed(),
                    worker.getTimeIdle()));
        }
        return sb.toString();
    }
}
