
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scheduling.TiredExecutor;

public class TiredExcutorUnitTest {
    private TiredExecutor executor;
    @BeforeEach
    public void setUp() {
        executor = new TiredExecutor(2);
    }
    @Test
    public void testSubmit() throws InterruptedException {
        final boolean[] ran = {false};
        Runnable task = () -> {
            ran[0] = true;
        };
        executor.submit(task);
        Thread.sleep(100);
        assert(ran[0]);
    }
    @Test
    public void testSubmitAll() throws InterruptedException {
        final int[] counter = {0};
        Runnable task1 = () -> {
            counter[0]++;
        };
        Runnable task2 = () -> {
            counter[0]++;
        };
        executor.submitAll(java.util.Arrays.asList(task1, task2));
        Thread.sleep(200);
        assert(counter[0] == 2);
    }
    @Test
    public void testShutdown() throws InterruptedException {
        final boolean[] ran = {false};
        Runnable task = () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                
            }
            ran[0] = true;
        };
        executor.submit(task);
        executor.shutdown();
        assert(ran[0]);
    }
    
}
