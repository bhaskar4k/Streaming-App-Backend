import React, { useState, useEffect } from 'react';
import { Environment } from '../../Environment/Environment';
import AlertModal from '../Common-Components/AlertModal/AlertModal';
import './Upload.css';
import removeFile from '../../../public/Images/remove-file.svg';
import { UploadService } from '../../Service/UploadService';


let loadAlertModal = null;
function Upload() {
    const [file_not_uploaded, set_file_not_uploaded] = useState(true);
    const [video_pubblicity_status, set_video_pubblicity_status] = useState(0);
    const [video_upload_success, set_video_upload_success] = useState(false);
    const [progress, setProgress] = useState(0);
    const [thumbnail, set_thumbnail] = useState(null);
    const [thumbnail_name, set_thumbnail_name] = useState("");
    const [video_info, set_video_info] = useState(null);
    const [new_video_title, set_new_video_title] = useState("");
    const [new_video_description, set_new_video_description] = useState("");

    const [max_character_title, set_max_character_title] = useState(200);
    const [max_character_description, set_max_character_description] = useState(5000);

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


    function thumbnailSave(event) {
        const file = event.target.files[0];
        if (file) {
            set_thumbnail_name(file.name);
            set_thumbnail(file);
        }
    }


    function removeThumbnail() {
        event.preventDefault();
        set_thumbnail(null);
        set_thumbnail_name("");
    }


    async function saveVideoInfo() {
        let formData = new FormData();
        formData.append("title", new_video_title);
        formData.append("description", new_video_description);
        formData.append("is_public", parseInt(video_pubblicity_status));
        formData.append("thumbnail", thumbnail);
        formData.append("video_info", JSON.stringify(video_info));

        try {
            let response = await uploadService.DoUploadVideoInfo(formData);

            if (response.status == 200) {
                Alert(Environment.alert_modal_header_video_info_upload, Environment.colorSuccess, response.message);
            } else {
                Alert(Environment.alert_modal_header_video_info_upload, Environment.colorError, response.message);
            }
        } catch (error) {
            console.error("Error:", error);
            Alert(Environment.alert_modal_header_video_info_upload, Environment.colorError, "Failed to upload video info.");
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
                        {file_not_uploaded && <span className="drop-title">Drop video files here</span>}
                        {file_not_uploaded && <span className="drop-or-text">or</span>}
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

                    <div className="input-container">
                        <input
                            type="text"
                            className="upload_input upload_normal_input"
                            id="video_title"
                            maxLength={max_character_title}
                            value={new_video_title}
                            onChange={(e) => set_new_video_title(e.target.value)}
                        />
                        <span id="charCountTitle">{new_video_title.length}/{max_character_title}</span>
                    </div>
                </div>

                <div className='description'>
                    <span>Description<span className="required_color">*</span></span>

                    <div className="input-container">
                        <textarea
                            className="upload_input upload_textarea"
                            rows="10"
                            id="video_description"
                            maxLength={max_character_description}
                            value={new_video_description}
                            onChange={(e) => set_new_video_description(e.target.value)}>                            
                        </textarea>
                        <span id="charCountDescription">{new_video_description.length}/{max_character_description}</span>
                    </div>
                </div>

                <div className='thumbnail_and_save'>
                    <label className="drop-container-thumbnail" id="dropcontainer">
                        {!thumbnail && <span className="drop-title">Drop thumbnail here</span>}
                        {!thumbnail && <span className="drop-or-text">or</span>}
                        {!thumbnail && <input type="file" accept="image/*" onChange={thumbnailSave} required />}
                        {thumbnail && <span className="drop-filename-text">{thumbnail_name}</span>}
                        {thumbnail && <span className='remove_thumbnail_btn'><img src={removeFile} className='menu-icons' onClick={removeThumbnail}></img></span>}
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