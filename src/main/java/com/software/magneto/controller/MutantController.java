package com.software.magneto.controller;

import com.software.magneto.service.MutantService;
import com.software.magneto.service.dto.DNAVerificationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MutantController {

    private final MutantService service;

    @PostMapping("/mutant")
    public ResponseEntity verifyDna(@RequestBody DNAVerificationRequestDTO data) {
        boolean response = service.isMutant(data);

        if (response) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
