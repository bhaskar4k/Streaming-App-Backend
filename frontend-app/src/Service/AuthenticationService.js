import { EndpointMicroservice, EndpointAuthentication } from '../Environment/Endpoint';

export class AuthenticationService {
    constructor() {
        this.BASE_URL = EndpointMicroservice.authentication;
    }

    async DoSignUpService(obj){
        try {
            let url=this.BASE_URL.concat(EndpointAuthentication.do_signup);
            let response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(obj)
            });
      
            let res = await response.json();
            return res;
        } catch (ex){
            console.log(ex);
            return {status : 404, message : 'Internal Server Error.', data : null};
        }
    }   

    async DoLoginService(obj){
        try {
            let url=this.BASE_URL.concat(EndpointAuthentication.do_login);
            let response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(obj)
            });
      
            let res = await response.json();
            return res;
        } catch (ex){
            console.log(ex);
            return {status : 404, message : 'Internal Server Error.', data : null};
        }
    }   

    async GetJwtSubject(JWT){
        try {
            const response = await fetch('http://localhost:8090/authentication/get_userid_from_jwt', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${JWT}`,
                    'Content-Type': 'application/json',
                },
            });
    
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
    
            const res = await response.json();
            const JWT_subject = JSON.parse(res.data);
            console.log('Subject:', JWT_subject);
            return JWT_subject; 
        } catch (error) {
            console.error('Error fetching email from JWT:', error.message);
            return null;
        }
    }
}
