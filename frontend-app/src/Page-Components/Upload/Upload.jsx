import React, { useState, useEffect } from 'react';
import { AuthenticationService } from '../../Service/AuthenticationService';

function Upload() {
    const authenticationService = new AuthenticationService();
    const JWT_TOKEN_INFO = JSON.parse(localStorage.getItem("JWT"));


    return (
        <>
            <h1>Upload</h1>
        </>
    );
}

export default Upload;