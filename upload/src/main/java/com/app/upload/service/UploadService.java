package com.app.upload.service;

import com.app.authentication.common.DbWorker;
import com.app.authentication.entity.TMstUser;
import com.app.upload.common.CommonReturn;
import com.app.upload.common.Util;
import com.app.upload.common.VideoUtil;
import com.app.upload.entity.TEncodedVideoInfo;
import com.app.upload.entity.TLogExceptions;
import com.app.upload.entity.TVideoInfo;
import com.app.upload.entity.TVideoMetadata;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.enums.UIEnum;
import com.app.upload.model.ProcesingStatusInputModel;
import com.app.upload.model.Video;
import com.app.upload.rabbitmq.RabbitQueuePublish;
import com.app.upload.repository.TEncodedVideoInfoRepository;
import com.app.upload.repository.TVideoInfoRepository;
import com.app.upload.repository.TVideoMetadataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.io.File;

@Service
@Component
public class UploadService {
    private Environment environment;
    private Util util;
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private TVideoInfoRepository tVideoInfoRepository;
    @Autowired
    private TEncodedVideoInfoRepository tEncodedVideoInfoRepository;
    @Autowired
    private TVideoMetadataRepository tVideoMetadataRepository;
    @Autowired
    private VideoUtil videoUtil;
    private RabbitQueuePublish rabbitQueuePublish;
    private DbWorker dbWorker;

    @PersistenceContext
    private EntityManager entityManager;

    private String sql_string;
    List<Object> params;

    public UploadService(){
        this.environment = new Environment();
        this.util = new Util();
        this.rabbitQueuePublish = new RabbitQueuePublish();
        this.dbWorker=new DbWorker();
    }


    public TVideoInfo saveVideo(MultipartFile file, JwtUserDetails userDetails) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            String VIDEO_GUID = util.getrandomGUID();
            String ORIGINAL_FILE_DIR = environment.getOriginalVideoPath() + util.getUserSpecifiedFolder(userDetails.getT_mst_user_id(),VIDEO_GUID);
            Files.createDirectories(Paths.get(ORIGINAL_FILE_DIR));

            long fileSize = file.getSize();
            String fileExtension = util.getFileExtension(file.getOriginalFilename());

            String originalFilenameWithoutExtension = util.getFileNameWithoutExtension(file.getOriginalFilename());
            String encodedFileName = VIDEO_GUID + "." + fileExtension;

            Path originalFilePath = Paths.get(ORIGINAL_FILE_DIR, encodedFileName);
            Files.write(originalFilePath, file.getBytes());

            String sourceResolution = videoUtil.getVideoResolution(originalFilePath.toString(), userDetails);
            Double duration = videoUtil.getVideoDuration(originalFilePath.toString(),userDetails);
            Long no_of_chunks = (long)Math.ceil(duration / 5L);

            List<String> validResolutions = videoUtil.getValidResolutions(sourceResolution, environment.getResolutions(), userDetails.getT_mst_user_id());

            TVideoInfo tVideoInfo = new TVideoInfo(VIDEO_GUID, originalFilenameWithoutExtension, fileSize, fileExtension, sourceResolution, duration, no_of_chunks, userDetails.getT_mst_user_id());
            TEncodedVideoInfo tEncodedVideoInfo = new TEncodedVideoInfo(String.join(",", validResolutions),
                                                                        UIEnum.ProcessingStatus.TO_BE_PROCESSED.getValue());
            TVideoMetadata tVideoMetadata = new TVideoMetadata(UIEnum.YesNo.NO.getValue(),UIEnum.YesNo.NO.getValue());

            if(saveVideoDetails(tVideoInfo,tEncodedVideoInfo,tVideoMetadata)){
                Video video = new Video(VIDEO_GUID,originalFilePath.toString(),encodedFileName,userDetails.getT_mst_user_id());
                rabbitQueuePublish.publishIntoRabbitMQ(video).getData();
                return tVideoInfo;
            }else{
                deleteVideoDetails(tVideoInfo,tEncodedVideoInfo);
                return null;
            }
        } catch (Exception e) {
            log(userDetails.getT_mst_user_id(),"saveVideo()",e.getMessage());
            return null;
        }
    }

    @Transactional
    private boolean saveVideoDetails(TVideoInfo video, TEncodedVideoInfo encodedVideoInfo, TVideoMetadata tVideoMetadata) {
        try {
            tVideoInfoRepository.save(video);
            encodedVideoInfo.setT_video_info_id(video.getId());
            tEncodedVideoInfoRepository.save(encodedVideoInfo);
            tVideoMetadata.setT_video_info_id(video.getId());
            tVideoMetadataRepository.save(tVideoMetadata);
            return true;
        } catch (Exception e) {
            log(video.getT_mst_user_id(),"saveVideoDetails()",e.getMessage());
            return false;
        }
    }

    private void deleteVideoDetails(TVideoInfo video, TEncodedVideoInfo encodedVideoInfo) {
        try {
            tVideoInfoRepository.deleteById(video.getId());
            tEncodedVideoInfoRepository.deleteById(encodedVideoInfo.getId());
        } catch (Exception e) {
            log(video.getT_mst_user_id(),"deleteVideoDetails()",e.getMessage());
        }
    }

    @Transactional
    public CommonReturn<Boolean> saveVideoMetadata(TVideoInfo video_info, String title, String description, int is_public, MultipartFile thumbnail, JwtUserDetails post_validated_request){
        try {
            TVideoMetadata tVideoMetadata = new TVideoMetadata(video_info.getId(), title, description, is_public, UIEnum.YesNo.NO.getValue());

            if (thumbnail != null && !thumbnail.isEmpty()) {
                String THUMBNAIL_FILE_DIR = environment.getOriginalThumbnailPath() + util.getUserSpecifiedFolderForThumbnail(post_validated_request.getT_mst_user_id());

                File thumbnailDir = new File(THUMBNAIL_FILE_DIR);
                if (!thumbnailDir.exists()) thumbnailDir.mkdirs();

                String fileExtension = util.getFileExtension(thumbnail.getOriginalFilename());
                File tempUploadedThumbnailFile = new File(THUMBNAIL_FILE_DIR + File.separator + video_info.getGuid() + "_TEMPCPYFILE." + fileExtension);
                thumbnail.transferTo(tempUploadedThumbnailFile);

                String ConvertedJPGFileOutputPath = THUMBNAIL_FILE_DIR + File.separator + video_info.getGuid() + ".jpg";
                boolean thumbnail_saved = convertImageToJPGFormatAndSave(tempUploadedThumbnailFile.getAbsolutePath(), ConvertedJPGFileOutputPath, post_validated_request);

                if(thumbnail_saved){
                    tempUploadedThumbnailFile.delete();
                    tVideoMetadata = new TVideoMetadata(video_info.getId(), title, description, is_public, UIEnum.YesNo.YES.getValue());
                }
            } else {
                if (alreadyHasThumbnail(video_info.getId(),post_validated_request)){
                    tVideoMetadata.setThumbnail_uploaded(UIEnum.YesNo.YES.getValue());
                }
            }

            updateVideoMetadata(video_info.getId(), tVideoMetadata.getVideo_title(), tVideoMetadata.getVideo_description(), tVideoMetadata.getIs_public(), tVideoMetadata.getThumbnail_uploaded(), post_validated_request);

            return CommonReturn.success("Video metadata has been updated successfully.",true);
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(),"saveVideoMetadata()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    private Boolean alreadyHasThumbnail(Long id, JwtUserDetails post_validated_request){
        try {
            sql_string = "select * from t_video_metadata where t_video_info_id = :value1 and thumbnail_uploaded = :value2";
            params = List.of(id,UIEnum.YesNo.YES.getValue());

            return (TVideoMetadata) dbWorker.getQuery(sql_string, entityManager, params, TVideoMetadata.class).getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(), "alreadyHasThumbnail()", e.getMessage());
            return false;
        }
    }

    @Transactional
    private int updateVideoMetadata(Long t_video_info_id, String title, String description, int is_public, int thumbnail_uploaded, JwtUserDetails post_validated_request) {
        try {
            sql_string = "UPDATE t_video_metadata SET video_title = :value1, video_description = :value2, is_public = :value3, thumbnail_uploaded = :value4 WHERE t_video_info_id = :value5";
            params = List.of(title, description, is_public, thumbnail_uploaded, t_video_info_id);

            return dbWorker.getQuery(sql_string, entityManager, params, null).executeUpdate();
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(), "updateVideoMetadata()", e.getMessage());
            return 0;
        }
    }

    private boolean convertImageToJPGFormatAndSave(String inputFilePath, String outputFilePath, JwtUserDetails post_validated_request) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    environment.getFfmpegPath(),
                    "-i", inputFilePath,
                    "-vf", "format=rgb24",
                    "-q:v", "2",
                    "-y",
                    outputFilePath
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 1) {
                log(post_validated_request.getT_mst_user_id(), "convertImageToJPGFormatAndSave()", "FFmpeg failed with exit code " + exitCode + ": " + output.toString());
                return false;
            }

            return true;
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(), "convertImageToJPGFormatAndSave()", e.getMessage());
            return false;
        }
    }

    @Transactional
    public Boolean do_update_video_processing_status(ProcesingStatusInputModel procesingStatusInputModel){
        try {
            sql_string = "UPDATE t_encoded_video_info a JOIN t_video_info b ON a.t_video_info_id = b.id " +
                         "SET a.processing_status = :value1, a.processed_at = :value2 " +
                         "WHERE b.guid = :value3";

            params = List.of(procesingStatusInputModel.getProcessing_status(), LocalDateTime.now(), procesingStatusInputModel.getVideo().getVIDEO_GUID());
            dbWorker.getQuery(sql_string, entityManager, params, null).executeUpdate();
            return true;
        } catch (Exception e) {
            log(0L,"do_update_video_processing_status()",e.getMessage());
            return false;
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