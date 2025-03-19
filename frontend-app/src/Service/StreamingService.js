import { EndpointMicroservice, EndpointUpload } from '../Environment/Endpoint';
import axios from "axios";

export class StreamingService {
    constructor() {
        this.JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));
        this.BASE_URL = EndpointMicroservice.upload;
    }


    async Temp(start) {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.upload_video_info);

            const response = await fetch(`http://localhost:8092/fetch_video_chunk?start=${start}&count=2`, {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                },
            });

            return response;
        } catch (ex) {
            console.error(ex);
            return { status: 404, message: 'Internal Server Error.', data: null };
        }
    }
}
