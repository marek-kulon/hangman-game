package hangman.util;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Test AccessMonitor.execute(K, Supplier<V>)) blocks access for the same key and throws TimeoutException on entering monitor
 * Scenario:
 * - thread 1 enters monitor and executes long-running job (where job time > monitor acquiring timeout),
 * - thread 2 tries to enter monitor and execute its job
 * Expected results:
 * - thread 1 executes fine
 * - thread 2 receives TimeoutException
 *
 * Instead of Thread.sleep()/CountDownLatch implementation uses MultithreadedTC for ordering threads
 */
public class AccessMonitorExecuteTimeoutTest extends MultithreadedTestCase {
    private static final Logger log = LoggerFactory.getLogger(AccessMonitorExecuteTimeoutTest.class);

    private static final String KEY = "key";
    private static final int MONITOR_TIME = 100;

    private AccessMonitor<String, String> monitor;

    @Override
    public void initialize() {
        monitor = new AccessMonitor<>(1, MONITOR_TIME, MILLISECONDS);
    }

    /**
     * thread 1 successfully enters monitor and executes its job
     */
    public void thread1() {
        waitForTick(1);
        String res = "NOK";
        try {
            log.info("thread 1 about to enter monitor");
            res = monitor.execute(KEY, () -> {
                log.info("thread 1 entered the monitor");
                waitForTick(3);

                freezeClock(); // disable clock thread -> make sure thread 1 isn't captured
                trySleep(500);
                unfreezeClock();

                log.info("thread 1 about to exit the monitor");
                return "OK";
            });
        } catch (InterruptedException e) {
            fail("IE occurred");
        } catch (TimeoutException e) {
            fail("TE occurred");
        }
        assertEquals("OK", res);
        log.info("thread 1 finished");
    }


    /**
     * thread 2 can't enter monitor and times out
     */
    public void thread2() {
        final long start = System.currentTimeMillis();
        try {
            waitForTick(2);
            log.info("thread 2 about to enter monitor");
            monitor.execute(KEY, () -> {
                log.error("thread 2 entered the monitor");
                fail();
                return null;
            });
        } catch (InterruptedException e) {
            fail("IE occurred");
        } catch (TimeoutException e) {
            long acquiringTime = System.currentTimeMillis() - start;
            log.info("thread 2 timeout occurred, acquiring time: {}", acquiringTime);
            assertTrue("Monitor tried to acquire lock for minimum provided time", acquiringTime >= MONITOR_TIME);
            log.info("thread 2 finished");
            return;
        }
        fail();
    }

    @Test // no need for timeout here - framework provides it
    public void executeThrowsMonitorExceptionTest() throws Throwable {
        TestFramework.runOnce(new AccessMonitorExecuteTimeoutTest());
    }


    private void trySleep(int timeInMills) {
        try {
            MILLISECONDS.sleep(timeInMills);
        } catch (InterruptedException e) {
            fail("IE occurred");
        }
    }
}
