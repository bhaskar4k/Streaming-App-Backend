import React, { useState, useEffect } from 'react';
import { AuthenticationService } from '../../Service/AuthenticationService';

function Home() {
    const authenticationService = new AuthenticationService();
    const JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));

    return (
        <>
            <h1>HOME</h1>
        </>
    );
}

export default Home;