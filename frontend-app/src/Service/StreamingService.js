import { EndpointMicroservice, EndpointStreaming } from '../Environment/Endpoint';
import axios from "axios";

export class StreamingService {
    constructor() {
        this.JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));
        this.BASE_URL = EndpointMicroservice.streaming;
    }


    async VideoFileInfo(guid) {
        try {
            let url = this.BASE_URL.concat(EndpointStreaming.get_video_information);
            let response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.JWT_TOKEN_INFO.jwt}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ guid: guid })
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
