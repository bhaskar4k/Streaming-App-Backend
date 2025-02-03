package com.app.processing.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class DbWorker {
    public <T> Query getQuery(String sqlStr, EntityManager entityManager, List<Object> paramValue, Class<T> resultClass) {
        //Param key should start from value1 then value2, value3, value4 and so on.
        Query query;
        if (resultClass != null) {
            query = entityManager.createNativeQuery(sqlStr, resultClass);
        } else {
            query = entityManager.createNativeQuery(sqlStr);
        }

        int i = 1;
        for (Object item : paramValue) {
            String paramKey = "value" + i++;
            query.setParameter(paramKey, item);
        }

        return query;
    }
}
