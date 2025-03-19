package com.app.upload.service;

import com.app.authentication.common.DbWorker;
import com.app.upload.common.Util;
import com.app.upload.entity.TLogExceptions;
import com.app.upload.enums.UIEnum;
import com.app.upload.environment.Environment;
import com.app.upload.model.JwtUserDetails;
import com.app.upload.model.ManageVideoDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Component
public class ManageVideoService {
    private Environment environment;
    private Util util;
    @Autowired
    private LogExceptionsService logExceptionsService;
    private DbWorker dbWorker;

    @PersistenceContext
    private EntityManager entityManager;

    private String sql_string;
    List<Object> params;

    public ManageVideoService(){
        this.environment = new Environment();
        this.util = new Util();
        this.dbWorker=new DbWorker();
        this.params=new ArrayList<>();
    }


    public List<ManageVideoDetails> do_get_uploaded_video_list(JwtUserDetails post_validated_request){
        try {
            sql_string = "select a.id, a.guid, b.video_title, b.video_description, b.is_public, b.thumbnail_uploaded, a.trans_datetime, c.processing_status " +
                    "from t_video_info a left join t_video_metadata b on a.id = b.t_video_info_id " +
                    "left join t_encoded_video_info c on c.t_video_info_id = b.t_video_info_id " +
                    "where a.t_mst_user_id = :value1 and a.is_active = :value2 order by a.id desc";

            params = List.of(post_validated_request.getT_mst_user_id(), UIEnum.Activity.IS_ACTIVE.getValue());

            List<Object[]> results = dbWorker.getQuery(sql_string, entityManager, params, null).getResultList();
            List<ManageVideoDetails> manageVideos = new ArrayList<>();

            for (Object[] row : results) {
                Long id = (row[0] != null) ? ((Number) row[0]).longValue() : null;
                String guid = (row[1] != null) ? (String) row[1] : "";
                String videoTitle = (row[2] != null) ? (String) row[2] : "";
                String videoDescription = (row[3] != null) ? (String) row[3] : "";
                int isPublic = (row[4] != null) ? ((Number) row[4]).intValue() : 0;
                int thumbnailUploaded = (row[5] != null) ? ((Number) row[5]).intValue() : 0;
                LocalDateTime transDatetime = (row[6] != null) ? ((Timestamp) row[6]).toLocalDateTime() : null;
                int processingStatus = (row[7] != null) ? ((Number) row[7]).intValue() : 0;

                String thumbnailPath = environment.getOriginalThumbnailPath() + util.getUserSpecifiedFolderForThumbnail(guid) +
                                       File.separator + guid + ".jpg";

                File file = new File(thumbnailPath);
                String base64EncodedImage = null;

                if (file.exists()) {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    base64EncodedImage = Base64.getEncoder().encodeToString(fileContent);
                }

                ManageVideoDetails video = new ManageVideoDetails(id, guid, videoTitle, videoDescription, isPublic, thumbnailUploaded, base64EncodedImage, transDatetime, processingStatus);
                manageVideos.add(video);
            }

            return manageVideos;
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(),"do_get_uploaded_video_list()",e.getMessage());
            return null;
        }
    }

    @Transactional
    public Boolean do_delete_video(Long t_video_info_id, JwtUserDetails post_validated_request){
        try {
            sql_string = "UPDATE t_video_info set is_active = :value1 where id = :value2";

            params = List.of(UIEnum.Activity.INACTIVE.getValue(), t_video_info_id);
            dbWorker.getQuery(sql_string, entityManager, params, null).executeUpdate();
            return true;
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(),"do_delete_video()",e.getMessage());
            return false;
        }
    }

    public ResponseEntity<Resource> do_download_video(String guid, JwtUserDetails post_validated_request) {
        try {
            String ORIGINAL_FILE_DIR = environment.getOriginalVideoPath() + util.getUserSpecifiedFolder(guid);
            String FILENAME = File.separator + guid + ".mp4";

            Path filePath = Paths.get(ORIGINAL_FILE_DIR, FILENAME);
            org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("video/mp4"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(),"download_video()",e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    public List<ManageVideoDetails> do_get_deleted_video_list(JwtUserDetails post_validated_request){
        try {
            sql_string = "select a.id, a.guid, b.video_title, b.video_description, b.is_public, b.thumbnail_uploaded, a.trans_datetime, c.processing_status " +
                    "from t_video_info a left join t_video_metadata b on a.id = b.t_video_info_id " +
                    "left join t_encoded_video_info c on c.t_video_info_id = b.t_video_info_id " +
                    "where a.t_mst_user_id = :value1 and a.is_active = :value2 order by a.id desc";

            params = List.of(post_validated_request.getT_mst_user_id(), UIEnum.Activity.INACTIVE.getValue());

            List<Object[]> results = dbWorker.getQuery(sql_string, entityManager, params, null).getResultList();
            List<ManageVideoDetails> manageVideos = new ArrayList<>();

            for (Object[] row : results) {
                Long id = (row[0] != null) ? ((Number) row[0]).longValue() : null;
                String guid = (row[1] != null) ? (String) row[1] : "";
                String videoTitle = (row[2] != null) ? (String) row[2] : "";
                String videoDescription = (row[3] != null) ? (String) row[3] : "";
                int isPublic = (row[4] != null) ? ((Number) row[4]).intValue() : 0;
                int thumbnailUploaded = (row[5] != null) ? ((Number) row[5]).intValue() : 0;
                LocalDateTime transDatetime = (row[6] != null) ? ((Timestamp) row[6]).toLocalDateTime() : null;
                int processingStatus = (row[7] != null) ? ((Number) row[7]).intValue() : 0;

                String thumbnailPath = environment.getOriginalThumbnailPath() + util.getUserSpecifiedFolderForThumbnail(guid) +
                                       File.separator + guid + ".jpg";

                File file = new File(thumbnailPath);
                String base64EncodedImage = null;

                if (file.exists()) {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    base64EncodedImage = Base64.getEncoder().encodeToString(fileContent);
                }

                ManageVideoDetails video = new ManageVideoDetails(id, guid, videoTitle, videoDescription, isPublic, thumbnailUploaded, base64EncodedImage, transDatetime, processingStatus);
                manageVideos.add(video);
            }

            return manageVideos;
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(),"do_get_uploaded_video_list()",e.getMessage());
            return null;
        }
    }

    @Transactional
    public Boolean do_restore_video(Long t_video_info_id, JwtUserDetails post_validated_request){
        try {
            sql_string = "UPDATE t_video_info set is_active = :value1 where id = :value2";

            params = List.of(UIEnum.Activity.IS_ACTIVE.getValue(), t_video_info_id);
            dbWorker.getQuery(sql_string, entityManager, params, null).executeUpdate();
            return true;
        } catch (Exception e) {
            log(post_validated_request.getT_mst_user_id(),"do_restore_video()",e.getMessage());
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
