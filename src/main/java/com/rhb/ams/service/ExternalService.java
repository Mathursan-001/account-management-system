package com.rhb.ams.service;

import com.rhb.ams.dto.ExternalResponseDTO;
import com.rhb.ams.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalService {

    private final WebClient webClient;

    /**
     * Get data from external service
     *
     * @return ExternalResponseDTO containing the fetched objects
     * @throws ExternalServiceException if the external service call fails
     */
    public ExternalResponseDTO getExternalData() {
        try {
            List<JsonNode> objects = webClient
                    .get()
                    .uri("/todos")
                    .retrieve()
                    .bodyToFlux(JsonNode.class)
                    .collectList()
                    .block();

            if (objects == null) {
                log.warn("External service returned null response");
                throw new ExternalServiceException("External API", "Null response received", 500);
            }

            return ExternalResponseDTO
                    .builder()
                    .objects(objects)
                    .build();
        } catch (WebClientResponseException e) {
            log.error("External service call failed with status {}: {}", e.getStatusCode(), e.getMessage());
            throw new ExternalServiceException("External API", e.getMessage(), e.getStatusCode().value());
        } catch (Exception e) {
            log.error("External service call failed: {}", e.getMessage(), e);
            throw new ExternalServiceException("Failed to fetch data from external service: " + e.getMessage(), e);
        }
    }

}
