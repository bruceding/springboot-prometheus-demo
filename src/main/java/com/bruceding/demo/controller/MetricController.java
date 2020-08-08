package com.bruceding.demo.controller;

import com.bruceding.demo.meter.MeterService;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO 说明 springboot-prometheus-demo
 *
 * @author 丁静
 * @since 2020-08-08 20:14
 */
@RestController
public class MetricController {
    @Autowired
    MeterService meterService;

    Timer histogram;

    Timer summary;

    @PostConstruct
    public void init() {
        //histogram = meterService.createHistogram("demo.metric.request.duration", null, Duration.ofMillis(100L),
        //        Duration.ofMillis(1000L));
        // 也可以显示的指定 bucket
        histogram = meterService.createHistogram("demo.metric.request.duration", null, Duration.ofMillis(100L),
                Duration.ofMillis(1000L), Duration.ofMillis(100L), Duration.ofMillis(200L));

        summary = meterService.createSummary("demo.metric.request.duration.summary", null, Duration.ofMillis(50L),
                Duration.ofMillis(1000L));
    }
    @RequestMapping("/metric")
    public String metric() {
        Timer.Sample sample = Timer.start();
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("api_name", "metric"));
        // count demo
        this.meterService.countMeter("demo.metric.request.count", tags);

        // guage demo
        this.meterService.guageMeter("demo.metric.guage", null, System.currentTimeMillis());

        // 模拟响应时间
        try {
           Thread.sleep(new Random().nextInt(1000));
        } catch (Exception e) {

        }

        sample.stop(histogram);
        sample.stop(summary);
        return "custom metric test";
    }

}
