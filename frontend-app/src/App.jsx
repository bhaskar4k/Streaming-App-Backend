import { Routes, Route } from 'react-router-dom';
import React, { useState, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import { useNavigate } from 'react-router-dom';

import Home from './Page-Components/Home/Home.jsx';
import Dashboard from './Page-Components/Dashboard/Dashboard.jsx';
import Login from './Page-Components/Login/Login.jsx';
import Profile from './Page-Components/Profile/Profile.jsx';
import Error from './Error.jsx';
import AlertModal from './Page-Components/Common-Components/AlertModal/AlertModal.jsx';

import { EndpointWebsocket } from '../src/Environment/Endpoint.js';
import { Environment } from '../src/Environment/Environment.js';
import { logout, redirect_to_login } from './Common/Utils.js';

import './App.css';

let loadAlertModal = null;

function App() {
  const navigate = useNavigate();
  const [connected, setConnected] = useState(false);
  const [showAlertModal, setShowAlertModal] = useState(false);
  const [headerTextOfAlertModal, setHeaderTextOfAlertModal] = useState(null);
  const [bodyTextOfAlertModal, setBodyTextOfAlertModal] = useState(null);


  const setupWebSocket = useCallback(() => {
    const JWT = JSON.parse(localStorage.getItem("JWT"));

    if (!JWT) {
      redirect_to_login(navigate);
      return null;
    }

    const client = new Client({
      brokerURL: EndpointWebsocket.authentication_websocket + EndpointWebsocket.get_websocket_emit,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);

        client.subscribe(EndpointWebsocket.get_logout_emit + JWT.device_endpoint, (response) => {
          const deserializedObject = JSON.parse(response.body);

          if (deserializedObject.data === "logout_" + JWT.device_endpoint) {
            openAlertModal(Environment.alert_modal_header_logout, deserializedObject.message);

            loadAlertModal = setTimeout(() => {
              closeAlertModal();
              logout(navigate);
            }, 5000);
          }
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
      onWebSocketError: (error) => {
        console.error('Error with websocket', error);
        setConnected(false);
      },
      onDisconnect: () => {
        setConnected(false);
      }
    });

    client.activate();
    return client;
  }, [navigate]);

  React.useEffect(() => {
    const client = setupWebSocket();

    return () => {
      if (client && client.active) {
        client.deactivate();
      }
    };
  }, [setupWebSocket]);

  function openAlertModal(header_text, body_text) {
    setHeaderTextOfAlertModal(header_text);
    setBodyTextOfAlertModal(body_text);
    setShowAlertModal(true);
  }

  function closeAlertModal() {
    setShowAlertModal(false);
    setHeaderTextOfAlertModal(null);
    setBodyTextOfAlertModal(null);

    clearTimeout(loadAlertModal);
    loadAlertModal = null;
    logout(navigate);
  }

  return (
    <>
      <AlertModal showModal={showAlertModal} handleClose={closeAlertModal} headerText={headerTextOfAlertModal} bodyText={bodyTextOfAlertModal} alertColor={Environment.colorError} />
      <Routes>
        <Route index element={<Home />} />
        <Route path="/home" element={<Home />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/login" element={<Login />} />
        <Route path="*" element={<Error />} />
        <Route path="/error" element={<Error />} />
      </Routes>
    </>
  );
}

export default App;