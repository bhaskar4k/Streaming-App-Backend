import React, { useEffect, useRef, useState } from "react";
import './WatchVideo.css';
import { useSearchParams } from "react-router-dom";
import { UploadService } from '../../Service/UploadService';

function WatchVideo() {
    const [searchParams] = useSearchParams();

    const guid = searchParams.get("v");
    const playback = searchParams.get("playback") || 0;

    const uploadService = new UploadService();

    const [videoChunks, setVideoChunks] = useState([]);
    const [currentChunkIndex, setCurrentChunkIndex] = useState(0);
    const videoRef = useRef(null);

    useEffect(() => {
        async function fetchChunks() {
            try {
                const response = await uploadService.Temp(1);
                console.log(response)
                if (!response.ok) throw new Error("Failed to fetch chunks");
                const chunks = await response.json();
                console.log(chunks)
                setVideoChunks(chunks);
            } catch (error) {
                console.error("Error fetching video chunks:", error);
            }
        }
        fetchChunks();
    }, []);

    const handleVideoEnd = () => {
        if (currentChunkIndex < videoChunks.length - 1) {
            setCurrentChunkIndex(currentChunkIndex + 1);
        }
    };

    return (
        <div>
            {videoChunks.length > 0 ? (
                <video
                    ref={videoRef}
                    controls
                    autoPlay
                    onEnded={handleVideoEnd}
                    src={videoChunks[currentChunkIndex]}
                />
            ) : (
                <p>Loading video...</p>
            )}
        </div>
    );
}

export default WatchVideo;
