import './WatchVideo.css';
import { useParams } from "react-router-dom";

function WatchVideo() {
    const { guid } = useParams();

    return (
        <>
            <h1>Watch Video {guid}</h1>
        </>
    )
}

export default WatchVideo;
