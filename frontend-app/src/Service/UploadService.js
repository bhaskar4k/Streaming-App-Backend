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

            if (!response.ok) {
                const errorData = await response.json();
                console.error('Error Message:', errorData.message);
            }

            let res = await response.json();
            return res;
        } catch (ex) {
            console.log(ex);
            return { status: 404, message: 'Internal Server Error.', data: null };
        }
    }
}
