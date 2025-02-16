import React from 'react';
import './Layout.css';
import { Outlet } from "react-router-dom";
import { useState, useEffect } from 'react';
import home from '../../public/Images/home.svg';
import dashboard from '../../public/Images/dashboard.svg';
import upload from '../../public/Images/upload.svg';
import profile from '../../public/Images/profile.svg';
import logout from '../../public/Images/logout.svg';
import bars from '../../public/Images/bars.svg';

function Layout() {
    useEffect(() => {

    }, []);

    function toggleSidebar(){
        const menubar = document.getElementById('menubar');

        let newWidthMenubar, newWidthMainContent, newDisplay;
        if(document.getElementById('menubar').style.width === '70px'){
            newWidthMenubar = '13%';
            newWidthMainContent = '86%';
            newDisplay = 'block';
        }else{
            newWidthMenubar = '70px';
            newWidthMainContent = '95.5%';
            newDisplay = 'none';
        }

        menubar.style.width = newWidthMenubar;
        
        document.getElementById('menu-item-text-home').style.display = newDisplay;
        document.getElementById('menu-item-text-dashboard').style.display = newDisplay;
        document.getElementById('menu-item-text-upload').style.display = newDisplay;
        document.getElementById('menu-item-text-profile').style.display = newDisplay;
        document.getElementById('menu-item-text-logout').style.display = newDisplay;

        document.getElementById('mainContent').style.width = newWidthMainContent;
    }


    return (
        <>
            <div className='navbar' id='navbar'>
                <img src={bars} className='menu-icons' onClick={toggleSidebar}></img>
                <p className='navbar-text'>Streamer</p>
            </div>

            <div className='mainBody' id='mainBody'>
                <div className='menubar' id='menubar'>
                    <div className='a-menu-item'>
                        <div className='menu-item'>
                            <img src={home} className='menu-icons'></img>
                            <h4 className='menu-item-text' id='menu-item-text-home'>Home</h4>
                        </div>
                    </div>

                    <div className='a-menu-item'>
                        <div className='menu-item'>
                            <img src={dashboard} className='menu-icons'></img>
                            <h4 className='menu-item-text' id='menu-item-text-dashboard'>Dashboard</h4>
                        </div>
                    </div>

                    <div className='a-menu-item'>
                        <div className='menu-item'>
                            <img src={upload} className='menu-icons'></img>
                            <h4 className='menu-item-text' id='menu-item-text-upload'>Upload</h4>
                        </div>
                    </div>
                    
                    <div className='a-menu-item'>
                        <div className='menu-item'>
                            <img src={profile} className='menu-icons'></img>
                            <h4 className='menu-item-text' id='menu-item-text-profile'>Profile</h4>
                        </div>
                    </div>

                    <div className='a-menu-item'>
                        <div className='menu-item'>
                            <img src={logout} className='menu-icons'></img>
                            <h4 className='menu-item-text' id='menu-item-text-logout'>Logout</h4>
                        </div>
                    </div>
                </div>

                <div id='mainContent'>
                    <div className='mainContentActualPortion'>
                        <Outlet />
                    </div>
                </div>
            </div>
        </>
    );
}

export default Layout;