package com.bruceding.demo.meter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.lang.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO 说明 springboot-prometheus-demo
 *
 * @author 丁静
 * @since 2020-08-08 20:12
 */
public interface MeterService {
    static final long DURATION_INTERVAL_NANO = 50;

    MeterRegistry getMeterRegistry();

    default void countMeter(String name, @Nullable List<Tag> tags) {
        countMeter(name, tags, 1.0);
    }

    void countMeter(String name, @Nullable List<Tag> tags, double amount);

    void guageMeter(String name, @Nullable List<Tag> tags, double amount);

    default Timer createHistogram(String name, @Nullable List<Tag> tags, Duration min, Duration max) {
        return createHistogram(name, tags, min, max, DURATION_INTERVAL_NANO);
    }

    default Timer createHistogram(String name, @Nullable List<Tag> tags, Duration min, Duration max,
                                  long interval) {
        List<Duration> durations = new ArrayList<>();

        for (long d = min.toMillis(); d <= max.toMillis(); d += interval) {
            durations.add(Duration.ofMillis(d));
        }

        return createHistogram(name, tags, min, max, durations.toArray(new Duration[durations.size()]));
    }

    Timer createHistogram(String name, @Nullable List<Tag> tags, Duration min, Duration max, Duration... slos);

    default  Timer createSummary(String name, @Nullable List<Tag> tags, Duration min, Duration max) {
        return createSummary(name, tags, min, max, 0.5, 0.75, 0.9, 0.99);
    }

    Timer createSummary(String name, @Nullable List<Tag> tags, Duration min, Duration max, double... percentiles);
}
