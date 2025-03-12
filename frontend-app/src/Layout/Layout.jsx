import React from 'react';
import './Layout.css';
import { Outlet } from "react-router-dom";
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DashboardService } from '../Service/DashboardService';
import { GenerateMenu } from '../Common/MenuGenerator';

import bars from '../../public/Images/bars.svg';
import home from '../../public/Images/home.svg';
import dashboard from '../../public/Images/dashboard.svg';
import upload from '../../public/Images/upload.svg';
import profile from '../../public/Images/profile.svg';
import logout from '../../public/Images/logout.svg';
import manage from '../../public/Images/manage.svg';
import uploaded_video from '../../public/Images/uploaded_video.svg';
import deleted_video from '../../public/Images/delete.svg';

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
        manage: manage,
        uploaded_video,
        deleted_video
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

            document.getElementById("root_menu").innerHTML = GenerateMenu(response.data, iconMap);
        } catch (error) {
            console.error("Error:", error);
            Alert(Environment.alert_modal_header_video_info_upload, Environment.colorError, "Failed to upload video info.");
        }
    }


    window.toggleSubmenu = function (parentId, route) {
        if (route !== 'null') navigate(route);

        const subMenuParent = document.getElementById(`submenu-${parentId}`);
        const subMenus = document.getElementsByClassName('a-menu-item-child');

        if (subMenuParent.style.maxHeight && subMenuParent.style.maxHeight !== "0px") {
            subMenuParent.style.maxHeight = "0px";
        } else {
            subMenuParent.style.maxHeight = subMenuParent.scrollHeight + "px";
        }

        let subMenuMarginLeft = (document.getElementById('menubar').style.width === '70px') ? "0px" : "20px";

        for (let i = 0; i < subMenus.length; i++) {
            subMenus[i].style.marginLeft = subMenuMarginLeft;
        }
    };


    window.navigateTo = function (route) {
        navigate(route);
    }


    function toggleSidebar() {
        if (windowWidth < 1200) return;

        const menubar = document.getElementById('menubar');
        var subMenus = document.getElementsByClassName('a-menu-item-child');

        let newWidthMenubar, newWidthMainContent, newDisplay, marginLeftSubmenu;
        if (document.getElementById('menubar').style.width === '70px') {
            newWidthMenubar = '13%';
            newWidthMainContent = '86%';
            newDisplay = 'block';
            marginLeftSubmenu = "20px";
            setToogleStatus(1);
        } else {
            newWidthMenubar = '70px';
            newWidthMainContent = 'calc(100% - 85px)';
            newDisplay = 'none';
            marginLeftSubmenu = "0px";
            setToogleStatus(0);           
        }

        menubar.style.width = newWidthMenubar;

        const mainContent = document.getElementById("mainContent");
        if (mainContent) mainContent.style.width = newWidthMainContent;

        elements.forEach((id) => {
            const elem = document.getElementById(id);
            if (elem) elem.style.display = newDisplay;
        });

        for (let i = 0; i < subMenus.length; i++) {
            subMenus[i].style.marginLeft = marginLeftSubmenu;
        }
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
                    <div id="root_menu"></div>
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