import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';

function Home() {
    const [stompClient, setStompClient] = useState(null);
    const [connected, setConnected] = useState(false);
    const [name, setName] = useState('');

    if (stompClient) {
        try {
            stompClient.activate();
        } catch (error) {
            console.error('Connection error:', error);
        }
    }

    useEffect(() => {
        const client = new Client({
            brokerURL: 'ws://localhost:8090/authentication-websocket',
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: (frame) => {
                setConnected(true);
                console.log('Connected: ' + frame);
                client.subscribe('/topic/logout', (response) => {
                    const deserializedObject = JSON.parse(response.body);
                    console.log(deserializedObject)
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
    //             destination: '/app/send-message',
    //             body: name,
    //         });
    //     }
    // };

    return (
        <div>
            <h1>HOME</h1>
        </div>
    );
}

export default Home;