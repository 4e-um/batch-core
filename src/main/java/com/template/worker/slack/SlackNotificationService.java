package com.template.worker.slack;

import com.template.worker.health.HealthCheckResult;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotificationService {

    private final RestTemplate restTemplate;

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @Value("${project.name:Worker}")
    private String projectName;

    public void sendHealthCheckResults(List<HealthCheckResult> results) {
        boolean allHealthy = results.stream().allMatch(HealthCheckResult::isHealthy);

        StringBuilder message = new StringBuilder();
        message.append(allHealthy ? ":white_check_mark: " : ":x: ");
        message.append("*").append(projectName).append(" Health Check Report*\n\n");

        for (HealthCheckResult result : results) {
            String icon = result.isHealthy() ? ":large_green_circle:" : ":red_circle:";
            message.append(icon).append(" *").append(result.getServiceName()).append("*\n");
            message.append("   Status: ")
                    .append(result.isHealthy() ? "Healthy" : "Unhealthy")
                    .append("\n");
            message.append("   Message: ").append(result.getMessage()).append("\n");
            message.append("   Response Time: ").append(result.getResponseTimeMs()).append("ms\n");
            message.append("   Checked At: ").append(result.getCheckedAt()).append("\n\n");
        }

        sendToSlack(message.toString());
    }

    private void sendToSlack(String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> payload = Map.of("text", message);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(webhookUrl, request, String.class);
            log.info("Slack notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            throw new RuntimeException("Slack notification failed", e);
        }
    }
}
