import { EndpointMicroservice, EndpointDashboard } from '../Environment/Endpoint';


export class DashboardService {
    constructor() {
        this.JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));
        this.BASE_URL = EndpointMicroservice.dashboard;
    }


    async DoGetMenu(obj) {
        try {
            let url = this.BASE_URL.concat(EndpointDashboard.menu);

            const response = await fetch(url, {
                method: 'GET',
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
}
