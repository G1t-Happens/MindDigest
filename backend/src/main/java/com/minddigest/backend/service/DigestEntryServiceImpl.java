package com.minddigest.backend.service;

import com.minddigest.backend.dto.DigestEntryDto;
import com.minddigest.backend.entity.DigestEntry;
import com.minddigest.backend.exception.ResourceAlreadyExistsException;
import com.minddigest.backend.exception.ResourceNotFoundException;
import com.minddigest.backend.mapper.DigestEntryMapper;
import com.minddigest.backend.repository.DigestEntryRepository;
import com.minddigest.backend.service.interfaces.DigestEntryService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service implementation for managing {@link DigestEntryDto} entities.
 * <p>
 * This service provides methods to create, update, retrieve, and delete {@link DigestEntryDto} objects.
 * It uses {@link DigestEntryRepository} for database interactions and {@link DigestEntryMapper} for entity-DTO mapping.
 * </p>
 *
 * @see DigestEntryDto
 * @see DigestEntryRepository
 * @see DigestEntryMapper
 */
@Service
public class DigestEntryServiceImpl implements DigestEntryService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DigestEntryServiceImpl.class);

    private static final String DIGEST_ENTRY = "DigestEntryDto";

    private final DigestEntryRepository digestEntryRepository;

    private final DigestEntryMapper digestEntryMapper;

    /**
     * Constructs a {@link DigestEntryServiceImpl} with the specified {@link DigestEntryRepository} and {@link DigestEntryMapper}.
     *
     * @param digestEntryRepository the repository used for {@link DigestEntry} persistence
     * @param digestEntryMapper     the mapper for converting between {@link DigestEntry} and {@link DigestEntryDto}
     */
    @Autowired
    public DigestEntryServiceImpl(DigestEntryRepository digestEntryRepository, DigestEntryMapper digestEntryMapper) {
        this.digestEntryRepository = digestEntryRepository;
        this.digestEntryMapper = digestEntryMapper;
    }

    @Override
    public List<DigestEntryDto> listAll() {
        LOGGER.debug("listAll");
        return digestEntryRepository.findAll().stream()
                .map(digestEntryMapper::toDto)
                .toList();
    }

    @Override
    public DigestEntryDto getById(Long id) throws ResourceNotFoundException {
        LOGGER.debug("getById");
        return digestEntryMapper.toDto(digestEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DIGEST_ENTRY, "id", id)));
    }

    @Override
    public DigestEntryDto save(DigestEntryDto digestEntryDto) throws ResourceAlreadyExistsException {
        LOGGER.debug("--> save");
        List<DigestEntry> digestEntries = digestEntryRepository.findBySourceUrl(digestEntryDto.getSourceUrl());

        if (digestEntries.isEmpty()) {
            // Convert DTO to entity for database persistence
            DigestEntry entity = digestEntryMapper.toEntity(digestEntryDto);
            digestEntryRepository.save(entity);
        } else {
            LOGGER.debug("<-- save, ResourceAlreadyExistsException");
            throw new ResourceAlreadyExistsException(DIGEST_ENTRY, "sourceUrl", digestEntryDto.getSourceUrl());
        }
        LOGGER.debug("<-- save");
        return digestEntryDto;
    }

    @Override
    public DigestEntryDto update(Long id, DigestEntryDto digestEntryDto) throws ResourceNotFoundException {
        LOGGER.debug("--> update");

        // Retrieve the entity by ID (throws ResourceNotFoundException if not found)
        DigestEntry entity = digestEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DIGEST_ENTRY, "id", id));

        // Map DTO to entity and ensure the ID from the path variable is set
        // This ensures we are updating the correct entity
        digestEntryMapper.updateEntityFromDto(digestEntryDto, entity);

        // Perform the update (JPA recognizes the ID and will perform an update)
        DigestEntry updatedEntity = digestEntryRepository.save(entity);

        LOGGER.debug("<-- update");
        return digestEntryMapper.toDto(updatedEntity);
    }


    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        LOGGER.debug("--> delete");

        // Log a warning before throwing the exception if the entity is not found
        DigestEntry entity = digestEntryRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.debug("<-- delete, Digest entry with ID {} not found for deletion", id);
                    return new ResourceNotFoundException(DIGEST_ENTRY, "id", id);
                });

        // If the entity was found, delete it from the database
        digestEntryRepository.delete(entity);
        LOGGER.debug("<-- delete");
    }
}
