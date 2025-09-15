package co.pragma.metrics.aws;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricPublisher;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
public class MicrometerMetricPublisher implements MetricPublisher {
    private final ExecutorService service = Executors.newFixedThreadPool(10);
    private final MeterRegistry registry;

    @Override
    public void publish(MetricCollection metricCollection) {
        service.submit(() -> {
            List<Tag> tags = buildTags(metricCollection);
            metricCollection.stream()
                    .filter(r -> r.value() instanceof Duration || r.value() instanceof Integer)
                    .forEach(recordin -> {
                        if (recordin.value() instanceof Duration) {
                            registry.timer(recordin.metric().name(), tags).record((Duration) recordin.value());
                        } else if (recordin.value() instanceof Integer) {
                            registry.counter(recordin.metric().name(), tags).increment((Integer) recordin.value());
                        }
                    });
        });
    }

    @Override
    public void close() {
        /**/
    }

    private List<Tag> buildTags(MetricCollection metricCollection) {
        return metricCollection.stream()
                .filter(r -> r.value() instanceof String || r.value() instanceof Boolean)
                .map(recordin -> Tag.of(recordin.metric().name(), recordin.value().toString()))
                .toList();
    }
}
