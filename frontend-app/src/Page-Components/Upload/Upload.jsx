import React, { useState, useEffect } from 'react';
import './Upload.css';


import { UploadService } from '../../Service/UploadService';

function Upload() {
    const [file, setFile] = useState(null);
    const uploadService = new UploadService();
    const JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));


    function handleFileChange(event) {
        setFile(event.target.files[0]);
    }

    async function handleSubmit(event) {
        event.preventDefault();
        if (!file) {
            alert('Please select a file');
            return;
        }

        const formData = new FormData();
        formData.append("video", file);

        try {
            const result = await uploadService.DoUploadVideo(formData);

            if (result.status === 200) {
                setFile(null);
                alert("Video uploaded successfully!", result.message);
                console.log('Video uploaded successfully!:', result.status, result.message);
            } else {
                alert("Error uploading video!");
                console.error('Error uploading chunk:', result.status, result.message);
            }
        } catch (error) {
            console.error('Error uploading video:', error);
        }
    }


    return (
        <>
            <div className='container-upload '>
                <form onSubmit={handleSubmit} className='file-upload-form'>
                    <label  className="drop-container" id="dropcontainer">
                        <span className="drop-title">Drop files here</span>
                        or
                        <input type="file" accept="video/*" onChange={handleFileChange} required />
                        <button type="submit">Upload Video</button>
                    </label>
                </form>

                <span>Title<span>*</span></span>
                <input type="text" class="upload_input upload_normal_input" />

                <span>Description<span>*</span></span>
                <textarea class="upload_input upload_textarea" rows="10"></textarea>

                <div className='thumbnail_and_save'>
                    <label  className="drop-container-thumbnail" id="dropcontainer">
                        <span className="drop-title">Drop thumbnail here</span>
                        or
                        <input type="file" accept="image/*" onChange={handleFileChange} required />
                    </label>

                    <button className='video_save-button'>Save</button>
                </div>
            </div>
        </>
    );
}

export default Upload;