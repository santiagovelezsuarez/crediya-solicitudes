package co.pragma.sns.publisher;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "adapter.sqs")
public record SqsQueuesProperties(
     String region,
     Map<String, String> queues
) {}
