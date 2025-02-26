import { EndpointMicroservice, EndpointUpload } from '../Environment/Endpoint';

export class ManageVideoService {
    constructor() {
        this.BASE_URL = EndpointMicroservice.upload;
        this.JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));
    }


    async GetUploadeVideoList() {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.get_uploaded_video_list);
            let response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                }
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
