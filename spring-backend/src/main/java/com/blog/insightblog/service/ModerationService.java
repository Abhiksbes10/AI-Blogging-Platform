package com.blog.insightblog.service;

import com.blog.insightblog.dto.ModerationRequest;
import com.blog.insightblog.dto.ModerationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ModerationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public ModerationResponse analyze(String text, String userId) {

        String url = "http://127.0.0.1:8000/analyze";

        ModerationRequest req = new ModerationRequest();
        req.setText(text);
        req.setUser_id(userId);

        int attempts = 0;
        int maxAttempts = 3;

        while (attempts < maxAttempts) {
            try {
                log.info("Calling NLP service (attempt {}): {}", attempts + 1, text);

                return restTemplate.postForObject(
                        url,
                        req,
                        ModerationResponse.class
                );

            } catch (Exception ex) {
                attempts++;
                log.error("NLP call failed (attempt {}): {}", attempts, ex.getMessage());

                if (attempts == maxAttempts) {
                    log.error("NLP service down → using fallback");

                    //  FALLBACK RESPONSE
                    ModerationResponse fallback = new ModerationResponse();
                    fallback.setStatus("ALLOWED");
                    fallback.setMessage("NLP service unavailable - fallback used"); //  NEW
                    fallback.setCleanedText(text);
                    fallback.setScore(0.0);
                    fallback.setSentiment(0.0);
                    fallback.setKeywords(null);
                    fallback.setStrikes(0);

                    return fallback;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
        }

        return null;
    }
}