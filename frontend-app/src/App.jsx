import { Routes, Route } from 'react-router-dom';
import React, { useState, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import { useNavigate } from 'react-router-dom';

import Home from './Page-Components/Home/Home.jsx';
import Dashboard from './Page-Components/Dashboard/Dashboard.jsx';
import Login from './Page-Components/Login/Login.jsx';
import Profile from './Page-Components/Profile/Profile.jsx';
import Error from './Error.jsx';

import { EndpointWebsocket } from '../src/Environment/Endpoint.js';

import './App.css';

function App() {
  const navigate = useNavigate();
  const [connected, setConnected] = useState(false);

  const setupWebSocket = useCallback(() => {
    const JWT = JSON.parse(localStorage.getItem("JWT"));
    
    if (!JWT) {
      navigate(`/login`);
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

        client.subscribe(EndpointWebsocket.get_logout_emit + JWT.logout_ws_endpoint, (response) => {
          const deserializedObject = JSON.parse(response.body);

          if (deserializedObject.message === "logout_for_maximum_device_reached" && deserializedObject.data === JWT.logout_ws_endpoint) {        
            window.alert("Logged in from another device. Logging out....")
            localStorage.removeItem("JWT");
            navigate(`/login`);
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

  return (
    <>
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