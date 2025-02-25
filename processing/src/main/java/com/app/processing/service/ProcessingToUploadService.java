package com.app.processing.service;

import com.app.processing.Enums.UIEnum;
import com.app.processing.common.CommonReturn;
import com.app.processing.common.Util;
import com.app.processing.entity.TLogExceptions;
import com.app.processing.environment.Environment;
import com.app.processing.model.ProcessingStatusInputModel;
import com.app.processing.model.Video;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Service
@Component
public class ProcessingToUploadService {
    private Environment environment;
    private Util util;
    @Autowired
    private LogExceptionsService logExceptionsService;

    public ProcessingToUploadService(){
        this.environment = new Environment();
        this.util = new Util();
    }

    public CommonReturn<Boolean> update_processing_status_in_db(Video video, int status_code){
        ProcessingStatusInputModel processingStatusInputModel =
                new ProcessingStatusInputModel(video, UIEnum.ProcessingStatus.PROCESSED.getValue());

        String UPDATE_VIDEO_PROCESSING_STATUS_URL = environment.getUpdateVideoProcessingStatus();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProcessingStatusInputModel> entity = new HttpEntity<>(processingStatusInputModel, headers);

        try {
            ResponseEntity<CommonReturn<Boolean>> response = restTemplate.exchange(
                    UPDATE_VIDEO_PROCESSING_STATUS_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<CommonReturn<Boolean>>() {}
            );

            return CommonReturn.success("Processing status has been updated successfully.", true);
        } catch (Exception e) {
            log(0L, "updateProcessingStatus()", e.getMessage());  // Assuming log method exists
            return CommonReturn.error(400, "Internal Server Error: " + e.getMessage());
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
