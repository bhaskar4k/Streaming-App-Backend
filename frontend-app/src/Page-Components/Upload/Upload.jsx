import React, { useState, useEffect } from 'react';
import { AuthenticationService } from '../../Service/AuthenticationService';

function Upload() {
    const [file, setFile] = useState(null);
    const authenticationService = new AuthenticationService();
    const JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));
    const CHUNK_SIZE = 5 * 1024 * 1024; // 5MB

    function handleFileChange(event) {
        setFile(event.target.files[0]);
    }

    async function handleSubmit(event) {
        event.preventDefault();
        if (!file) {
            alert('Please select a file');
            return;
        }
    
        const CHUNK_SIZE = 5 * 1024 * 1024; // 5MB
        const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
        const fileId = `${file.name}-${Date.now()}`;
        let uploadedChunks = 0;
    
        // for (let start = 0; start < file.size; start += CHUNK_SIZE) {
        //     const end = Math.min(start + CHUNK_SIZE, file.size);
        //     const chunk = file.slice(start, end);
        //     const chunkIndex = Math.floor(start / CHUNK_SIZE);
    
            const formData = new FormData();
            formData.append("chunk", file);
            formData.append("fileId", "abc");
            formData.append("chunkIndex", 1);
            formData.append("totalChunks", 1);
    
            try {
                const response = await fetch('http://localhost:8093/upload/upload', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${JWT_TOKEN_INFO.jwt}`,
                    },
                    body: formData,
                });
    
                const result = await response.json();
    
                if (result.status === 200) {
                    alert("Video uploaded successfully!", result.message);
                } else {
                    console.error('Error uploading chunk:', result.status, result.message);
                }
            } catch (error) {
                console.error('Error uploading chunk:', error);
            }
        // }
    
        if (uploadedChunks === totalChunks) {
            alert("Video uploaded successfully!");
        } else {
            alert("Video upload failed. Please try again.");
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