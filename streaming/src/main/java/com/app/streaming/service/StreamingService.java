package com.app.streaming.service;

import com.app.streaming.common.DbWorker;
import com.app.streaming.common.Util;
import com.app.streaming.entity.TLogExceptions;
import com.app.streaming.enums.UIEnum;
import com.app.streaming.environment.Environment;
import com.app.streaming.model.JwtUserDetails;
import com.app.streaming.model.VideoInformation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.nio.file.*;
import java.util.stream.Stream;

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

            if(videoInfo.getHasVideo()){
                sql_string = "select c.video_title, c.video_description, b.encoded_resolutions, a.duration, a.no_of_chunks, b.processing_status " +
                             "from streaming_app_upload.t_video_info a join streaming_app_upload.t_encoded_video_info b on a.id = b.t_video_info_id " +
                             "join streaming_app_upload.t_video_metadata c on a.id = c.t_video_info_id where a.guid = :value1";

                params = List.of(guid);

                Object result = dbWorker.getQuery(sql_string, entityManager, params, null).getSingleResult();

                if (result instanceof Object[] res) {
                    String video_title = (res[0] != null) ? (String) res[0] : "";
                    String video_description = (res[1] != null) ? (String) res[1] : "";
                    String resolutions = (res[2] != null) ? (String) res[2] : "";
                    double duration = (res[3] != null) ? ((Number) res[3]).doubleValue() : 0;
                    int noOfChunks = (res[4] != null) ? ((Number) res[4]).intValue() : 0;
                    int processingStatus = (res[5] != null) ? ((Number) res[5]).intValue() : 0;

                    if(processingStatus == UIEnum.ProcessingStatus.PROCESSED.getValue()){
                        videoInfo.setProperlyProcessed(true);
                        videoInfo.setChunkCount(noOfChunks);
                        videoInfo.setVideoDuration(duration);
                        videoInfo.setTitle(video_title);
                        videoInfo.setDescription(video_description);
                        videoInfo.setVideoResolutions(Arrays.asList(resolutions.split(",")));
                        videoInfo.setChannel(post_validated_request.getFull_name());
                    } else {
                        videoInfo.setProperlyProcessed(false);
                    }
                } else {
                    videoInfo.setProperlyProcessed(false);
                }
            }

            return videoInfo;
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(),"do_get_video_information()",e.getMessage());
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
