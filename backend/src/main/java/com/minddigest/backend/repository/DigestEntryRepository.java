package com.minddigest.backend.repository;


import com.minddigest.backend.entity.DigestEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DigestEntryRepository extends JpaRepository<DigestEntry, Long> {
}
