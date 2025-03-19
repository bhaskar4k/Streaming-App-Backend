package com.app.streaming.service;

import com.app.streaming.common.DbWorker;
import com.app.streaming.common.Util;
import com.app.streaming.entity.TLogExceptions;
import com.app.streaming.environment.Environment;
import com.app.streaming.model.JwtUserDetails;
import com.app.streaming.model.VideoInformation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.*;

@Component
public class StreamingService {
    private Environment environment;
    private Util util;
    @Autowired
    private LogExceptionsService logExceptionsService;
    private DbWorker dbWorker;

    @PersistenceContext
    private EntityManager entityManager;

    private String sql_string;
    List<Object> params;

    public StreamingService(){
        this.environment = new Environment();
        this.util = new Util();
        this.dbWorker=new DbWorker();
        this.params=new ArrayList<>();
    }

    public VideoInformation do_get_video_information(String guid, JwtUserDetails post_validated_request){
        try {
            VideoInformation videoInfo = new VideoInformation();

            Path filePath = Paths.get(environment.getEncodedVideoPath() + util.getUserSpecifiedFolder(guid));

            videoInfo.setHasVideo(Files.exists(filePath));

            return videoInfo;
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(),"do_restore_video()",e.getMessage());
            return null;
        }
    }


    private void log(Long t_mst_user_id, String function_name, String exception_msg){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String full_class_path = stackTraceElements[2].getClassName();
        String class_name = full_class_path.substring(full_class_path.lastIndexOf(".") + 1);

        String full_package_path = full_class_path.substring(0, full_class_path.lastIndexOf("."));
        String package_name = full_package_path.substring(full_package_path.lastIndexOf(".") + 1);

        logExceptionsService.saveLogException(new TLogExceptions(package_name,class_name,function_name,exception_msg,t_mst_user_id));
    }
}
