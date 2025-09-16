package co.pragma.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "adapter.sqs")
public record SqsQueuesProperties(
     String region,
     Map<String, String> queues
) {}
