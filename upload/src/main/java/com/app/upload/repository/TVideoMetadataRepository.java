package com.app.upload.repository;

import com.app.upload.entity.TVideoMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TVideoMetadataRepository extends JpaRepository<TVideoMetadata, Long> { }
