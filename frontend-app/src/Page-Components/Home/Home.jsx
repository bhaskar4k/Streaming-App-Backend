import React, { useState, useEffect } from 'react';
import { AuthenticationService } from '../../Service/AuthenticationService';
import Upload from '../Upload/Upload';

function Home() {
    const authenticationService = new AuthenticationService();
    const JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));

    return (
        <>
            <h1>HOME</h1>
            <Upload />
        </>
    );
}

export default Home;