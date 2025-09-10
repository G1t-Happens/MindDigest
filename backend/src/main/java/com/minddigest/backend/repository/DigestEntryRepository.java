package com.minddigest.backend.repository;

import com.minddigest.backend.entity.DigestEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DigestEntryRepository extends JpaRepository<DigestEntry, Long> {

    List<DigestEntry> findBySourceUrl(String sourceUrl);

}
