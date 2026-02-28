package com.rhb.ams.controller;

import com.rhb.ams.dto.ExternalResponseDTO;
import com.rhb.ams.service.CustomerService;
import com.rhb.ams.service.ExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/objects")
@RequiredArgsConstructor
public class ExternalCallController {

    private final ExternalService externalService;

    @GetMapping
    public ResponseEntity<ExternalResponseDTO> callExternalService() {

        ExternalResponseDTO externalResponseDTO = externalService.getExternalData();
        return ResponseEntity.ok(externalResponseDTO);

    }

}
