import React, { useState, useEffect } from 'react';
import { AuthenticationService } from '../../Service/AuthenticationService';

function Home() {
    const authenticationService = new AuthenticationService();
    const JWT = JSON.parse(localStorage.getItem("JWT"));
    
    return (
        <div>
            <h1>HOME</h1>
        </div>
    );
}

export default Home;