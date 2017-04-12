package net.powermatcher.runtime;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.flexiblepower.context.FlexiblePowerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The runtime implementation of {@link FlexiblePowerContext}. This implementation uses a
 * {@link ScheduledThreadPoolExecutor} in order to execute tasks. You would typically use a different implementation in
 * a simulated environment.
 */
public class PowerMatcherContext
    extends ScheduledThreadPoolExecutor
    implements FlexiblePowerContext {

    static final Unit<Duration> MS = SI.MILLI(SI.SECOND);

    Properties prop = new Properties();
    InputStream input = null;

    private long now; // current simulation time in milliseconds from the epoch
    private int speedUp;// the factor the simulation is sped up by
    private long lastCheckedTime;

    private static final Logger logger = LoggerFactory.getLogger(PowerMatcherContext.class);

    /**
     * This class wraps a task and catches and logs exceptions that might occur. Normally, a scheduled tasks gets
     * cancelled when it throws an exception. Since PowerMatcher needs to be robust, we need to continue even if an
     * exception gets thrown.
     */
    static class WrappedTask<T>
        implements RunnableScheduledFuture<T> {
        private final RunnableScheduledFuture<T> task;

        public WrappedTask(RunnableScheduledFuture<T> task) {
            this.task = task;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return task.cancel(mayInterruptIfRunning);
        }

        @Override
        public int compareTo(Delayed o) {
            return task.compareTo(o);
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            return task.get();
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return task.get(timeout, unit);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return task.getDelay(unit);
        }

        @Override
        public boolean isCancelled() {
            return task.isCancelled();
        }

        @Override
        public boolean isDone() {
            return task.isDone();
        }

        @Override
        public boolean isPeriodic() {
            return task.isPeriodic();
        }

        @Override
        public void run() {
            try {
                task.run();
            } catch (Exception ex) {
                // The Exception is not thrown again to avoid this task being cancelled
                logger.error("An scheduled execution has thrown an exception: " + ex.getMessage(), ex);
            }
        }
    }

    public PowerMatcherContext() {
        // We provide a ThreadFactor so we can name the Threads, which makes debugging easier
        super(Runtime.getRuntime().availableProcessors() + 1, new ThreadFactory() {

            private final AtomicInteger cnt = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread newThread = Executors.defaultThreadFactory().newThread(r);
                newThread.setName("PowerMatcherRuntime-" + cnt.getAndIncrement());
                return newThread;
            }
        });
        setKeepAliveTime(5, TimeUnit.MINUTES);
        lastCheckedTime = now = System.currentTimeMillis();
        try {
            input = new FileInputStream(System.getProperty("user.dir") + "/res/config.properties");
            prop.load(input);
            speedUp = Integer.parseInt(prop.getProperty("speedUp"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * calcutlates how much time should have passed in our simulated world and updates now time appropriatly
     */
    public void updateNowTime() {
        long timeSinceCheck = System.currentTimeMillis() - lastCheckedTime;
        lastCheckedTime = System.currentTimeMillis();
        long simTimeSinceCheck = timeSinceCheck * speedUp;
        now += simTimeSinceCheck;

    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(final Callable<V> callable,
                                                          final RunnableScheduledFuture<V> task) {
        return new WrappedTask<V>(task);
    };

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable,
                                                          java.util.concurrent.RunnableScheduledFuture<V> task) {
        return new WrappedTask<V>(task);
    };

    @Override
    public long currentTimeMillis() {
        updateNowTime();
        return now;// System.currentTimeMillis();
    }

    @Override
    public Date currentTime() {
        updateNowTime();
        return new Date(now);// currentTimeMillis());
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, Measurable<Duration> delay) {
        return schedule(command, delay.longValue(MS) / speedUp, TimeUnit.MILLISECONDS);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, Measurable<Duration> delay) {
        return schedule(callable, delay.longValue(MS) / speedUp, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                  Measurable<Duration> initialDelay,
                                                  Measurable<Duration> period) {
        return scheduleAtFixedRate(command,
                                   initialDelay.longValue(MS) / speedUp,
                                   period.longValue(MS) / speedUp,
                                   TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                     Measurable<Duration> initialDelay,
                                                     Measurable<Duration> delay) {
        return scheduleWithFixedDelay(command,
                                      initialDelay.longValue(MS) / speedUp,
                                      delay.longValue(MS) / speedUp,
                                      TimeUnit.MILLISECONDS);
    }
}
