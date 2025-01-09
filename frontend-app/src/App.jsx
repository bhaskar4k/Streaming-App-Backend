import { Routes, Route } from 'react-router-dom';
import React, { useState, useEffect } from 'react';
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

  const [stompClient, setStompClient] = useState(null);
  const [connected, setConnected] = useState(false);
  const [name, setName] = useState('');

  const JWT = JSON.parse(localStorage.getItem("JWT"));

  if (stompClient) {
    try {
      stompClient.activate();
    } catch (error) {
      console.error('Connection error:', error);
    }
  }

  useEffect(() => {
    if (JWT === undefined || JWT === null) {
      navigate(`/login`);
    }

    const client = new Client({
      brokerURL: EndpointWebsocket.authentication_websocket + EndpointWebsocket.get_websocket_emit,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);

        client.subscribe(EndpointWebsocket.get_logout_emit+"/u1/d1", (response) => {
          const deserializedObject = JSON.parse(response.body);
          console.log("removed user - ", deserializedObject);
          localStorage.removeItem("JWT");
          window.alert("New login detected from another device.")
          navigate(`/login`);
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

    setStompClient(client);

    return () => {
      if (client.active) {
        client.deactivate();
      }
    };
  }, []);

  // const disconnect = () => {
  //     if (stompClient) {
  //         stompClient.deactivate();
  //     }
  //     setConnected(false);
  // };

  // Call this function to send message to websocket
  // const sendName = () => {
  //     if (stompClient && stompClient.active) {
  //         stompClient.publish({
  //             destination: EndpointWebsocket.emit_data,
  //             body: name,
  //         });
  //     }
  // };

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
  )
}

export default App;
