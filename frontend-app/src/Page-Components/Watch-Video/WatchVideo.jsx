import React, { useEffect, useRef, useState } from "react";
import './WatchVideo.css';
import { useSearchParams } from "react-router-dom";
import { UploadService } from '../../Service/UploadService';
import like from '../../../public/Images/like.png';
import dislike from '../../../public/Images/dislike.png';
import share from '../../../public/Images/share.svg';
import profile from '../../../public/Images/profile.svg';

function WatchVideo() {
    const [searchParams] = useSearchParams();

    const guid = searchParams.get("v");
    const playback = searchParams.get("playback") || 0;

    const uploadService = new UploadService();

    const [isLiked, setIsLiked] = useState(0);
    const [isDisliked, setIsDisliked] = useState(0);

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


    useEffect(() => {
        const likeButton = document.getElementById("video_player_info_action_like");
        const dislikeButton = document.getElementById("video_player_info_action_dislike");

        const handleLikeClick = () => {
            if (isLiked === 0) {
                likeButton.style.backgroundColor = "rgb(255, 145, 0)";
                dislikeButton.style.backgroundColor = "rgb(210, 210, 210)";
                setIsLiked(1);
            } else {
                likeButton.style.backgroundColor = "rgb(210, 210, 210)";
                setIsLiked(0);
            }

            setIsDisliked(0);
        };

        const handleDislikeClick = () => {
            if (isDisliked === 0) {
                dislikeButton.style.backgroundColor = "rgb(255, 145, 0)";
                likeButton.style.backgroundColor = "rgb(210, 210, 210)";
                setIsDisliked(1);
            } else {
                dislikeButton.style.backgroundColor = "rgb(210, 210, 210)";
                setIsDisliked(0);
            }

            setIsLiked(0);
        };

        likeButton.addEventListener("click", handleLikeClick);
        dislikeButton.addEventListener("click", handleDislikeClick);

        return () => {
            likeButton.removeEventListener("click", handleLikeClick);
            dislikeButton.removeEventListener("click", handleDislikeClick);
        };
    }, [isLiked, isDisliked]);

    return (
        <>
            <div className="video_player_container">
                <div id="video_player">

                </div>

                <div className="video_player_info">
                    <div className="video_player_info_header">
                        <span className="video_player_info_title">Video title</span>

                        <div className="video_player_info_header_action">
                            <div className="video_player_info_channel_info">
                                <img src={profile}></img>
                                <span className="video_player_info_channel">Channel</span>
                            </div>

                            <div className="video_player_info_action">
                                <div id="video_player_info_action_like">
                                    <img src={like}></img>
                                </div>
                                <div id="video_player_info_action_dislike">
                                    <img src={dislike}></img>
                                </div>
                                <div id="video_player_info_action_share">
                                    <img src={share}></img>
                                </div>
                            </div>
                        </div>
                    </div>
                    <span className="video_player_info_description">Video description</span>
                </div>
            </div>

        </>
    );
}

export default WatchVideo;
