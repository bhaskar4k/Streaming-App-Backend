import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { go_back } from '../../../Common/Utils'
import './EditVideo.css';
import AlertModal from '../../Common-Components/AlertModal/AlertModal';


let loadAlertModal = null;
function EditVideo() {
    const location = useLocation();
    const navigate = useNavigate();
    const { video_title, video_description, is_public, uploaded_at, processing_status } = location.state;

    const [thumbnail, set_thumbnail] = useState(null);
    const [thumbnail_name, set_thumbnail_name] = useState("");
    const [video_pubblicity_status, set_video_pubblicity_status] = useState(0);
    const [edited_video_title, set_edited_video_title] = useState("");
    const [edited_video_description, set_edited_video_description] = useState("");

    const [showAlertModal, setShowAlertModal] = useState(false);
    const [headerTextOfAlertModal, setHeaderTextOfAlertModal] = useState(null);
    const [bodyTextOfAlertModal, setBodyTextOfAlertModal] = useState(null);
    const [colorOfAlertModal, setColorOfAlertModal] = useState('green');

    useEffect(() => {
        set_edited_video_title(video_title);
        set_edited_video_description(video_description);
        if(is_public === 1) handleVideoStatusToggleSwitch();
    }, [])

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


    async function saveVideoInfo() {
        let title = document.getElementById("video_title").value;
        let description = document.getElementById("video_description").value;

        let formData = new FormData();
        formData.append("title", title);
        formData.append("description", description);
        formData.append("is_public", parseInt(video_pubblicity_status));
        formData.append("thumbnail", thumbnail);

        // try {
        //     let response = await uploadService.DoUploadVideoInfo(formData);

        //     if (response.status == 200) {
        //         Alert(Environment.alert_modal_header_video_info_upload, Environment.colorSuccess, response.message);
        //     } else {
        //         Alert(Environment.alert_modal_header_video_info_upload, Environment.colorError, response.message);
        //     }
        // } catch (error) {
        //     console.error("Error:", error);
        //     Alert(Environment.alert_modal_header_video_info_upload, Environment.colorError, "Failed to upload video info.");
        // }
    }

    return (
        <>
            <div className='container-upload '>
                <h1>Edit - {video_title}</h1>

                <AlertModal showModal={showAlertModal} handleClose={closeAlertModal} headerText={headerTextOfAlertModal} bodyText={bodyTextOfAlertModal} alertColor={colorOfAlertModal} />

                <div className='title'>
                    <span>Title<span className="required_color">*</span></span>
                    <input type="text" className="upload_input upload_normal_input" id="video_title" 
                        value={edited_video_title} onChange={(e) => set_edited_video_title(e.target.value)}/>
                </div>

                <div className='description'>
                    <span>Description<span className="required_color">*</span></span>
                    <textarea className="upload_input upload_textarea" rows="10" id="video_description" 
                        value={edited_video_description} onChange={(e) => set_edited_video_description(e.target.value)}></textarea>
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

                        <button className='video-save-button' onClick={saveVideoInfo}>Save</button>
                    </div>
                </div>

                <button className='back-button' onClick={() => go_back(navigate)}>Go Back</button>
            </div>
        </>
    );
}

export default EditVideo;