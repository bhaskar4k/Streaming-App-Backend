import React, { useState, useEffect } from 'react';
import { Environment } from '../../Environment/Environment';
import AlertModal from '../Common-Components/AlertModal/AlertModal';
import './Upload.css';
import { UploadService } from '../../Service/UploadService';


let loadAlertModal = null;
function Upload() {
    const [file_not_uploaded, set_file_not_uploaded] = useState(true);
    const [video_pubblicity_status, set_video_pubblicity_status] = useState(0);
    const [video_upload_success, set_video_upload_success] = useState(false);
    const [progress, setProgress] = useState(0);
    const [thumbnail, set_thumbnail] = useState(null);
    const [video_info, set_video_info] = useState(null);

    const [showAlertModal, setShowAlertModal] = useState(false);
    const [headerTextOfAlertModal, setHeaderTextOfAlertModal] = useState(null);
    const [bodyTextOfAlertModal, setBodyTextOfAlertModal] = useState(null);
    const [colorOfAlertModal, setColorOfAlertModal] = useState('green');

    const uploadService = new UploadService();


    async function handleVideoUpload(event) {
        set_file_not_uploaded(false);
        document.getElementById("dropcontainer").style.height = "180px";
        let file = event.target.files[0];

        if (!file) {
            Alert(Environment.alert_modal_header_video_info_upload, Environment.colorWarning, "Please select a video file.");
            return;
        }

        const formData = new FormData();
        formData.append("video", file);

        try {
            const result = await uploadService.DoUploadVideo(formData, setProgress);
            
            if (result.data.status === 200) {
                set_video_upload_success(true);
                set_video_info(result.data.data);
            } else {
                Alert(Environment.alert_modal_header_video_info_upload, Environment.colorError, "Error uploading video! (Internal server error)");
            }
        } catch (error) {
            console.error('Error uploading video:', error);
        }
    }


    function handleVideoStatusToggleSwitch() {
        const toggle = document.getElementById('video_status_toggle');

        toggle.classList.toggle('toggle-right');

        if (video_pubblicity_status === 0) {
            set_video_pubblicity_status(1);
        } else {
            set_video_pubblicity_status(0);
        }
    }


    function validateVideoFormData(title, description) {
        let validationStatus = true;
        let warning_message = "";

        if (title === "" || title === null) {
            warning_message = "Video title can't be empty.";
            validationStatus = false;
        } else if (description === "" || description === null) {
            warning_message = "Video description can't be empty.";
            validationStatus = false;
        }

        if (validationStatus === false) {
            Alert(Environment.alert_modal_header_video_info_upload, Environment.colorWarning, warning_message);
        }

        return validationStatus;
    }


    function thumbnailUpload(event) {
        set_thumbnail(event.target.files[0]);
    }


    async function saveVideoInfo() {
        let title = document.getElementById("video_title").value;
        let description = document.getElementById("video_description").value;

        if (validateVideoFormData(title, description) === false) return;

        if (!thumbnail) {
            Alert(Environment.alert_modal_header_video_info_upload, Environment.colorWarning, "Please select a thumbnail");
            return;
        }

        let formData = new FormData();
        formData.append("title", title);
        formData.append("description", description);
        formData.append("is_public", parseInt(video_pubblicity_status));
        formData.append("thumbnail", thumbnail);
        formData.append("video_info", JSON.stringify(video_info));

        try {
            let response = await uploadService.DoUploadVideoInfo(formData);

            console.log("Success:", response);
        } catch (error) {
            console.error("Error:", error);
            Alert("Error", "red", "Failed to upload video info.");
        }
    }


    function Alert(header, color, message) {
        closeAlertModal();

        setColorOfAlertModal(color);
        openAlertModal(header, message);

        loadAlertModal = setTimeout(() => {
            closeAlertModal();
        }, 5000);
    }


    function openAlertModal(header_text, body_text) {
        setHeaderTextOfAlertModal(header_text);
        setBodyTextOfAlertModal(body_text);
        setShowAlertModal(true);
    }


    function closeAlertModal() {
        setShowAlertModal(false);
        setHeaderTextOfAlertModal(null);
        setBodyTextOfAlertModal(null);

        clearTimeout(loadAlertModal);
        loadAlertModal = null;
    }


    return (
        <>
            <div className='container-upload '>
                <AlertModal showModal={showAlertModal} handleClose={closeAlertModal} headerText={headerTextOfAlertModal} bodyText={bodyTextOfAlertModal} alertColor={colorOfAlertModal} />

                <form className='file-upload-form'>
                    <label className="drop-container" id="dropcontainer">
                        {file_not_uploaded && <span className="drop-title">Drop files here</span>}
                        {file_not_uploaded && <h3 className="drop-or-text">or</h3>}
                        {file_not_uploaded && <input type="file" accept="video/*" onChange={handleVideoUpload} required />}

                        {!file_not_uploaded &&
                            <div className='uploading-percentage-text'>
                                {!video_upload_success && <h1 class="uploading-text"></h1>}
                                {video_upload_success && <h1>Uploaded</h1>}
                                <h1>&nbsp;({progress}%)</h1>
                            </div>
                        }

                        {progress > 0 && (
                            <div className="upload_progress_bar">
                                <div className="progress_bar_container">
                                    <div className="upload_progress" style={{ width: `${progress}%` }}></div>
                                </div>
                            </div>
                        )}
                    </label>
                </form>

                <div className='title'>
                    <span>Title<span className="required_color">*</span></span>
                    <input type="text" className="upload_input upload_normal_input" id="video_title" />
                </div>

                <div className='description'>
                    <span>Description<span className="required_color">*</span></span>
                    <textarea className="upload_input upload_textarea" rows="10" id="video_description"></textarea>
                </div>

                <div className='thumbnail_and_save'>
                    <label className="drop-container-thumbnail" id="dropcontainer">
                        <span className="drop-title">Drop thumbnail here</span>
                        or
                        <input type="file" accept="image/*" onChange={thumbnailUpload} required />
                    </label>

                    <div className='video_right_side_buttons'>
                        <div className='video_publicity_switch_container'>
                            <span className='video_publicity_name'>Private</span>
                            <div className='video_publicity_switch' onClick={handleVideoStatusToggleSwitch}>
                                <div id='video_status_toggle'></div>
                            </div>
                            <span className='video_publicity_name'>Public</span>
                        </div>

                        {video_upload_success && <button className='video-save-button' onClick={saveVideoInfo}>Save</button>}
                    </div>
                </div>
            </div>
        </>
    );
}

export default Upload;