import React from 'react';
import './Layout.css';
import { Outlet } from "react-router-dom";
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DashboardService } from '../Service/DashboardService';

import bars from '../../public/Images/bars.svg';
import home from '../../public/Images/home.svg';
import dashboard from '../../public/Images/dashboard.svg';
import upload from '../../public/Images/upload.svg';
import profile from '../../public/Images/profile.svg';
import logout from '../../public/Images/logout.svg';
import manage from '../../public/Images/manage.svg';

function Layout() {
    const navigate = useNavigate();

    const [windowWidth, setWindowWidth] = useState(window.innerWidth);
    const [previousWindowWidth, setPreviousWindowWidth] = useState(window.innerWidth);
    const [toogleStatus, setToogleStatus] = useState(0);
    const [elements, setElements] = useState([]);
    const [layout, setLayout] = useState([]);

    const dashboardService = new DashboardService();

    const iconMap = {
        home: home,
        dashboard: dashboard,
        upload: upload,
        profile: profile,
        logout: logout,
        manage: manage
    };

    useEffect(() => {
        getLeftSideMenu();
    }, []);


    async function getLeftSideMenu() {
        try {
            let response = await dashboardService.DoGetMenu();
            setLayout(response.data);

            const newElements = response.data.map(item => item.menu_name_id);
            setElements(newElements);
        } catch (error) {
            console.error("Error:", error);
            Alert(Environment.alert_modal_header_video_info_upload, Environment.colorError, "Failed to upload video info.");
        }
    }


    function toggleSidebar() {
        if (windowWidth < 1200) return;

        const menubar = document.getElementById('menubar');

        let newWidthMenubar, newWidthMainContent, newDisplay;
        if (document.getElementById('menubar').style.width === '70px') {
            newWidthMenubar = '13%';
            newWidthMainContent = '86%';
            newDisplay = 'block';
            setToogleStatus(1);
        } else {
            newWidthMenubar = '70px';
            newWidthMainContent = 'calc(100% - 85px)';
            newDisplay = 'none';
            setToogleStatus(0);
        }

        menubar.style.width = newWidthMenubar;

        const mainContent = document.getElementById("mainContent");
        if (mainContent) mainContent.style.width = newWidthMainContent;

        elements.forEach((id) => {
            const elem = document.getElementById(id);
            if (elem) elem.style.display = newDisplay;
        });
    }


    useEffect(() => {
        const handleResize = () => {
            setWindowWidth(window.innerWidth);
        };

        window.addEventListener("resize", handleResize);

        return () => {
            window.removeEventListener("resize", handleResize);
        };
    }, []);


    useEffect(() => {
        const menubar = document.getElementById("menubar");
        let newWidthMenubar, newWidthMainContent, newDisplay;

        if (windowWidth >= 1200) {
            if (previousWindowWidth >= 1200 || !toogleStatus) {
                setPreviousWindowWidth(windowWidth);
                return;
            }
            newWidthMenubar = "13%";
            newWidthMainContent = "86%";
            newDisplay = "block";
        } else {
            if (previousWindowWidth < 1200) {
                setPreviousWindowWidth(windowWidth);
                return;
            }
            newWidthMenubar = '70px';
            newWidthMainContent = 'calc(100% - 85px)';
            newDisplay = "none";
        }

        if (menubar) menubar.style.width = newWidthMenubar;

        elements.forEach((id) => {
            const elem = document.getElementById(id);
            if (elem) elem.style.display = newDisplay;
        });

        const mainContent = document.getElementById("mainContent");
        if (mainContent) mainContent.style.width = newWidthMainContent;

        setPreviousWindowWidth(windowWidth);
    }, [windowWidth]);


    return (
        <>
            <div className='navbar' id='navbar'>
                <img src={bars} className='menu-icons toggleMenu' onClick={toggleSidebar}></img>
                <p className='navbar-text'>Streamer</p>
            </div>

            <div className='mainBody' id='mainBody'>
                <div className='menubar' id='menubar'>

                    {layout.map((item) => (
                        <div key={item.id} className="a-menu-item" onClick={() => navigate(item.route_name)}>
                            <div className="menu-item">
                                <img src={iconMap[item.menu_icon]} className="menu-icons" alt={item.menu_icon} />
                                <h4 className="menu-item-text" id={item.menu_name_id}>{item.menu_name}</h4>
                            </div>
                        </div>
                    ))}
                </div>

                <div id='mainContent' className='mainContent'>
                    <div className='mainContentActualPortion'>
                        <div className='mainContentActualPortionChild'>
                            <Outlet />
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default Layout;