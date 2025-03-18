import { EndpointMicroservice, EndpointUpload } from '../Environment/Endpoint';
import axios from "axios";

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

    async DoDeleteVideo(t_video_info_id) {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.delete_video);
            let response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ t_video_info_id: t_video_info_id })
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

    async DoEditVideo(obj) {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.edit_video);
            let response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(obj)
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

    async DoDownloadVideo(guid) {
        try {
            let url = this.BASE_URL.concat(EndpointUpload.download_video);
            const response = await axios({
                url: url + `/${guid}`,
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                },
                responseType: 'blob',
            });

            if (response.status !== 200) {
                console.error('Error Message:', response.message);
            }

            return response;
        } catch (ex) {
            console.log(ex);
            return { status: 404, message: 'Internal Server Error.', data: null };
        }
    }
}
