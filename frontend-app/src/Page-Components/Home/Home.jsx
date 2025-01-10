import React, { useState, useEffect } from 'react';
import { AuthenticationService } from '../../Service/AuthenticationService';

function Home() {
    const authenticationService = new AuthenticationService();
    const JWT = JSON.parse(localStorage.getItem("JWT"));
    
    const fetchEmailFromJwt = async (jwtToken) => {
        let data = await authenticationService.GetJwtSubject(JWT);
        console.log(data);
    };

    useEffect(() => {
        fetchEmailFromJwt(JWT);
    }, []);
    return (
        <div>
            <h1>HOME</h1>
            <p>JWT Token - {JWT}</p>
        </div>
    );
}

export default Home;