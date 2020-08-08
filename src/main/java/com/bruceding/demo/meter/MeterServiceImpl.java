package com.bruceding.demo.meter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * TODO 说明 springboot-prometheus-demo
 *
 * @author 丁静
 * @since 2020-08-08 20:18
 */
@Service
public class MeterServiceImpl implements MeterService {
    static final Object lock = new Object();
    private MeterRegistry meterRegistry;
    public MeterServiceImpl(MeterRegistry registry) {
        this.meterRegistry = registry;
    }

    @Override
    public MeterRegistry getMeterRegistry() {
        return this.meterRegistry;
    }

    @Override
    public void countMeter(String name, List<Tag> tags, double amount) {
        Counter counter = this.meterRegistry.counter(name,tags);
        counter.increment(amount);
    }

    @Override
    public void guageMeter(String name, List<Tag> tags, double amount) {
        this.meterRegistry.gauge(name, tags, amount);
    }

    @Override
    public Timer createHistogram(String name, List<Tag> tags, Duration min, Duration max, Duration... slos) {
        Timer timer = null;
        synchronized (lock) {
            if (tags == null) {
                timer = this.meterRegistry.find(name).timer();
            } else {
                timer = this.meterRegistry.find(name).tags(tags).timer();
            }
            if (timer == null) {
                timer = Timer.builder(name)
                        .minimumExpectedValue(min)
                        .maximumExpectedValue(max)
                        .serviceLevelObjectives(slos)
                        .register(this.meterRegistry);
            }
        }
        return timer;
    }

    @Override
    public Timer createSummary(String name, List<Tag> tags, Duration min, Duration max, double... percentiles) {
        Timer timer = null;
        synchronized (lock) {
            if (tags == null) {
                timer = this.meterRegistry.find(name).timer();
            } else {
                timer = this.meterRegistry.find(name).tags(tags).timer();
            }
            if (timer == null) {
                timer = Timer.builder(name)
                        .minimumExpectedValue(min)
                        .maximumExpectedValue(max)
                        .publishPercentiles(percentiles)
                        .register(this.meterRegistry);
            }
        }
        return timer;
    }
}
