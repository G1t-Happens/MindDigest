package com.minddigest.backend.controller;

import com.minddigest.backend.dto.DigestEntryDto;
import com.minddigest.backend.exception.ResourceAlreadyExistsException;
import com.minddigest.backend.exception.ResourceNotFoundException;
import com.minddigest.backend.service.interfaces.DigestEntryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.base-path}/digest-entries")
public class DigestEntryController {

    private final DigestEntryService digestEntryService;


    @Autowired
    public DigestEntryController(DigestEntryService digestEntryService) {
        this.digestEntryService = digestEntryService;
    }

    @GetMapping
    public List<DigestEntryDto> listAll() {
        return digestEntryService.listAll();
    }

    @GetMapping("/{id}")
    public DigestEntryDto getById(@PathVariable Long id) throws ResourceNotFoundException {
        return digestEntryService.getById(id);
    }

    @PostMapping
    public DigestEntryDto create(@RequestBody @Valid DigestEntryDto digestEntry) throws ResourceAlreadyExistsException {
        return digestEntryService.save(digestEntry);
    }

    @PutMapping("/{id}")
    public DigestEntryDto update(@PathVariable Long id, @RequestBody @Valid DigestEntryDto digestEntry) throws ResourceNotFoundException {
        return digestEntryService.update(id, digestEntry);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws ResourceNotFoundException {
        digestEntryService.delete(id);
    }
}

