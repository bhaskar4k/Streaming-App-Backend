import './WatchVideo.css';
import { useSearchParams } from "react-router-dom";

function WatchVideo() {
    const [searchParams] = useSearchParams();

    const guid = searchParams.get("v");
    const playback = searchParams.get("playback") || 0;

    return (
        <>
            <h1>Watch Video {guid} {playback}</h1>
        </>
    )
}

export default WatchVideo;
