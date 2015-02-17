package hangman.util;


import com.google.common.util.concurrent.Striped;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;


/**
 * Monitor protecting code from race conditions by utilizing ReentrantLock.
 * Available configuration options are:
 * - concurrencyLevel: number of threads accessing critical section at the same time,
 * - time: maximum time to wait for access to critical section,
 * - unit: time unit for the {@code time} parameter.
 *
 * @param <K> the type of key value that monitor is locking on
 * @param <V> the type of protected value
 */
public class AccessMonitor<K, V> {
    private final Striped<Lock> locks;
    private final long time;
    private final TimeUnit unit;


    /**
     * Create instance of ReadWriteMonitor
     *
     * @param concurrencyLevel number of thread expected to access protected section simultaneously
     * @param time             the maximum time to wait for obtaining the lock
     * @param unit             the time unit of the time argument
     */
    public AccessMonitor(int concurrencyLevel, long time, TimeUnit unit) {
        isTrue(concurrencyLevel > 0);
        isTrue(time > 0);
        notNull(unit);

        this.locks = Striped.lazyWeakLock(concurrencyLevel);
        this.time = time;
        this.unit = unit;
    }

    /**
     * Execute task in safe manner: prevents race conditions
     * Monitor tries to obtain the lock for specified period of time.
     * If the lock is not available TimeoutException is thrown
     *
     * @param key  key to lock on
     * @param task task to execute
     * @return value of the operation
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public V execute(K key, Supplier<V> task) throws InterruptedException, TimeoutException {
        notNull(key);
        notNull(task);

        final Lock lock = locks.get(key);
        if (lock.tryLock(time, unit)) {
            try {
                return task.get();
            } finally {
                lock.unlock();
            }
        } else {
            throw new TimeoutException("Acquiring lock timeout");
        }
    }

    /**
     * Execute task in safe manner: prevent race conditions
     * Operation won't throw checked exception. In case of lock accessing failure MonitorException is thrown
     *
     * @param key  key to lock on
     * @param task task to execute
     * @return value of the operation
     * @throws MonitorException on lock acquiring failure
     */
    public V tryExecute(K key, Supplier<V> task) {
        try {
            return execute(key, task);
        } catch (InterruptedException | TimeoutException e) {
            throw new MonitorException(e);
        }
    }


    public static class MonitorException extends RuntimeException {
        public MonitorException(Throwable cause) {
            super(cause);
        }
    }
}
