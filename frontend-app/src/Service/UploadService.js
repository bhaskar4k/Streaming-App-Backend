import { EndpointMicroservice, EndpointUpload } from '../Environment/Endpoint';

export class UploadService {
    constructor() {
        this.JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));
        this.BASE_URL = EndpointMicroservice.upload;
    }

    async DoUploadVideo(obj) {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.upload_video);
            console.log(obj)
            let response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                },
                body: obj,
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
