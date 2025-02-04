import React, { useState, useEffect } from 'react';
import { AuthenticationService } from '../../Service/AuthenticationService';

function Upload() {
    const [file, setFile] = useState(null);
    const authenticationService = new AuthenticationService();
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

        const fileId = `${Date.now()}-${file.name}`

        const formData = new FormData();
        formData.append("video", file);
        formData.append("fileId", fileId);

        try {
            const response = await fetch('http://localhost:8093/upload/upload_video', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${JWT_TOKEN_INFO.jwt}`,
                },
                body: formData,
            });

            const result = await response.json();

            if (result.status === 200) {
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
            <h1>Upload</h1>
            <form onSubmit={handleSubmit}>
                <input type="file" accept="video/*" onChange={handleFileChange} />
                <button type="submit">Upload Video</button>
            </form>
        </>
    );
}

export default Upload;