import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scheduling.TiredThread;

public class TiredThreadUnitTest {
    private TiredThread tiredThread;
    @BeforeEach
    public void setUp() {
        tiredThread = new TiredThread(0, 1.0);
    }
    @Test
    public void testConstructor() {
        assert(tiredThread.getWorkerId() == 0);
        assert(tiredThread.getFatigue() == 1.0*tiredThread.getTimeUsed());
    }
    @Test
    public void testNewTask() throws InterruptedException {
        Runnable task = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                
            }
        };
        tiredThread.newTask(task);
    }
    @Test
    public void testShutdown() throws InterruptedException {
        tiredThread.shutdown();
        tiredThread.join();
        assert(!tiredThread.isAlive());
    }
}
