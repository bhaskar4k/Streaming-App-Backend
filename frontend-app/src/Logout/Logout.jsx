import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { do_logout } from '../Common/Utils';


function Profile() {
    const navigate = useNavigate();
    
    useEffect(() => {
        do_logout(navigate);
    }, []);

    return (
        <>
            <h1>Profile</h1>
        </>
    )
}

export default Profile;
