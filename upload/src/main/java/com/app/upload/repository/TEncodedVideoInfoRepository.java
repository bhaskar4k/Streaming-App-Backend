package com.app.upload.repository;

import com.app.upload.entity.TEncodedVideoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TEncodedVideoInfoRepository extends JpaRepository<TEncodedVideoInfo, Long> { }
