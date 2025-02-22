package com.app.dashboard.service;

import com.app.authentication.common.DbWorker;
import com.app.dashboard.common.Util;
import com.app.dashboard.environment.Environment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Service
@Component
public class DashboardService {
    private Environment environment;
    private Util util;
    @Autowired
    private LogExceptionsService logExceptionsService;
    private DbWorker dbWorker;

    @PersistenceContext
    private EntityManager entityManager;

    private String sql_string;
    List<Object> params;

    public DashboardService(){
        this.environment = new Environment();
        this.util = new Util();
        this.dbWorker=new DbWorker();
    }
}
