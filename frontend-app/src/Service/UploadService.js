import { EndpointMicroservice, EndpointUpload } from '../Environment/Endpoint';
import axios from "axios";

export class UploadService {
    constructor() {
        this.JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));
        this.BASE_URL = EndpointMicroservice.upload;
    }

    async DoUploadVideo(obj, onProgress) {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.upload_video);

            const response = await axios.post(url, obj, {
                headers: {
                    'Authorization': `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                },
                onUploadProgress: (progressEvent) => {
                    const percentCompleted = Math.round(
                        (progressEvent.loaded * 100) / progressEvent.total
                    );
                    if (onProgress) {
                        onProgress(percentCompleted);
                    }
                },
            });

            if (response.status != 200) {
                const errorData = await response.json();
                console.error('Error Message:', errorData.message);
            }

            return response;
        } catch (ex) {
            console.log(ex);
            return { status: 404, message: 'Internal Server Error.', data: null };
        }
    }


    async DoUploadVideoInfo(obj) {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.upload_video_info);

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                },
                body: obj
            });

            if (!response.ok) {
                const errorData = await response.json();
                console.error('Error Message:', errorData.message);
            }

            const res = await response.json();
            return res;
        } catch (ex) {
            console.error(ex);
            return { status: 404, message: 'Internal Server Error.', data: null };
        }
    }


    async DoUpdateVideoInfo(obj) {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.update_video_info);

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                },
                body: obj
            });

            if (!response.ok) {
                const errorData = await response.json();
                console.error('Error Message:', errorData.message);
            }

            const res = await response.json();
            return res;
        } catch (ex) {
            console.error(ex);
            return { status: 404, message: 'Internal Server Error.', data: null };
        }
    }



    async Temp(start) {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.upload_video_info);

            const response = await fetch(`http://localhost:8093/manage_video/video?start=${start}&count=2`, {
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
