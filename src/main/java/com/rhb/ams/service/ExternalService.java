package com.rhb.ams.service;

import com.rhb.ams.dto.ExternalResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalService {

    private final WebClient webClient;

    public ExternalResponseDTO getExternalData() {
        List<JsonNode> objects = webClient
                .get()
                .uri("/todos")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .collectList()
                .block();

        return ExternalResponseDTO
                .builder()
                .objects(objects)
                .build();

    }

}
