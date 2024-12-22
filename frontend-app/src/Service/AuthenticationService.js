import { EndpointMicroservice, EndpointAuthentication } from '../Environment/Endpoint';

export class AuthenticationService {
    constructor() {
        this.BASE_URL = EndpointMicroservice.authentication;
    }

    async DoSignUpService(obj){
        try {
            let url=EndpointMicroservice.authentication.concat(EndpointAuthentication.do_signup);
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
}
