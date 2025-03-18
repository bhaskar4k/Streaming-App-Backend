import React, { useState, useEffect, useRef } from "react";
import './WatchVideo.css';
import { useSearchParams } from "react-router-dom";
import { UploadService } from '../../Service/UploadService';

function WatchVideo() {
    const [searchParams] = useSearchParams();

    const guid = searchParams.get("v");
    const playback = searchParams.get("playback") || 0;

    const uploadService = new UploadService();

    const [videoSrc, setVideoSrc] = useState("");
    const [currentChunk, setCurrentChunk] = useState(1);
    const [videoCache, setVideoCache] = useState({});
    const videoRef = useRef(null);

    useEffect(() => {
        loadChunk(currentChunk);
    }, [currentChunk]);


    const loadChunk = async (start) => {
        const token = localStorage.getItem("jwtToken");

        if (videoCache[start]) {
            setVideoSrc(videoCache[start]); // Load from cache
            return;
        }

        try {
            const response = uploadService.Temp(start);

            console.log("Response Object:", response);

            // if (!response.ok) {
            //     const errorText = await response.text(); // Read error text for debugging
            //     console.error("Failed to fetch video chunk:", errorText);
            //     return;
            // }

            // const contentType = response.headers.get("Content-Type");
            // if (!contentType || !contentType.includes("video")) {
            //     console.error("Unexpected response type:", await response.text()); // Log unexpected responses
            //     return;
            // }

            const blob = await response.blob(); // Convert response to video blob
            const url = URL.createObjectURL(blob);

            setVideoCache((prevCache) => ({ ...prevCache, [start]: url })); // Cache chunk
            setVideoSrc(url);
        } catch (error) {
            console.error("Error fetching video chunk:", error);
        }
    };




    const handleTimeUpdate = (e) => {
        const { currentTime, duration } = e.target;
        if (currentTime > duration - 1) {
            setCurrentChunk((prev) => prev + 2);
        }
    };

    const handleSeeked = () => {
        const currentTime = videoRef.current.currentTime;
        const chunkNumber = Math.floor(currentTime / 5) + 1; // Each chunk is 5 sec

        if (videoCache[chunkNumber]) {
            setVideoSrc(videoCache[chunkNumber]); // Load from cache
        } else {
            setCurrentChunk(chunkNumber); // Fetch if not cached
        }
    };

    return (
        <video
            ref={videoRef}
            src={videoSrc}
            controls
            autoPlay
            onTimeUpdate={handleTimeUpdate}
            onSeeked={handleSeeked} // Handle seeking
        />
    );
}

export default WatchVideo;
