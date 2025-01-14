import React, { useState, useEffect } from 'react';
import { AuthenticationService } from '../../Service/AuthenticationService';

function Home() {
    const authenticationService = new AuthenticationService();
    const JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));

    useEffect(() => {
        temp();
    },[]);

    async function temp(){
        let ans = await authenticationService.GetTMstUserIdFromJWTSubject(JWT_TOKEN_INFO.jwt);
        console.log("principal",ans)
    }
    
    return (
        <div>
            <h1>HOME</h1>
        </div>
    );
}

export default Home;