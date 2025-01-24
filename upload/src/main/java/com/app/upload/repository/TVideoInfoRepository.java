package com.app.upload.repository;

import com.app.upload.entity.TVideoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TVideoInfoRepository extends JpaRepository<TVideoInfo, Long> { }
