package co.pragma.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "adapter.sns")
public record SnsTopicsProperties(
        String region,
        Map<String, String> topics
) {}
