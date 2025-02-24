import { EndpointMicroservice, EndpointAuthentication } from '../Environment/Endpoint';

export class AuthenticationService {
    constructor() {
        this.BASE_URL = EndpointMicroservice.authentication;
        this.JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));
    }

    async DoSignUpService(obj) {
        try {
            let url = this.BASE_URL.concat(EndpointAuthentication.do_signup);
            let response = await fetch(url, {
                method: 'POST',
                headers: {
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


    async DoLoginService(obj) {
        try {
            let url = this.BASE_URL.concat(EndpointAuthentication.do_login);
            let response = await fetch(url, {
                method: 'POST',
                headers: {
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

    async DoLogout() {
        try {
            console.log(this.JWT_TOKEN_INFO)
            let url = this.BASE_URL.concat(EndpointAuthentication.logout);
            let response = await fetch(url, {
                method: 'POST',
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


    async GetTMstUserIdFromJWTSubject(JWT) {
        try {
            let url = this.BASE_URL.concat(EndpointAuthentication.get_userid_from_jwt);
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${JWT}`,
                    'Content-Type': 'application/json',
                },
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
